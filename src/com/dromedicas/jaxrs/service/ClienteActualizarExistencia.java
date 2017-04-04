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
import com.dromedicas.dto.Existencias;
import com.dromedicas.dto.ExistenciasId;
import com.dromedicas.dto.Sucursales;
import com.dromedicas.dto.Ventadiariaglobal;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class ClienteActualizarExistencia implements Job {
	

	Logger log = Logger.getLogger(ClienteActualizarExistencia.class);
	// Servicio Existencia acutal general
	private String servicio = "wsjson/exiactualgeneral";
	Integer bodegaActual;
		

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		SucursalesHome sucursalHome = new SucursalesHome();
		log.info("Obteniendo Sucursales");
		List<Sucursales> sucursalList = sucursalHome.findAll();
		System.out.println("Total Sucursales Existencias: " + sucursalList.size());
		ExistenciasHome exisHome = new ExistenciasHome();
		
		//Recorre las sucursales
		for(Sucursales sucursal : sucursalList){
			System.out.println("Consume servicio Existencias para la Sucursal: " + sucursal.getDescripcion() );
			
			if (sucursal.getEs24horas().trim().equals("true")) {				
				try {
					System.out.println("Servicio Existencias -> Bodega Actual: " + this.bodegaActual);
					//Consume el servicio
					List<Existencias> existenciaList = obtenertWSExistencia(sucursal);
					//ubica la bodega actual
					//coloca las existencias en cero
					exisHome.existenciaBodegaoACero(this.bodegaActual);					
					for( int i = 0;  i < existenciaList.size(); i++){
						//actualiza los productos actuales con los valores recibidos del ws
						//exisHome.actualizarExistneciaProducto(existenciaList.get(i));
						System.out.println( ""+ i +"- Codigo Bodega: "+ existenciaList.get(i).getId().getBodegaid() + 
												" Producto id: " + existenciaList.get(i).getId().getProductoid() +
												" Cantidad:" + existenciaList.get(i).getCantidad());	
						exisHome.actualizarExistneciaProducto(existenciaList.get(i));
					}					
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}				
				
			}else{				
				try {
					if(estaAbierta(sucursal) ){
						
						//Consume el servicio
						List<Existencias> existenciaList = obtenertWSExistencia(sucursal);
						for( int i = 0;  i < existenciaList.size(); i++){
							System.out.println( ""+ i +"- Codigo Bodega: "+ existenciaList.get(i).getId().getBodegaid() + 
									" Producto id: " + existenciaList.get(i).getId().getProductoid() +
									" Cantidad:" + existenciaList.get(i).getCantidad());
						}
						//ubica la bodega actual
						//coloca las existencias en cero
						//y actualiza los productos actuales con los valores recibidos del ws
						
						
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				
			}			
		}//fin del for que itera sucursales
		
		
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
		List<Existencias> existenciasList = new ArrayList<Existencias>();
		//cliente Jersey- consume el WS
		System.out.println("Servicio Existencias: " + sucursal.getDescripcion() + " | " + sucursal.getRutaweb() + this.servicio);
		
		Client client = Client.create();
		WebResource webResource = client.resource(sucursal.getRutaweb() + this.servicio);
		ExistenciaActualWrap response = webResource.accept("application/json").get(ExistenciaActualWrap.class);
	
		try {
			//este list viene del WS "data:[]"
			List<ExistenciaActualDetalle> detalle = response.getMessage().getData();
			if (detalle != null) {
				if (!detalle.isEmpty()) {
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

		// validacion del dia de la semana
		Calendar cal = Calendar.getInstance();
		cal.setTime(diaOperativo);
		int diaSemana = cal.get(Calendar.DAY_OF_WEEK);
		if (diaSemana >= 2 && diaSemana <= 7) {//lunes a sabado

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
	
	
}