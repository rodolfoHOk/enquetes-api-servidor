package br.com.hioktec.votacao.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.hioktec.votacao.modelo.TokenConfirmacao;
import br.com.hioktec.votacao.modelo.Usuario;
/**
 * Interface de comunicação com a entidade de persistência TokenConfirmacao (confirmação de email)
 * @author rodolfo
 */
@Repository
public interface TokenConfirmacaoRepository extends JpaRepository<TokenConfirmacao, Long>{
	
	Optional<TokenConfirmacao> findByToken(String token);
	
	Optional<TokenConfirmacao> findByUsuario(Usuario usuario); 
}
