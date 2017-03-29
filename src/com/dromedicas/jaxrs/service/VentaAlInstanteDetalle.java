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
@JsonPropertyOrder({ "diaoperativo", "vendedor", "vtageneral", "vtaespecial" })
public class VentaAlInstanteDetalle {

	@JsonProperty("diaoperativo")
	private String diaoperativo;
	@JsonProperty("vendedor")
	private String vendedor;
	@JsonProperty("vtageneral")
	private String vtageneral;
	@JsonProperty("vtaespecial")
	private String vtaespecial;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("diaoperativo")
	public String getDiaoperativo() {
		return diaoperativo;
	}

	@JsonProperty("diaoperativo")
	public void setDiaoperativo(String diaoperativo) {
		this.diaoperativo = diaoperativo;
	}

	@JsonProperty("vendedor")
	public String getVendedor() {
		return vendedor;
	}

	@JsonProperty("vendedor")
	public void setVendedor(String vendedor) {
		this.vendedor = vendedor;
	}

	@JsonProperty("vtageneral")
	public String getVtageneral() {
		return vtageneral;
	}

	@JsonProperty("vtageneral")
	public void setVtageneral(String vtageneral) {
		this.vtageneral = vtageneral;
	}

	@JsonProperty("vtaespecial")
	public String getVtaespecial() {
		return vtaespecial;
	}

	@JsonProperty("vtaespecial")
	public void setVtaespecial(String vtaespecial) {
		this.vtaespecial = vtaespecial;
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