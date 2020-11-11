package br.com.hioktec.votacao.requisicao.app;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class OpcaoRequest {
	
	@NotBlank
	@Size(max = 40)
	private String texto;

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}
	
}
