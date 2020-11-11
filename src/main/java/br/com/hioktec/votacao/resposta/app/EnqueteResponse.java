package br.com.hioktec.votacao.resposta.app;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

public class EnqueteResponse {
	
	private Long id;
	private String pergunta;
	private List<OpcaoResponse> opcoes;
	private UsuarioSucinto criadoPor;
	private Instant dataHoraCriacao;
	private Instant dataHoraExpiracao;
	private Boolean isExpirada;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Long opcaoSelecionada;
	private Long totalVotos;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getPergunta() {
		return pergunta;
	}
	public void setPergunta(String pergunta) {
		this.pergunta = pergunta;
	}
	public List<OpcaoResponse> getOpcoes() {
		return opcoes;
	}
	public void setOpcoes(List<OpcaoResponse> opcoes) {
		this.opcoes = opcoes;
	}
	public UsuarioSucinto getCriadoPor() {
		return criadoPor;
	}
	public void setCriadoPor(UsuarioSucinto criadoPor) {
		this.criadoPor = criadoPor;
	}
	public Instant getDataHoraCriacao() {
		return dataHoraCriacao;
	}
	public void setDataHoraCriacao(Instant dataHoraCriacao) {
		this.dataHoraCriacao = dataHoraCriacao;
	}
	public Instant getDataHoraExpiracao() {
		return dataHoraExpiracao;
	}
	public void setDataHoraExpiracao(Instant dataHoraExpiracao) {
		this.dataHoraExpiracao = dataHoraExpiracao;
	}
	public Boolean getIsExpirado() {
		return isExpirada;
	}
	public void setIsExpirado(Boolean isExpirado) {
		this.isExpirada = isExpirado;
	}
	public Long getOpcaoSelecionada() {
		return opcaoSelecionada;
	}
	public void setOpcaoSelecionada(Long opcaoSelecionada) {
		this.opcaoSelecionada = opcaoSelecionada;
	}
	public Long getTotalVotos() {
		return totalVotos;
	}
	public void setTotalVotos(Long totalVotos) {
		this.totalVotos = totalVotos;
	}
	
}
