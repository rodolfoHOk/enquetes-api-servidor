package br.com.hioktec.votacao.requisicao.app;

import javax.validation.constraints.NotNull;

public class VotoRequest {
	
	@NotNull
	private Long opcaoId;

	public Long getOpcaoId() {
		return opcaoId;
	}

	public void setOpcaoId(Long opcaoId) {
		this.opcaoId = opcaoId;
	}
	
}
