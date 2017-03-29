package com.dromedicas.jaxrs.service;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.dromedicas.dao.HibernateSessionFactory;
import com.dromedicas.dao.SucursalesHome;
import com.dromedicas.dto.Sucursales;
import com.dromedicas.dto.Ventadiariaglobal;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class ClienteVentasAlInstante implements Job {
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// Obtiene todas las Sucursales		
		SucursalesHome sucursalesHome = new SucursalesHome();//Objeto DAO
		List<Sucursales> sucursalList= sucursalesHome.findAll();

		//Servicio Ventas al instante
		String servicio = "wsjson/ventainstante"; 

		// Itera Todas las sucursales
		for( Sucursales sucursal : sucursalList ){	
			
			// Revisa si la sucursal  es 24 horas
			if( sucursal.getEs24horas().trim() == "true" ){				
				//consume servicio
				try {
					List<Ventadiariaglobal> ventasList = 
										this.getWSVentaAlInstante(sucursal.getRutaweb() + servicio);
					if(ventasList != null ){
						// Busca si existen valores para el dia operativo actual, y los elimina

						// Perisite el(los) nuevo(s) objeto(s) Ventadiariaglobal
					}				
				} catch (Exception e) {
					System.out.println("Error en la conexion para la sucursal:  "  + 
								sucursal.getDescripcion() + " | " + sucursal.getRutaweb() + servicio);
					e.printStackTrace();
				}
				
			}else{
				// revisa si la hora actual esta entre la hora de  apertura y +1 hora sobre cierre
				//consume servicio 
			}
		}

			// Crea un objeto Ventadiariaglobal con los nuevos valores

			// Busca si existen valores para el dia operativo actual y los elimina

			// Perisite el nuevo objeto Ventadiariaglobal

		// finalizada la iteracion de la sucursales cierra la conexion a la base de datos
		
	}

	
	public static void main(String args[]) {
		
		try {
			
			//Obtengo todas las sucursales
			SucursalesHome sucursalesHome = new SucursalesHome();
			List<Sucursales> sucursalList= sucursalesHome.findAll();
			String servicio = "wsjson/ventainstante";
			for( Sucursales sucursal : sucursalList ){		
				//consultamos el servicio 
				Client client = Client.create();
				String url = sucursal.getRutaweb() + servicio;
				try {
					WebResource webResource = client
							.resource(url);				
					VentaAlInstanteWrap response = webResource.accept("application/json").get(VentaAlInstanteWrap.class);	
									
					List<VentaAlInstanteDetalle> detalle = response.getMessage().getData();
					
					
					System.out.println("Sucursal: " + sucursal.getDescripcion() + " | " + sucursal.getRutaweb() + servicio);
					System.out.println("Dia Operativo: " + detalle.get(0).getDiaoperativo());	
					
					for( VentaAlInstanteDetalle e : detalle){
						
						System.out.println(  e.getVendedor().trim() + " - " + e.getVtageneral() +" - "+  e.getVtaespecial() );
					}
					
				} catch (Exception e) {
					System.out.println("Error en la conexion para la sucursal:  "  + sucursal.getDescripcion() + " | " + sucursal.getRutaweb() + servicio);
					e.printStackTrace();
				}
				
				
				System.out.println();
			}
			
			
			//sessionFactory.close();
			HibernateSessionFactory.stopSessionFactory();
			

			

			// //create ObjectMapper instance
			// ObjectMapper objectMapper = new ObjectMapper();
			//
			// //convert json string to object
			// VentaAlInstanteWrap emp = objectMapper.readValue(jsonData,
			// VentaAlInstanteWrap.class);

			// if (response.getStatus() != 200) {
			// throw new RuntimeException("Failed : HTTP error code : "
			// + response.getStatus());
			// }
			//
			// String output = response.getEntity(String.class);
			//
			// System.out.println("Output from Server .... \n");
			// System.out.println(output);

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	
	/**
	 * Retorna una List de objetos  <code>Ventadiariaglobal</code> correspondientes
	 * a las ventas de una sucursal por vendedor.
	 * @param url
	 * @return
	 */
	private List<Ventadiariaglobal> getWSVentaAlInstante(String url){
		
		Client client = Client.create();
		WebResource webResource = client.resource(url);				
		VentaAlInstanteWrap response = webResource.accept("application/json").get(VentaAlInstanteWrap.class);	
						
		List<VentaAlInstanteDetalle> detalle = response.getMessage().getData();
		return null;
	}

	

}
