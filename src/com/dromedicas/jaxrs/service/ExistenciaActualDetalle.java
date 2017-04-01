package com.dromedicas.jaxrs.service;

import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({ "productoid", "cantidad", "bodegaid" })
public class ExistenciaActualDetalle {

	@JsonProperty("productoid")
	private String productoid;
	@JsonProperty("cantidad")
	private String cantidad;
	@JsonProperty("bodegaid")
	private String bodegaid;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("productoid")
	public String getProductoid() {
		return productoid;
	}

	@JsonProperty("productoid")
	public void setProductoid(String productoid) {
		this.productoid = productoid;
	}

	@JsonProperty("cantidad")
	public String getCantidad() {
		return cantidad;
	}

	@JsonProperty("cantidad")
	public void setCantidad(String cantidad) {
		this.cantidad = cantidad;
	}

	@JsonProperty("bodegaid")
	public String getBodegaid() {
		return bodegaid;
	}

	@JsonProperty("bodegaid")
	public void setBodegaid(String bodegaid) {
		this.bodegaid = bodegaid;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

}