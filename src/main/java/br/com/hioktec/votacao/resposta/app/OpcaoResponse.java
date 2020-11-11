package br.com.hioktec.votacao.resposta.app;

public class OpcaoResponse {
	
	private long id;
	private String texto;
	private long contagemVotos;
	
	public long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTexto() {
		return texto;
	}
	public void setTexto(String texto) {
		this.texto = texto;
	}
	public long getContagemVotos() {
		return contagemVotos;
	}
	public void setContagemVotos(Long contagemVotos) {
		this.contagemVotos = contagemVotos;
	}
	
}
