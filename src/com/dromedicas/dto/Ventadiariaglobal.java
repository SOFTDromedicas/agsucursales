package com.dromedicas.dto;
// Generated 28/03/2017 05:43:08 PM by Hibernate Tools 5.1.2.Final

import java.util.Date;

/**
 * Ventadiariaglobal generated by hbm2java
 */
public class Ventadiariaglobal implements java.io.Serializable {

	private Long id;
	private String codsucursal;
	private String vendedor;
	private double ventagen;
	private double ventaespe;
	private String diaoperativo;
	private Date ultactualizacion;
	private Double marcapropia;

	public Ventadiariaglobal() {
	}

	public Ventadiariaglobal(double ventagen, double ventaespe, String diaoperativo) {
		this.ventagen = ventagen;
		this.ventaespe = ventaespe;
		this.diaoperativo = diaoperativo;
	}

	public Ventadiariaglobal(String codsucursal, String vendedor, double ventagen, double ventaespe,
			String diaoperativo, Date ultactualizacion, Double marcapropia) {
		this.codsucursal = codsucursal;
		this.vendedor = vendedor;
		this.ventagen = ventagen;
		this.ventaespe = ventaespe;
		this.diaoperativo = diaoperativo;
		this.ultactualizacion = ultactualizacion;
		this.marcapropia = marcapropia;
	}


	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCodsucursal() {
		return this.codsucursal;
	}

	public void setCodsucursal(String codsucursal) {
		this.codsucursal = codsucursal;
	}

	public String getVendedor() {
		return this.vendedor;
	}

	public void setVendedor(String vendedor) {
		this.vendedor = vendedor;
	}

	public double getVentagen() {
		return this.ventagen;
	}

	public void setVentagen(double ventagen) {
		this.ventagen = ventagen;
	}

	public double getVentaespe() {
		return this.ventaespe;
	}

	public void setVentaespe(double ventaespe) {
		this.ventaespe = ventaespe;
	}

	public String getDiaoperativo() {
		return this.diaoperativo;
	}

	public void setDiaoperativo(String diaoperativo) {
		this.diaoperativo = diaoperativo;
	}

	public Date getUltactualizacion() {
		return ultactualizacion;
	}

	public void setUltactualizacion(Date ultactualizacion) {
		this.ultactualizacion = ultactualizacion;
	}

	public Double getMarcapropia() {
		return this.marcapropia;
	}

	public void setMarcapropia(Double marcapropia) {
		this.marcapropia = marcapropia;
	}

	
	
}
