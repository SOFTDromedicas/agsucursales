package com.dromedicas.test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.dromedicas.dao.HibernateSessionFactory;
import com.dromedicas.dao.SucursalesHome;
import com.dromedicas.dao.VentadiariaglobalHome;
import com.dromedicas.dto.Sucursales;
import com.dromedicas.dto.Ventadiariaglobal;
import com.dromedicas.jaxrs.service.ClienteVentasAlInstante;
import com.dromedicas.jaxrs.service.VentaAlInstanteDetalle;
import com.dromedicas.jaxrs.service.VentaAlInstanteWrap;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class TestVentasAldia {
	static Logger log = Logger.getLogger(TestVentasAldia.class);

	// Servicio Ventas al instante
	static String servicio = "wsjson/ventainstante";

	public static void main(String args[]) {
		// Obtiene todas las Sucursales
		SucursalesHome sucursalesHome = new SucursalesHome();// Objeto DAO para
																// sucursales
		VentadiariaglobalHome ventaDiaraHome = new VentadiariaglobalHome();
		log.info("Obteniendo Sucursales");
		List<Sucursales> sucursalList = sucursalesHome.findAll();
		System.out.println("Total Sucursales: " + sucursalList.size());
		Logger log = Logger.getLogger(ClienteVentasAlInstante.class);

		// Itera Todas las sucursales
		for (Sucursales sucursal : sucursalList) {
			System.out.println(sucursal.getEs24horas().trim().equals("true"));
			// Revisa si la sucursal es 24 horas
			if (sucursal.getEs24horas().trim().equals("true")) {
				try {
					System.out.println("Consume servicio para la Sucursal: " + sucursal.getDescripcion());
					// consume servicio
					List<Ventadiariaglobal> ventasActualList = obtenertWSVentaAlInstante(sucursal);
					System.out.println("Tamanio Actual de elementos: " + ventasActualList.size());
					if (ventasActualList != null) {
						// Itera ventasActualList
						// Busca si existen valores para el dia operativo
						// actual, y los elimina
						System.out.println("Buscando valores para el dia operativo actual");
						for (Ventadiariaglobal e : ventasActualList) {
							 Ventadiariaglobal ventaAnterior = (Ventadiariaglobal) ventaDiaraHome
									.getVentasDiaActual(e.getCodsucursal(), e.getDiaoperativo(), e.getVendedor());
									
							// elimina los registros actuales
							if (ventaAnterior != null){
								log.info("Elimina valor actual");
								ventaDiaraHome.eliminarVentaDiaraGlobal(ventaAnterior);
							}
							// Perisite el(los) nuevo(s) objeto(s)
							// Ventadiariaglobal
							System.out.println("Grabando el nuevo Valor valor actual");
							ventaDiaraHome.guardarVentaDiaraGlobal(e);
						}
					}

				} catch (Exception e) {
					System.out.println("Error en la conexion para la sucursal:  " + sucursal.getDescripcion() + " | "
							+ sucursal.getRutaweb() + servicio);
					System.out.println(e.getMessage());
					//com.sun.jersey.api.client.ClientHandlerException
					//java.net.ConnectException: Connection timed out: connect
					
				}
				
			} else {
				// revisa si la hora actual esta entre la hora de apertura y +1
				try {
					boolean abierto = estaAbierta(sucursal);
					if(abierto){
						System.out.println("Consume servicio para la Sucursal: " + sucursal.getDescripcion());
						// consume servicio
						List<Ventadiariaglobal> ventasActualList = obtenertWSVentaAlInstante(sucursal);
						System.out.println("Tamanio Actual de elementos: " + ventasActualList.size());
						if (ventasActualList != null) {
							// Itera ventasActualList
							// Busca si existen valores para el dia operativo
							// actual, y los elimina
							System.out.println("Buscando valores para el dia operativo actual");
							for (Ventadiariaglobal e : ventasActualList) {
								 Ventadiariaglobal ventaAnterior = (Ventadiariaglobal) ventaDiaraHome
										.getVentasDiaActual(e.getCodsucursal(), e.getDiaoperativo(), e.getVendedor());
										
								// elimina los registros actuales
								if (ventaAnterior != null){
									log.info("Elimina valor actual");
									ventaDiaraHome.eliminarVentaDiaraGlobal(ventaAnterior);
								}
								// Perisite el(los) nuevo(s) objeto(s)
								// Ventadiariaglobal
								System.out.println("Grabando el nuevo Valor valor actual");
								ventaDiaraHome.guardarVentaDiaraGlobal(e);
							}
						}
					}
				} catch (ParseException e) {					
					System.out.println("Error en la conexion para la sucursal:  " + sucursal.getDescripcion() + " | "
							+ sucursal.getRutaweb() + servicio);
					System.out.println(e.getMessage());
				}	
				
			}//fin del else ppal
			
			System.out.println();
		} // fin del for que itera las sucursales

		
		// finalizada la iteracion de la sucursales cierra la conexion a la base de datos
		HibernateSessionFactory.stopSessionFactory();
		sucursalesHome = null;
		ventaDiaraHome = null;

	}

	/**
	 * Retorna una List de objetos <code>Ventadiariaglobal</code>
	 * correspondientes a las ventas de una sucursal por vendedor.	 * 
	 * @param url
	 * @return
	 */
	private static List<Ventadiariaglobal> obtenertWSVentaAlInstante(Sucursales sucursal) {
		List<Ventadiariaglobal> ventaList = new ArrayList<Ventadiariaglobal>();
		Client client = Client.create();
		WebResource webResource = client.resource(sucursal.getRutaweb() + servicio);
		VentaAlInstanteWrap response = webResource.accept("application/json").get(VentaAlInstanteWrap.class);
		try {
			List<VentaAlInstanteDetalle> detalle = response.getMessage().getData();
			if (!detalle.isEmpty()) {
				for (VentaAlInstanteDetalle e : detalle) {
					Ventadiariaglobal venta = new Ventadiariaglobal();
					venta.setCodsucursal(sucursal.getCodigo());
					venta.setDiaoperativo(e.getDiaoperativo());
					venta.setVendedor(e.getVendedor());
					if(e.getVtaespecial() !=  null )
							venta.setVentaespe(new Double(e.getVtaespecial().trim()));
					venta.setVentagen(new Double(e.getVtageneral()));
					System.out.println("Sucursal persistente: " + venta.getCodsucursal() + " - " + venta.getVendedor());
					ventaList.add(venta);
				}
			} // fin del if empty
		} catch (Exception e) {
			e.printStackTrace();;
		}
		
		return ventaList;
	}
	
	
	/**
	 * Valida si la sucursal actual se encuentra abierte con 
	 * base en el horario de la misma. Recibe como parametro
	 * un objeto <code>Sucursales</code>.
	 * Ver mas informacion en la clase {@link com.dromedicas.dto.Sucursales}
	 * @param instance
	 * @return
	 * @throws ParseException 
	 */
	public static boolean estaAbierta(Sucursales instance) throws ParseException {
		boolean abierto = false;
		Date currentDate = new Date();
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date diaOperativo = format.parse(instance.getDiaoperativo());
		
		Calendar aperturaN = Calendar.getInstance();
		aperturaN.setTime(instance.getHoraperturagen());
				
		Calendar cierreN = Calendar.getInstance();
		cierreN.setTime(instance.getHoracierregen());
		
		Calendar aperturaEs = Calendar.getInstance();
		aperturaEs.setTime(instance.getHoraperturaes());
		
		Calendar cierreEs = Calendar.getInstance();
		cierreEs.setTime(instance.getHoracierrees());
		
		Calendar horaActual = Calendar.getInstance();
		horaActual.setTime(currentDate);
		
		//validacion del dia de la semana
		Calendar cal = Calendar.getInstance();
		cal.setTime(diaOperativo);
		int diaSemana = cal.get(Calendar.DAY_OF_WEEK);
		if( diaSemana >= 2 && diaSemana <= 7){
			
			if( horaActual.get(Calendar.HOUR_OF_DAY) > aperturaN.get(Calendar.HOUR_OF_DAY) && 
					horaActual.get(Calendar.HOUR_OF_DAY) < (cierreN.get(Calendar.HOUR_OF_DAY)+1) ){
				abierto = true;
			}
			
			if(horaActual.get(Calendar.HOUR_OF_DAY) == aperturaN.get(Calendar.HOUR_OF_DAY) ){
				if(horaActual.get(Calendar.MINUTE) >= aperturaN.get(Calendar.MINUTE))
					abierto = true;
			}
			
			if(horaActual.get(Calendar.HOUR_OF_DAY) == (cierreN.get(Calendar.HOUR_OF_DAY)+1) ){
				if(horaActual.get(Calendar.MINUTE) >= cierreN.get(Calendar.MINUTE))
					abierto = true;
			}
		}else{//Domingos
			if( horaActual.get(Calendar.HOUR_OF_DAY) > aperturaEs.get(Calendar.HOUR_OF_DAY) && 
					horaActual.get(Calendar.HOUR_OF_DAY) < (cierreEs.get(Calendar.HOUR_OF_DAY)+1) ){
				abierto = true;
			}
			
			if(horaActual.get(Calendar.HOUR_OF_DAY) == aperturaEs.get(Calendar.HOUR_OF_DAY) ){
				if(horaActual.get(Calendar.MINUTE) >= aperturaEs.get(Calendar.MINUTE))
					abierto = true;
			}
			
			if(horaActual.get(Calendar.HOUR_OF_DAY) == (cierreEs.get(Calendar.HOUR_OF_DAY)+1) ){
				if(horaActual.get(Calendar.MINUTE) >= cierreEs.get(Calendar.MINUTE))
					abierto = true;
			}			
		}//fin del else ppal
				
		return true;
	}
	
	
	
	
}
