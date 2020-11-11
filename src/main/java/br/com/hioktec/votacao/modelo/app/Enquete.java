package br.com.hioktec.votacao.modelo.app;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import br.com.hioktec.votacao.modelo.app.audit.UsuarioDataAudit;

/**
 * Uma enquete tem um id, uma pergunta, uma lista de opções e uma dataHoraExpiracao. 
 * @author rodolfo
 */
@Entity
@Table(name = "enquetes")
public class Enquete extends UsuarioDataAudit{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	@Size(max = 140)
	private String pergunta;
	
	@OneToMany(mappedBy = "enquete", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@Size(min = 2, max = 6)
	@Fetch(FetchMode.SELECT)
	@BatchSize(size = 30)
	private List<Opcao> opcoes = new ArrayList<Opcao>();
	
	@NotNull
	private Instant dataHoraExpiracao;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPergunta() {
		return pergunta;
	}

	public void setPergunta(String pergunta) {
		this.pergunta = pergunta;
	}

	public List<Opcao> getOpcoes() {
		return opcoes;
	}

	public void setOpcoes(List<Opcao> opcoes) {
		this.opcoes = opcoes;
	}

	public Instant getDataHoraExpiracao() {
		return dataHoraExpiracao;
	}

	public void setDataHoraExpiracao(Instant dataHoraExpiracao) {
		this.dataHoraExpiracao = dataHoraExpiracao;
	}
	
	public void adicionarOpcao(Opcao opcao) {
		opcoes.add(opcao);
		opcao.setEnquete(this);
	}
	
	public void removerOpcao(Opcao opcao) {
		opcoes.remove(opcao);
		opcao.setEnquete(null);
	}
}
