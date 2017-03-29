package com.dromedicas.jaxrs.service;

import java.util.List;

import com.dromedicas.dao.HibernateSessionFactory;
import com.dromedicas.dao.SucursalesHome;
import com.dromedicas.dto.Sucursales;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class ClienteVentasAlInstante {

	public static void main(String args[]) {
		//Obtiene todas las Sucursales
		
		//Itera Todas las sucursales
		
				//Revisa si la sucursal no es 24 horas
				
				//Si no es 24 horas revisa si la hora actual esta entre la hora de apertura y +1 sobre
				//la hora cierre
				
		
			//Consume el servicio web de Ventas al instante para cada sucursal
			
			//Crea un objeto Ventadiariaglobal con los nuevos valores
			
			//Busca si existen valores para el dia operativo actual y los elimina
			
			//Perisite el nuevo objeto Ventadiariaglobal
	
		//finalizada la iteracion de la sucursales cierra la conexion  a la base de datos
		
		
		
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
					System.out.printf( "%s\t\t\t%s\t%s\n", "Vendedor", "V. General", "V. Bonificado" );
					for( VentaAlInstanteDetalle e : detalle){
						
						System.out.printf( "%s\t\t\t%s\t%s\n", e.getVendedor().trim(), e.getVtageneral(),  e.getVtaespecial() );
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

}
