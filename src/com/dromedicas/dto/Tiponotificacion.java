package com.dromedicas.dto;
// Generated 5/04/2017 03:19:03 PM by Hibernate Tools 5.1.2.Final

import java.util.HashSet;
import java.util.Set;

/**
 * Tiponotificacion generated by hbm2java
 */
public class Tiponotificacion implements java.io.Serializable {

	private Integer idtiponotificacion;
	private String descripcion;
	private Double intervalo;
	private Set notificacions = new HashSet(0);

	public Tiponotificacion() {
	}

	public Tiponotificacion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Tiponotificacion(String descripcion, Double intervalo, Set notificacions) {
		this.descripcion = descripcion;
		this.intervalo = intervalo;
		this.notificacions = notificacions;
	}

	public Integer getIdtiponotificacion() {
		return this.idtiponotificacion;
	}

	public void setIdtiponotificacion(Integer idtiponotificacion) {
		this.idtiponotificacion = idtiponotificacion;
	}

	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Double getIntervalo() {
		return this.intervalo;
	}

	public void setIntervalo(Double intervalo) {
		this.intervalo = intervalo;
	}

	public Set getNotificacions() {
		return this.notificacions;
	}

	public void setNotificacions(Set notificacions) {
		this.notificacions = notificacions;
	}

}
