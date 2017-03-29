package com.dromedicas.jaxrs.service;

import java.util.List;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class ClienteVentasAlInstante {

	public static void main(String args[]) {
		try {

			Client client = Client.create();

			WebResource webResource = client
					.resource("http://farmanorte21.no-ip.info:8080/dropos/wsjson/ventainstante");

			System.out.println(webResource.getURI());

			VentaAlInstanteWrap response = webResource.accept("application/json").get(VentaAlInstanteWrap.class);

			System.out.println(response.getMessage().getCode());
			
			List<VentaAlInstanteDetalle> detalle = response.getMessage().getData();
			for( VentaAlInstanteDetalle e : detalle){
				System.out.println(e.getVendedor());
			}

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
