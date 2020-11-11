package br.com.hioktec.votacao.requisicao.app;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

public class DuracaoEnquete {
	
	@NotNull
	@Max(7)
	private Integer dias;
	
	@NotNull
	@Max(23)
	private Integer horas;

	public Integer getDias() {
		return dias;
	}

	public void setDias(Integer dias) {
		this.dias = dias;
	}

	public Integer getHoras() {
		return horas;
	}

	public void setHoras(Integer horas) {
		this.horas = horas;
	}
	
}
