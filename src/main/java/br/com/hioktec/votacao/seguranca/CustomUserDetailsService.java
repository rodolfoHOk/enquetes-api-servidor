package br.com.hioktec.votacao.seguranca;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.hioktec.votacao.modelo.Usuario;
import br.com.hioktec.votacao.repositorio.UsuarioRepository;

/*
 * Para autenticar um usuário ou realizar várias verificações baseadas em funções,
 *  a segurança do Spring precisa carregar os detalhes dos usuários de alguma forma.
 * Para tanto, consiste em uma interface chamada UserDetailsService que possui um único método que carrega
 *  um usuário baseado em nome de usuário.
 *  
 * Observe que, o método loadUserByUsername () retorna um objeto UserDetails que Spring Security
 *  usa para realizar várias autenticações e validações baseadas em funções.
 * Em nossa implementação, também definiremos uma classe UserPrincipal personalizada que implementará
 *  a interface UserDetails e retornará o objeto UserPrincipal do método loadUserByUsername().
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String nomeUsuarioOrEmail) throws UsernameNotFoundException {
		// Permitiremos que as pessoas façam login com o nome de usuário ou o e-mail.
		Usuario usuario = usuarioRepository.findByNomeUsuarioOrEmail(nomeUsuarioOrEmail, nomeUsuarioOrEmail)
				.orElseThrow(() -> new UsernameNotFoundException(
						"Usuario não encontrado com o nome de usuário ou email informado"));
		return UsuarioPrincipal.criar(usuario);
	}
	
	// Este método é usado pela JWTAuthenticationFilter
	@Transactional
	public UserDetails loadUserById(Long id) {
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new UsernameNotFoundException(
						"Usuario não encontrado com o id: " + id));
		return UsuarioPrincipal.criar(usuario);
	}
	
}
