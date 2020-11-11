package br.com.hioktec.votacao.requisicao;

import javax.validation.constraints.NotBlank;

public class LoginRequest {
	
	@NotBlank
	private String nomeUsuarioOuEmail;
	
	@NotBlank
	private String senha;

	public String getNomeUsuarioOuEmail() {
		return nomeUsuarioOuEmail;
	}

	public void setNomeUsuarioOuEmail(String nomeUsuarioOuEmail) {
		this.nomeUsuarioOuEmail = nomeUsuarioOuEmail;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}
	
}
