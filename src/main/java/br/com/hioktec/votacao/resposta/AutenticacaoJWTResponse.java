package br.com.hioktec.votacao.resposta;

public class AutenticacaoJWTResponse {
	
	private String tokenAcesso;
	
	private String tipoToken = "Portador";
	
	public AutenticacaoJWTResponse(String tokenAcesso) {
		this.tokenAcesso = tokenAcesso;
	}

	public String getTokenAcesso() {
		return tokenAcesso;
	}

	public void setTokenAcesso(String tokenAcesso) {
		this.tokenAcesso = tokenAcesso;
	}

	public String getTipoToken() {
		return tipoToken;
	}

	public void setTipoToken(String tipoToken) {
		this.tipoToken = tipoToken;
	}
	
	

}
