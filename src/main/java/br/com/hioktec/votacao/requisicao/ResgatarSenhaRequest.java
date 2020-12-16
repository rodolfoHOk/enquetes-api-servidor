package br.com.hioktec.votacao.requisicao;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ResgatarSenhaRequest {
	
	@NotBlank
	@Size(max = 40)
	@Email
	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}