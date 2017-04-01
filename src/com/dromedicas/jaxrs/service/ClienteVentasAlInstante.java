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
import com.dromedicas.dao.VentadiariaglobalHome;
import com.dromedicas.dto.Sucursales;
import com.dromedicas.dto.Ventadiariaglobal;
import com.dromedicas.servicio.EnviarMailAlertas;
import com.dromedicas.servicio.EnviarSms;
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
		// HibernateSessionFactory.openSessionFactory();
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
					System.out.println("Consume servicio para la Sucursal: " + sucursal.getDescripcion() + " | "+
							sucursal.getRutaweb()+servicio);
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

				} catch (Exception e) {
					System.out.println("Error en la conexion para la sucursal:  " + sucursal.getDescripcion() + " | "
							+ sucursal.getRutaweb() + servicio);
					e.printStackTrace();
					//Manejo de error en la consulta del ws
					enviarNotificaciones(sucursal);
				}

			} else {
				// revisa si la hora actual esta entre la hora de apertura y +1
				try {
					boolean abierto = estaAbierta(sucursal);
					if (abierto) {
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
					}
				} catch (ParseException e) {
					System.out.println("Error en la conexion para la sucursal:  " + sucursal.getDescripcion() + " | "
							+ sucursal.getRutaweb() + servicio);
					e.printStackTrace();
					enviarNotificaciones(sucursal);
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
	private void enviarNotificaciones(Sucursales instance) {
		// busca la ultima actualizacion de la sucursal
		VentadiariaglobalHome ventaDiaraHome = new VentadiariaglobalHome();
		try {
			log.info("Enviando SMS a:");

			Date fechaActual = new Date();
			Date ultimaActualizacion = ventaDiaraHome.ultimaActualizacion(instance.getCodigo())
								.getUltactualizacion();

			if (ultimaActualizacion != null) {
				Calendar calFechaAct = Calendar.getInstance();
				calFechaAct.setTime(fechaActual);

				Calendar calUltAct = Calendar.getInstance();
				calUltAct.setTime(ultimaActualizacion);

				// obteniendo diferencia de dias
				long diff = fechaActual.getTime() - ultimaActualizacion.getTime();
				long diferencia = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

				System.out.println("Dias sin actualizar: " + diferencia);
				if (diferencia > 0) {
					log.info("Envia SMS por desconexion mayor a un dia");
					notificarSMS(instance.getDescripcion(), diferencia, ultimaActualizacion);
				}else{
					int difHoras = calFechaAct.get(Calendar.HOUR) - calUltAct.get(Calendar.HOUR) ;
					int difMinutes = calFechaAct.get(Calendar.MINUTE) - calUltAct.get(Calendar.MINUTE) ;
					if( difHoras > 0 ){
						notificarFallaEmail(instance, ultimaActualizacion);
					}
				}
			}
		} catch (Exception e) {
			log.error("Error al obtener la ultima actualizacion" + e.getMessage());
		}

	}
	
	
	
	private void notificarFallaEmail(Sucursales instance, Date ultActualizacion){
		EnviarMailAlertas.enviarEmailAlertaVentas(instance,ultActualizacion);
		
	}
	
	
	/**
	 * Envia una notificacion SMS sobre el proceso de Ventas al Instante
	 * con la informacion recibida como parametro
	 * @param instance
	 */
	private void notificarSMS(String sucursal, long diferencia, Date ultimaActualizacion) {
		String nroCel = "3102097474";

		EnviarSms.enviarSms(
				"Mensaje desde DROPOS. La sucursal " + sucursal + " no actualiza Ventas al instante desde hace "
						+ diferencia + " dia(s)." + " Ultima Actualizacion  " + ultimaActualizacion,
				nroCel);
	}
	

}
