package br.com.hioktec.votacao.excecao;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class AppException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	public AppException(String mensagem) {
		super(mensagem);
	}
	
	public AppException(String mensagem, Throwable causa) {
		super(mensagem, causa);
	}
	
}
