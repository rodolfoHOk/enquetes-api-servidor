package br.com.hioktec.votacao.seguranca;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.hioktec.votacao.modelo.Usuario;

/*
 * UserPrincipal será nossa classe UserDetails personalizada. 
 * Esta é a classe cujas instâncias serão retornadas de nosso UserDetailsService personalizado (CustomUserDetailsService).
 * Spring Security usará as informações armazenadas no objeto UserPrincipal para realizar autenticação e autorização.
 */
public class UsuarioPrincipal implements UserDetails {
	
	private static final long serialVersionUID = 1L;

	private Long id;
	
	private String nome;
	
	private String nomeUsuario;
	
	private String email;
	
	private String senha;
	
	private Collection<? extends GrantedAuthority> autoridades; 
	
	public UsuarioPrincipal(Long id, String nome, String nomeUsuario, String email, String senha,
			Collection<? extends GrantedAuthority> autoridades) {
		super();
		this.id = id;
		this.nome = nome;
		this.nomeUsuario = nomeUsuario;
		this.email = email;
		this.senha = senha;
		this.autoridades = autoridades;
	}
	
	public static UsuarioPrincipal criar(Usuario usuario) {
		List<GrantedAuthority> autoridades = usuario.getFuncoes().stream().map(funcao -> 
			new SimpleGrantedAuthority(funcao.getNome().name())
		).collect(Collectors.toList());
		return new UsuarioPrincipal(
				usuario.getId(),
				usuario.getNome(),
				usuario.getNomeUsuario(),
				usuario.getEmail(),
				usuario.getSenha(),
				autoridades);
	}
	
	public Long getId() {
		return id;
	}
	
	public String getNome() {
		return nome;
	}
	
	public String getEmail() {
		return email;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return autoridades;
	}

	@Override
	public String getPassword() {
		return senha;
	}

	@Override
	public String getUsername() {
		return nomeUsuario;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UsuarioPrincipal other = (UsuarioPrincipal) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

}
