package br.com.hioktec.votacao.resposta.app;

public class UsuarioIdentidadeDisponivel {
	
	private boolean disponivel;

	public UsuarioIdentidadeDisponivel(boolean disponivel) {
		this.disponivel = disponivel;
	}

	public boolean isDisponivel() {
		return disponivel;
	}

	public void setDisponivel(boolean disponivel) {
		this.disponivel = disponivel;
	}
		
}
