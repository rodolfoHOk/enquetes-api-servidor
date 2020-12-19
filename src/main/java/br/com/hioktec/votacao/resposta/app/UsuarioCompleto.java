package br.com.hioktec.votacao.resposta.app;

public class UsuarioCompleto {
	
	private Long id;
	private String nome;
	private String nomeUsuario;
	private String email;
	
	public UsuarioCompleto(Long id, String nome, String nomeUsuario, String email) {
		this.id = id;
		this.nome = nome;
		this.nomeUsuario = nomeUsuario;
		this.email = email;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNomeUsuario() {
		return nomeUsuario;
	}

	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
