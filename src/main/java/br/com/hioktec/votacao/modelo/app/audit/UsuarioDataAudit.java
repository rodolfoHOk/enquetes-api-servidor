package br.com.hioktec.votacao.modelo.app.audit;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import br.com.hioktec.votacao.modelo.audit.DataAudit;

/**
 * Para realizar a auditoria do usuário. criamos esta classe UsuarioDataAudit que estende o modelo DataAudit
 * que definimos antes.
 * @author rodolfo
 */
@MappedSuperclass
@JsonIgnoreProperties(value = {"criadoPor", "atualizadoPor"}, allowGetters = true)
public abstract class UsuarioDataAudit extends DataAudit{

	private static final long serialVersionUID = 1L;

	@CreatedBy
	@Column(updatable = false)
	private Long criadoPor;
	
	@LastModifiedBy
	private Long atualizadoPor;

	public Long getCriadoPor() {
		return criadoPor;
	}

	public void setCriadoPor(Long criadoPor) {
		this.criadoPor = criadoPor;
	}

	public Long getAtualizadoPor() {
		return atualizadoPor;
	}

	public void setAtualizadoPor(Long atualizadoPor) {
		this.atualizadoPor = atualizadoPor;
	}
	
}
