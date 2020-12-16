package br.com.hioktec.votacao.requisicao;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class MudarSenhaRequest {
	
	@NotBlank
	@Size(max = 255)
	private String token;
	
	@NotBlank
	@Size(min = 6, max = 40)
	private String senha;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}
}
