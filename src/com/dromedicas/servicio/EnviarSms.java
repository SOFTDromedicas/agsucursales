package com.dromedicas.servicio;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class EnviarSms {

	public static String enviarSms(String mensaje, String numero) {
		String respuesta = "";
		try {
			// Thread.sleep(5500);
			String query = String.format("cliente=%s&api=%s&numero=%s&sms=%s", URLEncoder.encode("10010333", "UTF-8"),
					URLEncoder.encode("4z1MlW6lsQHKiJ6x909E7zS8Rp5PRF", "UTF-8"),
					URLEncoder.encode( numero, "UTF-8"), 
					URLEncoder.encode(mensaje, "UTF-8"));

			URL url = new URL("https://ws.hablame.co/sms_http.php" + "?" + query);
			System.out.println(url);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			Map<String, List<String>> header = conn.getHeaderFields();
			int responseCode = conn.getResponseCode();
			System.out.println("Headers : " + header);
			System.out.println("Respuesta : " + responseCode);
			respuesta = "" + responseCode;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return respuesta;

	}// fin del metodo

}// fin de la clase
