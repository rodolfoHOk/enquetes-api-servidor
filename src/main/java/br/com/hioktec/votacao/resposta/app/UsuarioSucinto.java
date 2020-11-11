package br.com.hioktec.votacao.resposta.app;

public class UsuarioSucinto {
	
	private Long id;
	private String nomeUsuario;
	private String nome;
	
	public UsuarioSucinto(Long id, String nomeUsuario, String nome) {
		this.id = id;
		this.nomeUsuario = nomeUsuario;
		this.nome = nome;
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
	
}
