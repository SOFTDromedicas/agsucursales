package com.dromedicas.test;

import java.util.ArrayList;
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

	static // Servicio Ventas al instante
	String servicio = "wsjson/ventainstante";

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
				}

			} else {
				// revisa si la hora actual esta entre la hora de apertura y +1
				// hora sobre cierre
				// consume servicio
			}
			System.out.println();
		} // fin del for que itera las sucursales

		// Perisite el nuevo objeto Ventadiariaglobal

		// finalizada la iteracion de la sucursales cierra la conexion a la base
		// de datos
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
	private static List<Ventadiariaglobal> obtenertWSVentaAlInstante(Sucursales sucursal) {
		List<Ventadiariaglobal> ventaList = new ArrayList<Ventadiariaglobal>();
		Client client = Client.create();
		WebResource webResource = client.resource(sucursal.getRutaweb() + servicio);
		VentaAlInstanteWrap response = webResource.accept("application/json").get(VentaAlInstanteWrap.class);

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
		return ventaList;
	}

	public boolean estaAbierta() {
		return true;
	}
}
