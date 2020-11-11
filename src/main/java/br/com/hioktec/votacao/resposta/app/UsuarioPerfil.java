package br.com.hioktec.votacao.resposta.app;

import java.time.Instant;

public class UsuarioPerfil {
	
	private Long id;
	private String nomeUsuario;
	private String nome;
	private Instant entrouEm;
	private Long contagemEnquetes;
	private Long contagemVotos;
	
	public UsuarioPerfil(Long id, String nomeUsuario, String nome, Instant entrouEm, Long contagemEnquetes,
			Long contagemVotos) {
		this.id = id;
		this.nomeUsuario = nomeUsuario;
		this.nome = nome;
		this.entrouEm = entrouEm;
		this.contagemEnquetes = contagemEnquetes;
		this.contagemVotos = contagemVotos;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNomeUsuario() {
		return nomeUsuario;
	}

	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Instant getEntrouEm() {
		return entrouEm;
	}

	public void setEntrouEm(Instant entrouEm) {
		this.entrouEm = entrouEm;
	}

	public Long getContagemEnquetes() {
		return contagemEnquetes;
	}

	public void setContagemEnquetes(Long contagemEnquetes) {
		this.contagemEnquetes = contagemEnquetes;
	}

	public Long getContagemVotos() {
		return contagemVotos;
	}

	public void setContagemVotos(Long contagemVotos) {
		this.contagemVotos = contagemVotos;
	}
	
}
