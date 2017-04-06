package com.dromedicas.servicio;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.dromedicas.dao.IncidenteHome;
import com.dromedicas.dao.NotificacionHome;
import com.dromedicas.dto.Incidente;
import com.dromedicas.dto.Notificacion;
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
			int intervalo = obetenerDiferenciaTiempos("minutos", registro, ahora);
			if( intervalo >= (incidente.getTipoincidente().getHorasintervalo()*100)){
				//hay notificaciones enviadas
				NotificacionHome notiHome = new NotificacionHome();
				List<Notificacion> notificacionList = notiHome.obtenerNotificacionPorIncidente(incidente);
				if( !notificacionList.isEmpty() ){
					
					//obtener la ultima notificacion Email
					
					//obtener la ultima 
					
				}else{
					
					//envia la notificacion con base en el timpo en el valor de intervalo
					
				}
			}
			
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
