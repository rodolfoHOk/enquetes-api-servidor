package br.com.hioktec.votacao.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.hioktec.votacao.modelo.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
	Optional<Usuario> findByEmail(String email);
		
	Optional<Usuario> findByNomeUsuarioOrEmail(String nomeUsuario, String email);
	
	Optional<Usuario> findByNomeUsuario(String nomeUsuario);
	
	List<Usuario> findByIdIn(List<Long> usuarioIds);
	
	Boolean existsByNomeUsuario(String nomeUsuario);
	
	Boolean existsByEmail(String email);
}
