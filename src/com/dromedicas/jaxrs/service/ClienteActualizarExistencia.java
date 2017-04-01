package com.dromedicas.jaxrs.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

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
		

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		SucursalesHome sucursalHome = new SucursalesHome();
		log.info("Obteniendo Sucursales");
		List<Sucursales> sucursalList = sucursalHome.findAll();
		System.out.println("Total Sucursales: " + sucursalList.size());
		
		//Recorre las sucursales
		for(Sucursales sucursal : sucursalList){
			System.out.println("Consume servicio para la Sucursal: " + sucursal.getDescripcion() );
			//Consume el servicio
			List<Existencias> existenciaList = obtenertWSExistencia(sucursal);
			
			
		}
	}
	
	
	private List<Existencias> obtenertWSExistencia( Sucursales sucursal ){
		List<Existencias> existenciasList = new ArrayList<Existencias>();
		//cliente Jersey
		Client client = Client.create();
		WebResource webResource = client.resource(sucursal.getRutaweb() + this.servicio);
		ExistenciaActualWrap response = webResource.accept("application/json").get(ExistenciaActualWrap.class);
	
		try {
			List<ExistenciaActualDetalle> detalle = response.getMessage().getData();
			if (detalle != null) {
				if (!detalle.isEmpty()) {
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
	
	
}