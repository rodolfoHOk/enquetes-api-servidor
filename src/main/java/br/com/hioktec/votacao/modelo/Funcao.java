package br.com.hioktec.votacao.modelo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.NaturalId;

@Entity
@Table(name = "funcoes")
public class Funcao {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Enumerated(EnumType.STRING)
	@NaturalId
	@Column(name = "nome", length = 60)
	private NomeFuncao nome;

	public Funcao() {

	}

	public Funcao(NomeFuncao nome) {
		this.nome = nome;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public NomeFuncao getNome() {
		return nome;
	}

	public void setNome(NomeFuncao nome) {
		this.nome = nome;
	}
	
}
