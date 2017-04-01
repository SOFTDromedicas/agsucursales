package com.dromedicas.servicio;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.dromedicas.dto.Sucursales;

public class EnviarMailAlertas {
	
	public static void enviarEmailAlertaVentas(Sucursales instance , Date fecha) {
		String sucursal = instance.getDescripcion();
		System.out.println("Clase enviar Email Alerta");
		try{
			SimpleDateFormat sdf = new SimpleDateFormat(
					"dd/MM/yyyy - hh:mm:ss aaa");
			File inputHtml = new File(
					"C:/FarmapuntosEmail/dripresponsive.html");
			// Asginamos el archivo al objeto analizador Document
			Document doc = Jsoup.parse(inputHtml, "UTF-8");
			// obtengo los id's del DOM a los que deseo insertar los valores
			// mediante el metodo append() se insertan los valores obtenidos de
			// la consulta
			Element nroTc = doc.select("span#sucursal1").first();
			nroTc.append(sucursal);
			
			Element franq = doc.select("span#sucursal2").first();
			franq.append(sucursal);
			
			Element bank = doc.select("span#ultActualizacion").first();
			bank.append(sdf.format(fecha));
			
			// envia el mail

			// Propiedades de la conexión
			Properties props = new Properties();
			props.setProperty("mail.smtp.host", "stve.wnkserver5.com");
			props.setProperty("mail.smtp.port", "25");// puerto de salida, de
			// entrada 110
			props.setProperty("mail.smtp.user",
								"pruebassistemas@dromedicas.com.co");
			props.setProperty("mail.smtp.auth", "true");
			props.put("mail.transport.protocol.", "smtp");

			// Preparamos la sesion
			Session session = Session.getDefaultInstance(props);
			// Construimos el mensaje

			/**
			 * Ojo aca reemplazar por consulta
			 */
			// multiples direcciones
			String[] to = { "lfernandortiz@gmail.com",
							"sistemas2@dromedicas.com.co",
							"sistemas@dromedicas.com.co",
							"saidrodriguez@gmail.com"
							};
		
			
			// arreglo con las direcciones de correo
			InternetAddress[] addressTo = new InternetAddress[to.length];
			for (int i = 0; i < addressTo.length; i++) {
				addressTo[i] = new InternetAddress(to[i]);
			}
			
			
			// se compone el mensaje (Asunto, cuerpo del mensaje y direccion origen)
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(
					"pruebassistemas@dromedicas.com.co"));
			message.setRecipients(Message.RecipientType.BCC, addressTo);
			message.setSubject("FALLA ACTUALIZACION VENTAS AL INSTANTE " + sucursal);
			message.setContent(doc.html(), "text/html; charset=utf-8");

			// Lo enviamos.
			Transport t = session.getTransport("smtp");
			t.connect("pruebassistemas@dromedicas.com.co", "Dromedicas2013.");
			t.sendMessage(message, message.getAllRecipients());
			
			// Cierre de la conexion
			t.close();
			System.out.println("Conexion cerrada");
			
		}catch(Exception e){
			System.out.println("Falla en el envio del correo:");
			e.printStackTrace();
		}
		
		
	}

}
