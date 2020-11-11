package br.com.hioktec.votacao.repositorio.app;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.hioktec.votacao.modelo.app.Enquete;

@Repository
public interface EnqueteRepository extends JpaRepository<Enquete, Long>{
	
	Optional<Enquete> findById(Long enqueteId);
	
	Page<Enquete> findByCriadoPor(Long usuarioId, Pageable pageable);
	
	long countByCriadoPor(Long usuarioId);
	
	List<Enquete> findByIdIn(List<Long> enquetesIds);
	
	List<Enquete> findByIdIn(List<Long> enquetesIds, Sort sort);
}
