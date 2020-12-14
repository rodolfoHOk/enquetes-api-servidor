package br.com.hioktec.votacao.modelo;

import java.sql.Date;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * classe que representa um token de confirmação de email.
 * @author rodolfo
 */
@Entity
@Table(name = "token_confirmacao")
public class TokenConfirmacao {
	
	private static final int EXPIRACAO = 60 * 24;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	@Column(name = "token")
	private String token;
	
	@OneToOne(targetEntity = Usuario.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "usuario_id", nullable = false)
	private Usuario usuario;
	
	@Column(name = "data_criacao")
	private Date dataCriacao;
	
	@Column(name = "data_expiracao")
	private Date dataExpiracao;
	
	public TokenConfirmacao() {
		
	}
	
	public TokenConfirmacao (final String token, final Usuario usuario) {
		Calendar calendar = Calendar.getInstance();
		this.token = token;
		this.usuario = usuario;
		this.dataCriacao = new Date(calendar.getTime().getTime());
		this.dataExpiracao = calcularDataExpiracao(EXPIRACAO);
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public Date getDataExpiracao() {
		return dataExpiracao;
	}

	public void setDataExpiracao(Date dataExpiracao) {
		this.dataExpiracao = dataExpiracao;
	}

	private Date calcularDataExpiracao(int tempoExpiracaoEmMinutos) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, tempoExpiracaoEmMinutos);
		return new Date(calendar.getTime().getTime());
	}
}
