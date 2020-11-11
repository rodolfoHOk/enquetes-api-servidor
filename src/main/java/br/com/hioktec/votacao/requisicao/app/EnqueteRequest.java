package br.com.hioktec.votacao.requisicao.app;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class EnqueteRequest {
	
	@NotBlank
	@Size(max = 140)
	private String pergunta;
	
	@NotNull
	@Size(min = 2, max = 6)
	@Valid
	private List<OpcaoRequest> opcoes;
	
	@NotNull
	@Valid
	private DuracaoEnquete duracaoEnquete;

	public String getPergunta() {
		return pergunta;
	}

	public void setPergunta(String pergunta) {
		this.pergunta = pergunta;
	}

	public List<OpcaoRequest> getOpcoes() {
		return opcoes;
	}

	public void setOpcoes(List<OpcaoRequest> opcoes) {
		this.opcoes = opcoes;
	}

	public DuracaoEnquete getDuracaoEnquete() {
		return duracaoEnquete;
	}

	public void setDuracaoEnquete(DuracaoEnquete duracaoEnquete) {
		this.duracaoEnquete = duracaoEnquete;
	}
	
}
