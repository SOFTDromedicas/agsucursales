package com.dromedicas.jaxrs.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.dromedicas.dao.HibernateSessionFactory;
import com.dromedicas.dao.SucursalesHome;
import com.dromedicas.dao.VentadiariaglobalHome;
import com.dromedicas.dto.Sucursales;
import com.dromedicas.dto.Ventadiariaglobal;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class ClienteVentasAlInstante implements Job {
	
	Logger log = Logger.getLogger(ClienteVentasAlInstante.class);
	
	//Servicio Ventas al instante
	private String servicio = "wsjson/ventainstante"; 
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// Obtiene todas las Sucursales		
		SucursalesHome sucursalesHome = new SucursalesHome();//Objeto DAO para sucursales
		VentadiariaglobalHome ventaDiaraHome =  new VentadiariaglobalHome();
		log.info("Obteniendo Sucursales");
		List<Sucursales> sucursalList= sucursalesHome.findAll();


		// Itera Todas las sucursales
		for( Sucursales sucursal : sucursalList ){	
			
			// Revisa si la sucursal  es 24 horas
			if( sucursal.getEs24horas().trim().equals("true") ){
				try {
					log.info("Consume servicio para la Sucursal: " + sucursal.getDescripcion());
					//consume servicio
					List<Ventadiariaglobal> ventasActualList = 
										this.obtenertWSVentaAlInstante(sucursal);
					if(ventasActualList != null ){
						// Itera ventasActualList						
						// Busca si existen valores para el dia operativo actual, y los elimina
						log.info("Buscando valores para el dia operativo actual");
						for(Ventadiariaglobal e: ventasActualList){
							Ventadiariaglobal ventaAnterior =  
									(Ventadiariaglobal) ventaDiaraHome.getVentasDiaActual( 
												e.getCodsucursal(), e.getDiaoperativo(), e.getVendedor());
							//elimina los registros actuales
							if(ventaAnterior != null){		
								log.info("Elimina valor actual");
								ventaDiaraHome.delete(ventaAnterior);
							}
							
							// Perisite el(los) nuevo(s) objeto(s) Ventadiariaglobal
							log.info("Grabando el nuevo Valor valor actual");
							ventaDiaraHome.guardarVentaDiaraGlobal(e);
						}
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
			System.out.println();
		}//fin del for que itera las sucursales

			

			// Perisite el nuevo objeto Ventadiariaglobal

		// finalizada la iteracion de la sucursales cierra la conexion a la base de datos
		HibernateSessionFactory.stopSessionFactory();
		sucursalesHome = null;
		ventaDiaraHome = null;
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
					System.out.println(e.getCause());
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
	private List<Ventadiariaglobal> obtenertWSVentaAlInstante( Sucursales sucursal){
		List<Ventadiariaglobal> ventaList = null;
		Client client = Client.create();
		WebResource webResource = client.resource(sucursal.getRutaweb()+ this.servicio);				
		VentaAlInstanteWrap response = webResource.accept("application/json").get(VentaAlInstanteWrap.class);	
						
		List<VentaAlInstanteDetalle> detalle = response.getMessage().getData();
		if(!detalle.isEmpty()){
			for(VentaAlInstanteDetalle e: detalle){
				Ventadiariaglobal venta = new Ventadiariaglobal();
				venta.setCodsucursal(sucursal.getCodigo());
				venta.setDiaoperativo(e.getDiaoperativo());
				venta.setVendedor(e.getVendedor());
				venta.setVentaespe(new Double(e.getVtaespecial().trim()));
				venta.setVentagen(new Double(e.getVtageneral()));
				
				ventaList.add(venta);
			}
		}//fin del if empty
		return ventaList;
	}

	
	public boolean estaAbierta(){
		return true;
	}

}
