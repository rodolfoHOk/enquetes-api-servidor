package br.com.hioktec.votacao.modelo;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.NaturalId;

import br.com.hioktec.votacao.modelo.audit.DataAudit;

@Entity
@Table(name = "usuarios", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"nome_usuario"}),
		@UniqueConstraint(columnNames = {"email"})
})
public class Usuario extends DataAudit {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@NotBlank
	@Size(max = 40)
	@Column(name = "nome")
	private String nome;
	
	@NotBlank
	@Size(max = 15)
	@Column(name = "nome_usuario")
	private String nomeUsuario;
	
	@NaturalId
	@NotBlank
	@Size(max = 40)
	@Email
	@Column(name = "email")
	private String email;
	
	@NotBlank
	@Size(max = 100)
	@Column(name = "senha")
	private String senha;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "funcoes_usuario",
			joinColumns = @JoinColumn(name = "usuario_id"),
			inverseJoinColumns = @JoinColumn(name = "funcao_id"))
	private Set<Funcao> funcoes = new HashSet<Funcao>();
	
	// adicionado para verificação de email antes de habilitar conta do usuário.
	@Column(name = "habilitado")
	private boolean habilitado;
	
	public Usuario() {
		
	}
	
	public Usuario(String nome, String nomeUsuario, String email, String senha) {
		this.nome = nome;
		this.nomeUsuario = nomeUsuario;
		this.email = email;
		this.senha = senha;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNomeUsuario() {
		return nomeUsuario;
	}

	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public Set<Funcao> getFuncoes() {
		return funcoes;
	}

	public void setFuncoes(Set<Funcao> funcoes) {
		this.funcoes = funcoes;
	}

	// adicionados para verificação de email antes de habilitar conta do usuário.
	public boolean isHabilitado() {
		return habilitado;
	}

	public void setHabilitado(boolean habilitado) {
		this.habilitado = habilitado;
	}
}
