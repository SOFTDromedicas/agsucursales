package com.dromedicas.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dromedicas.dao.ExistenciasHome;
import com.dromedicas.dao.SucursalesHome;
import com.dromedicas.dao.VentadiariaglobalHome;
import com.dromedicas.dto.Existencias;
import com.dromedicas.dto.Sucursales;
import com.dromedicas.dto.Ventadiariaglobal;

public class TestCRUD {

	
	private static final Log log = LogFactory.getLog(SucursalesHome.class);
	
	public static void main(String[] args) {
		
		String dateStart = "10/04/2017 09:55:58";
		String dateStop = "10/04/2017 10:03:48";

		//HH converts hour in 24 hours format (0-23), day calculation
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

		Date d1 = null;
		Date d2 = null;

		try {
			d1 = format.parse(dateStart);
			d2 = format.parse(dateStop);

			//in milliseconds
			long diff = d2.getTime() - d1.getTime();

			long diffSeconds = diff / 1000 % 60;
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000) % 24;
			long diffDays = diff / (24 * 60 * 60 * 1000);

			System.out.print(diffDays + " days, ");
			System.out.print(diffHours + " hours, ");
			System.out.print(diffMinutes + " minutes, ");
			System.out.print(diffSeconds + " seconds.");

		} catch (Exception e) {
			e.printStackTrace();
		}


		
		
		
	}

}
