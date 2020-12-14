package br.com.hioktec.votacao.requisicao;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class LoginRequest {
	
	@NotBlank
	@Size(min = 3, max = 40)
	private String nomeUsuarioOuEmail;
	
	@NotBlank
	@Size(min = 6, max = 20)
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
