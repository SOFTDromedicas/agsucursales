package com.dromedicas.servicio;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import com.dromedicas.dao.IncidenteHome;
import com.dromedicas.dto.Incidente;
import com.dromedicas.dto.Sucursales;

public class NotificacionService {
	
	//constructor sin argumentos
	public NotificacionService(){
		
	}
	
	
	public void registrarIncidente(Incidente instance){
		IncidenteHome incidenteHome = new IncidenteHome();
		incidenteHome.guardarIncidente(instance);		
	}
	
	
	public Incidente existeIncidente(String cliente, String incidente){
		IncidenteHome incidenteHome = new IncidenteHome();
		Incidente incidenteT = null;
		incidenteT = 
				incidenteHome.buscarIncidenteAbierto(cliente, incidente) ;
		
		return incidenteT;
	}
	
	
	public void enviarNotificacion(Incidente incidente, Sucursales sucursal ){
		
		Date registro = incidente.getOcurrencia();
		Date ahora = new Date();
		try {
			int invervalo = obetenerDiferenciaTiempos("minutos", registro, ahora);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
		
	public int obetenerDiferenciaTiempos(String tipo, Date tiempoIni, Date tiempoFin) throws ParseException {
		int intervalo = 0;
		switch (tipo) {
		case ("minutos"):
			Calendar horaInim = Calendar.getInstance();
			horaInim.setTime(tiempoIni);

			Calendar horaActm = Calendar.getInstance();
			horaActm.setTime(tiempoFin);

			intervalo = horaActm.get(Calendar.MINUTE) - horaInim.get(Calendar.MINUTE);
			break;
		case ("horas"):
			Calendar horaIni = Calendar.getInstance();
			horaIni.setTime(tiempoIni);

			Calendar horaAct = Calendar.getInstance();
			horaAct.setTime(tiempoFin);

			intervalo = horaAct.get(Calendar.HOUR) - horaIni.get(Calendar.HOUR);
			break;
		}
		return intervalo;
	}

}
