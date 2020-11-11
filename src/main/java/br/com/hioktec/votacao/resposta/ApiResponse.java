package br.com.hioktec.votacao.resposta;

public class ApiResponse {

	private boolean sucesso;
	
	private String mensagem;

	public ApiResponse(boolean sucesso, String mensagem) {
		this.sucesso = sucesso;
		this.mensagem = mensagem;
	}

	public boolean isSucesso() {
		return sucesso;
	}

	public void setSucesso(boolean sucesso) {
		this.sucesso = sucesso;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
}
