package br.com.hioktec.votacao.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.hioktec.votacao.modelo.NomeFuncao;
import br.com.hioktec.votacao.modelo.Funcao;

@Repository
public interface FuncaoRepository extends JpaRepository<Funcao, Long> {
	Optional<Funcao> findByNome(NomeFuncao nomeFuncao);
}
