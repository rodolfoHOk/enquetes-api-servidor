package br.com.hioktec.votacao.modelo.app;

public class ContagemVotosPorOpcao {
	
	private Long opcaoId;
	
	private Long contagemVotos;

	public ContagemVotosPorOpcao(Long opcaoId, Long contagemVotos) {
		this.opcaoId = opcaoId;
		this.contagemVotos = contagemVotos;
	}

	public Long getOpcaoId() {
		return opcaoId;
	}

	public void setOpcaoId(Long opcaoId) {
		this.opcaoId = opcaoId;
	}

	public Long getContagemVotos() {
		return contagemVotos;
	}

	public void setContagemVotos(Long contagemVotos) {
		this.contagemVotos = contagemVotos;
	}
	
}
