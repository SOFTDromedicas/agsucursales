package com.dromedicas.jaxrs.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.dromedicas.dao.HibernateSessionFactory;
import com.dromedicas.dao.SucursalesHome;
import com.dromedicas.dao.TipoincidenteHome;
import com.dromedicas.dao.VentadiariaglobalHome;
import com.dromedicas.dto.Incidente;
import com.dromedicas.dto.Sucursales;
import com.dromedicas.dto.Tipoincidente;
import com.dromedicas.dto.Ventadiariaglobal;
import com.dromedicas.servicio.EnviarMailAlertas;
import com.dromedicas.servicio.EnviarSms;
import com.dromedicas.servicio.NotificacionService;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class ClienteVentasAlInstante implements Job {

	Logger log = Logger.getLogger(ClienteVentasAlInstante.class);
	// Servicio Ventas al instante
	private String servicio = "wsjson/ventainstante";

	/**
	 * Ejecuta el trabajo programado por el schedule de Ventas
	 * al Instante.
	 */
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// Obtiene todas las Sucursales
		SucursalesHome sucursalesHome = new SucursalesHome();// Objeto DAO para
																// sucursales
		VentadiariaglobalHome ventaDiaraHome = new VentadiariaglobalHome();
		log.info("Obteniendo Sucursales");
		List<Sucursales> sucursalList = sucursalesHome.findAll();
		System.out.println("Total Sucursales: " + sucursalList.size());
		

		// Itera Todas las sucursales
		for (Sucursales sucursal : sucursalList) {
			// Revisa si la sucursal es 24 horas
			if (sucursal.getEs24horas().trim().equals("true")) {
				try {
					System.out.println("Consume servicio para la Sucursal: " + sucursal.getDescripcion() );
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
							if (ventaAnterior != null) {
								log.info("Elimina valor actual");
								ventaDiaraHome.eliminarVentaDiaraGlobal(ventaAnterior);
							}
							// Perisite el(los) nuevo(s) objeto(s)
							// Ventadiariaglobal
							System.out.println("Grabando el nuevo Valor valor actual");
							ventaDiaraHome.guardarVentaDiaraGlobal(e);
						}
					}
					//aca se debe validar si hay incidentes abiertos para el cliente actual y cerrarlos
					cerrarIncidentes(sucursal);
				} catch (Exception e) {
					//Manejo de error en la consulta del ws
					enviarNotificaciones(sucursal);
					log.info("Error en la conexion para la sucursal:  " + sucursal.getDescripcion() + " | "
							+ sucursal.getRutaweb() + servicio);
					e.printStackTrace();
				}
			} else {
				// revisa si la hora actual esta entre la hora de apertura y cierre +1
				try {
					if ( estaAbierta(sucursal) ) {
						
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
								if (ventaAnterior != null) {
									log.info("Elimina valor actual");
									ventaDiaraHome.eliminarVentaDiaraGlobal(ventaAnterior);
								}
								// Perisite el(los) nuevo(s) objeto(s) Ventadiariaglobal
								System.out.println("Grabando el nuevo Valor valor actual");
								ventaDiaraHome.guardarVentaDiaraGlobal(e);
							}
						}
						//aca se debe validar si hay incidentes abiertos para el cliente actual y cerrarlos
						cerrarIncidentes(sucursal);
					}
				} catch (Exception e) {
					enviarNotificaciones(sucursal);
					System.out.println("Error en la conexion para la sucursal:  " + sucursal.getDescripcion() + " | "
							+ sucursal.getRutaweb() + servicio);
					e.printStackTrace();
				}
			} // fin del else ppal

			System.out.println();
		} // fin del for que itera las sucursales

		HibernateSessionFactory.stopSessionFactory();
		sucursalesHome = null;
		ventaDiaraHome = null;
	}

	/**
	 * Retorna una List de objetos <code>Ventadiariaglobal</code>
	 * correspondientes a las ventas de una sucursal por vendedor.
	 * 
	 * @param url
	 * @return
	 */
	private List<Ventadiariaglobal> obtenertWSVentaAlInstante(Sucursales sucursal) {
		List<Ventadiariaglobal> ventaList = new ArrayList<Ventadiariaglobal>();
		Client client = Client.create();
		WebResource webResource = client.resource(sucursal.getRutaweb() + this.servicio);
		VentaAlInstanteWrap response = webResource.accept("application/json").get(VentaAlInstanteWrap.class);
		try {
			List<VentaAlInstanteDetalle> detalle = response.getMessage().getData();
			if (detalle != null) {
				if (!detalle.isEmpty()) {
					for (VentaAlInstanteDetalle e : detalle) {
						Ventadiariaglobal venta = new Ventadiariaglobal();
						venta.setCodsucursal(sucursal.getCodigo());
						venta.setDiaoperativo(e.getDiaoperativo());
						venta.setVendedor(e.getVendedor());
						if (e.getVtaespecial() != null)
							venta.setVentaespe(new Double(e.getVtaespecial().trim()));
						venta.setVentagen(new Double(e.getVtageneral()));
						venta.setUltactualizacion(new Date());
						ventaList.add(venta);
					}
				} // fin del if empty
			}else{
				log.info("No hay informacion de ventas para el dia operativo actual");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ventaList;
	}

	/**
	 * Valida si la sucursal actual se encuentra abierte con base en el horario
	 * de la misma. Recibe como parametro un objeto <code>Sucursales</code>. Ver
	 * mas informacion en la clase {@link com.dromedicas.dto.Sucursales}
	 * 
	 * @param instance
	 * @return
	 * @throws ParseException
	 */
	public boolean estaAbierta(Sucursales instance) throws ParseException {
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

		// validacion del dia de la semana
		Calendar cal = Calendar.getInstance();
		cal.setTime(diaOperativo);
		int diaSemana = cal.get(Calendar.DAY_OF_WEEK);
		if (diaSemana >= 2 && diaSemana <= 7) {

			if (horaActual.get(Calendar.HOUR_OF_DAY) > aperturaN.get(Calendar.HOUR_OF_DAY)
					&& horaActual.get(Calendar.HOUR_OF_DAY) < (cierreN.get(Calendar.HOUR_OF_DAY) + 1)) {
				abierto = true;
			}

			if (horaActual.get(Calendar.HOUR_OF_DAY) == aperturaN.get(Calendar.HOUR_OF_DAY)) {
				if (horaActual.get(Calendar.MINUTE) >= aperturaN.get(Calendar.MINUTE))
					abierto = true;
			}

			if (horaActual.get(Calendar.HOUR_OF_DAY) == (cierreN.get(Calendar.HOUR_OF_DAY) + 1)) {
				if (horaActual.get(Calendar.MINUTE) >= cierreN.get(Calendar.MINUTE))
					abierto = true;
			}
		} else {// Domingos
			if (horaActual.get(Calendar.HOUR_OF_DAY) > aperturaEs.get(Calendar.HOUR_OF_DAY)
					&& horaActual.get(Calendar.HOUR_OF_DAY) < (cierreEs.get(Calendar.HOUR_OF_DAY) + 1)) {
				abierto = true;
			}

			if (horaActual.get(Calendar.HOUR_OF_DAY) == aperturaEs.get(Calendar.HOUR_OF_DAY)) {
				if (horaActual.get(Calendar.MINUTE) >= aperturaEs.get(Calendar.MINUTE))
					abierto = true;
			}

			if (horaActual.get(Calendar.HOUR_OF_DAY) == (cierreEs.get(Calendar.HOUR_OF_DAY) + 1)) {
				if (horaActual.get(Calendar.MINUTE) >= cierreEs.get(Calendar.MINUTE))
					abierto = true;
			}
		} // fin del else ppal

		return true;
	}
	
	/**
	 * Envia notificaciones SMS y Email si no se esta actualizando
	 * las ventas por periodos mayor a una hora
	 * @param instance
	 */
	private void enviarNotificaciones(Sucursales sucursal) {
		// busca la ultima actualizacion de la sucursal
		log.info("Ingrese a enviar Notificaciones");
		String incidente = "Falla Ventas Al Instante";
		Incidente inci = null;
		try{
			//busca si el incidente esta creado
			NotificacionService notificacion = new NotificacionService();
			inci = notificacion.existeIncidente(sucursal.getDescripcion(), incidente );
			System.out.println(">>>Se Hallo incidente Registrado para el cliente actual: " + (inci != null));
			if( inci !=  null){				
				notificacion.enviarNotificacion(inci, sucursal);				
			}else{
				//crea un nuevo incidente
				TipoincidenteHome tipoInHome = new TipoincidenteHome();
				//obtiene el tipo de incidente
				Tipoincidente tipoIncidente = tipoInHome.obtenerTipoIncidente(incidente);
				System.out.println("--Tipo Incidente: " + tipoIncidente.getNombreincidente());
				Incidente nuevoIncidente = new Incidente();
				nuevoIncidente.setTipoincidente( tipoIncidente);
				nuevoIncidente.setCliente(sucursal.getDescripcion());
				nuevoIncidente.setOcurrencia(new Date());
				//registra el nuevo incidente
				notificacion.registrarIncidente(nuevoIncidente);				
			}
			
			
		} catch (Exception e) {
			log.error("Error al obtener la ultima actualizacion" + e.getMessage());
		}

	}
	
	
	public void cerrarIncidentes(Sucursales sucursal){
		log.info("Ingrese a Cerrar Incidentes");
		String incidenteNombre = "Falla Ventas Al Instante";
		Incidente incidenteAbierto = null;
		try {
			//busca si el incidente esta creado
			NotificacionService notificacionService = new NotificacionService();
			incidenteAbierto = notificacionService.existeIncidente(sucursal.getDescripcion(), incidenteNombre );
			if(incidenteAbierto != null ){
				//si hay un incidente abierto es cerrado
				incidenteAbierto.setCierre(new Date());
				notificacionService.cerrarIncidente(incidenteAbierto);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	
	
	
	

}
