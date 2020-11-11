package br.com.hioktec.votacao.excecao;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;
	
	private String nomeRecurso;
	private String nomeCampo;
	private Object valorCampo;

	public ResourceNotFoundException(String nomeRecurso, String nomeCampo, Object valorCampo) {
		super(String.format("%s não encontrado com %s : '%s'", nomeRecurso, nomeCampo, valorCampo));	
		this.nomeRecurso = nomeRecurso;
		this.nomeCampo = nomeCampo;
		this.valorCampo = valorCampo;
	}

	public String getNomeRecurso() {
		return nomeRecurso;
	}

	public String getNomeCampo() {
		return nomeCampo;
	}

	public Object getValorCampo() {
		return valorCampo;
	}
	
}
