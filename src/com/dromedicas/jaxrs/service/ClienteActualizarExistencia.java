package com.dromedicas.jaxrs.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.dromedicas.dao.ExistenciasHome;
import com.dromedicas.dao.HibernateSessionFactory;
import com.dromedicas.dao.SucursalesHome;
import com.dromedicas.dao.TipoincidenteHome;
import com.dromedicas.dto.Existencias;
import com.dromedicas.dto.ExistenciasId;
import com.dromedicas.dto.Incidente;
import com.dromedicas.dto.Sucursales;
import com.dromedicas.dto.Tipoincidente;
import com.dromedicas.dto.Ventadiariaglobal;
import com.dromedicas.servicio.NotificacionService;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class ClienteActualizarExistencia implements Job {
	

	Logger log = Logger.getLogger(ClienteActualizarExistencia.class);
	// Servicio Existencia acutal general
	private String servicio = "wsjson/exiactualgeneral";
	Integer bodegaActual;
		
	/**
	 * Ejecuta la tarea programada para el trabajo
	 * Actualizacion de existencias
	 */
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		//Objeto DAO encargado de operaciones CRUD para la tabla sucursales
		SucursalesHome sucursalHome = new SucursalesHome();
		
		log.info("Obteniendo Sucursales");
		//Listado de obejetos sucursales
		List<Sucursales> sucursalList = sucursalHome.findAll();
		log.info("Total Sucursales Existencias: " + sucursalList.size());
		
		ExistenciasHome exisHome = new ExistenciasHome();
		long tInicio = System.currentTimeMillis();
		//Recorre las sucursales
		for(Sucursales sucursal : sucursalList){
			log.info("Consume servicio Existencias para la Sucursal: " + sucursal.getDescripcion() );	
			long t1 = System.currentTimeMillis();
			if (sucursal.getEs24horas().trim().equals("true")) {				
				try {					
					//Consume el servicio
					List<Existencias> existenciaList = obtenertWSExistencia(sucursal);
					log.info("Bodega Actual: " + this.bodegaActual);
					//ubica la bodega actual y coloca las existencias en cero
					exisHome.existenciaBodegaoACero(this.bodegaActual);	
					log.info("Actualizando existencias de productos");
					for(Existencias exisProducto: existenciaList){
						//actualiza los productos actuales con los valores recibidos del ws						
						exisHome.actualizarExistneciaProducto(exisProducto);						
					}					
					log.info("Total de productos actualizados: " + existenciaList.size());
					cerrarIncidentes(sucursal);
				} catch (Exception e) {
					// TODO: handle exception
					enviarNotificaciones(sucursal);
					e.printStackTrace();
				}	
			}else{				
				try {
					if(estaAbierta(sucursal) ){
						//Consume el servicio
						List<Existencias> existenciaList = obtenertWSExistencia(sucursal);
						log.info("Bodega Actual: " + this.bodegaActual);
						//ubica la bodega actual y coloca las existencias en cero
						exisHome.existenciaBodegaoACero(this.bodegaActual);		
						log.info("Actualizando existencias de productos");
						for(Existencias exisProducto: existenciaList){
							//actualiza los productos actuales con los valores recibidos del ws						
							exisHome.actualizarExistneciaProducto(exisProducto);						
						}	
						log.info("Total de productos actualizados: " + existenciaList.size());
						cerrarIncidentes(sucursal);
					}	
				} catch (ParseException e) {					
					enviarNotificaciones(sucursal);
					e.printStackTrace();
				}	
			}	
			long t2 = (System.currentTimeMillis() - t1) / 1000;
            String sec = "";
            if (t2 > 1 || t2 == 0) {
                sec = " segundos";
            } else {
                sec = " segundo";
            }
            
            log.info( "Tiempo de Ejecucion proceso:" + t2 + sec);		
		}//fin del for que itera sucursales
		
		long tFinal = (System.currentTimeMillis() - tInicio) / 1000;
        log.info(">>>>****Tiempo Total del proceso: " + (tFinal/60) + " minutos");
		
		HibernateSessionFactory.stopSessionFactory();
		sucursalHome = null;		
		this.bodegaActual = null;
	}
	
	
	/**
	 * Retorna un List de objetos <code>Existencias</code>
	 * correspondientes a las existencias actuales en la 
	 * sucursal recibida como argumento.
	 * 
	 * @param url
	 * @return
	 */
	private List<Existencias> obtenertWSExistencia( Sucursales sucursal ){
		//Lista con los prodctos con las existencias recibidas del ws
		List<Existencias> existenciasList = new ArrayList<Existencias>();
		//cliente Jersey- consume el WS
		log.info("Servicio Existencias: " + sucursal.getDescripcion() + " | " + sucursal.getRutaweb() + this.servicio);
		
		Client client = Client.create();
		WebResource webResource = client.resource(sucursal.getRutaweb() + this.servicio);
		ExistenciaActualWrap response = webResource.accept("application/json").get(ExistenciaActualWrap.class);
	
		try {
			//este list viene del WS "data:[]"
			List<ExistenciaActualDetalle> detalle = response.getMessage().getData();
			if (detalle != null) {
				if (!detalle.isEmpty()) {
					//aca se establece la bodega actual
					this.bodegaActual = Integer.parseInt(detalle.get(0).getBodegaid());
					//itera los resultado y crea objetos dto para la tabla
					for (ExistenciaActualDetalle e : detalle) {
						//crea id de Existencia
						ExistenciasId id = new ExistenciasId();
						id.setBodegaid( Integer.parseInt(e.getBodegaid()) );
						id.setProductoid(Long.parseLong(e.getProductoid()));
						//crea el registro de existencia
						Existencias exis = new Existencias();
						exis.setId(id);
						exis.setCantidad(Double.parseDouble(e.getCantidad()));
						exis.setUltcambio(new Date());
						//anade un nuevo registro de existencia al arreglo de rspta
						existenciasList.add(exis);						
					}
				} // fin del if empty
			}else{
				log.info("No hay informacion de existencias para la sucursal actual");
			}
		} catch (Exception e) {			
			e.printStackTrace();
		}
		//conjunto de registros de existencias del WS
		return existenciasList;
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

		// validacion del dia de la semana actual
		Calendar cal = Calendar.getInstance();
		cal.setTime(diaOperativo);
				
		int diaSemana = horaActual.get(Calendar.DAY_OF_WEEK);	
		
		
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
				if (horaActual.get(Calendar.MINUTE) < cierreN.get(Calendar.MINUTE))
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

		return abierto;
	}
	
	
	/**
	 * Envia notificaciones SMS y Email si no se esta actualizando
	 * las ventas por periodos mayor a una hora
	 * @param instance
	 */
	private void enviarNotificaciones(Sucursales sucursal) {
		// busca la ultima actualizacion de la sucursal
		log.info("Ingrese a enviar Notificaciones");
		String incidente = "Falla Existencias Globales";
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
	
	
	/**
	 * Valida la existencias de incidentes abiertos
	 * y actualiza su fecha de cierre
	 * @param sucursal
	 */
	public void cerrarIncidentes(Sucursales sucursal){
		log.info("Ingrese a Cerrar Incidentes");
		String incidenteNombre = "Falla Existencias Globales";
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