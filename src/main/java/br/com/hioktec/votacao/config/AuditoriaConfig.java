package br.com.hioktec.votacao.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import br.com.hioktec.votacao.seguranca.UsuarioPrincipal;

/**
 * para habilitar a auditoria do JPA é necessário adicionar a anotação EnableJpaAuditing na classe principal
 * ou em alguma outra classe de configuração.
 * A auditoria irá popular automaticamente a criadoAs e o atualizadoAs quando for persistido uma entidade.
 * @author rodolfo
 *
 */
@Configuration
@EnableJpaAuditing
public class AuditoriaConfig {
	
	@Bean // criamos este bean para preencher automaticamente os campos criadoPor e atualizadoPor (app votação).
	public AuditorAware<Long> provedorAuditoria(){
		return new SpringSecurityAuditAwareImpl();
	}
}

/**
 * criamos esta classe para preencher automaticamente os campos criadoPor e atualizadoPor no bean provedorAuditoria da
 * classe AuditoriaConfig
 * @author rodolfo
 */
class SpringSecurityAuditAwareImpl implements AuditorAware<Long> {

	@Override
	public Optional<Long> getCurrentAuditor() {
		Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
		
		if(autenticacao == null || !autenticacao.isAuthenticated() || autenticacao instanceof AnonymousAuthenticationToken) {
			return Optional.empty();
		}
		
		UsuarioPrincipal usuarioPrincipal = (UsuarioPrincipal) autenticacao.getPrincipal();
		
		return Optional.ofNullable(usuarioPrincipal.getId());
	}
	
}
