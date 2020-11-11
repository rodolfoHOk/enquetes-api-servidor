package br.com.hioktec.votacao.modelo.audit;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Super Classe que representa a Data de Auditoração criadoAs e atualizadoAs que é extendia pela classe Usuario.
 * @author rodolfo
 *
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"criadoAs", "atualizadoAs"}, allowGetters = true)
public abstract class DataAudit implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private Instant criadoAs;
	
	@LastModifiedDate
	@Column(nullable = false)
	private Instant atualizadoAs;

	public Instant getCriadoAs() {
		return criadoAs;
	}

	public void setCriadoAs(Instant criadoAs) {
		this.criadoAs = criadoAs;
	}

	public Instant getAtualizadoAs() {
		return atualizadoAs;
	}

	public void setAtualizadoAs(Instant atualizadoAs) {
		this.atualizadoAs = atualizadoAs;
	}
	
}
