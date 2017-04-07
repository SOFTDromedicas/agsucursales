package com.dromedicas.dto;
// Generated 5/04/2017 03:19:03 PM by Hibernate Tools 5.1.2.Final

import java.util.Date;

/**
 * Notificacion generated by hbm2java
 */
public class Notificacion implements java.io.Serializable {

	private Integer idnotificacion;
	private Incidente incidente;
	private Tiponotificacion tiponotificacion;
	private Date momento;

	public Notificacion() {
	}

	public Notificacion(Incidente incidente, Tiponotificacion tiponotificacion) {
		this.incidente = incidente;
		this.tiponotificacion = tiponotificacion;
	}

	public Notificacion(Incidente incidente, Tiponotificacion tiponotificacion, Date momento) {
		this.incidente = incidente;
		this.tiponotificacion = tiponotificacion;
		this.momento = momento;
	}

	public Integer getIdnotificacion() {
		return this.idnotificacion;
	}

	public void setIdnotificacion(Integer idnotificacion) {
		this.idnotificacion = idnotificacion;
	}

	public Incidente getIncidente() {
		return this.incidente;
	}

	public void setIncidente(Incidente incidente) {
		this.incidente = incidente;
	}

	public Tiponotificacion getTiponotificacion() {
		return this.tiponotificacion;
	}

	public void setTiponotificacion(Tiponotificacion tiponotificacion) {
		this.tiponotificacion = tiponotificacion;
	}

	public Date getMomento() {
		return this.momento;
	}

	public void setMomento(Date momento) {
		this.momento = momento;
	}

}