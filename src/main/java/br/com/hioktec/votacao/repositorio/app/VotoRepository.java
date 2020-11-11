package br.com.hioktec.votacao.repositorio.app;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.hioktec.votacao.modelo.app.ContagemVotosPorOpcao;
import br.com.hioktec.votacao.modelo.app.Voto;

@Repository
public interface VotoRepository extends JpaRepository<Voto, Long>{
	
	@Query("SELECT NEW br.com.hioktec.votacao.modelo.app.ContagemVotosPorOpcao(v.opcao.id, count(v.id)) FROM Voto v WHERE v.enquete.id in :enquetesIds GROUP BY v.opcao.id")
	List<ContagemVotosPorOpcao> countByEnqueteIdInGroupByOpcaoId(@Param("enquetesIds") List<Long> enquetesIds);
	
	@Query("SELECT NEW br.com.hioktec.votacao.modelo.app.ContagemVotosPorOpcao(v.opcao.id, count(v.id)) FROM Voto v WHERE v.enquete.id = :enqueteId GROUP BY v.opcao.id")
	List<ContagemVotosPorOpcao> countByEnqueteIdGroupByOpcaoId(@Param("enqueteId") Long enqueteId);
	
	@Query("SELECT v FROM Voto v WHERE v.usuario.id = :usuarioId and v.enquete.id in :enquetesIds")
	List<Voto> findByUsuarioIdAndEnqueteIdIn(@Param("usuarioId") Long usuarioId, @Param("enquetesIds") List<Long> enquetesIds);
	
	@Query("SELECT v FROM Voto v WHERE v.usuario.id = :usuarioId and v.enquete.id = :enqueteId")
	Voto findByUsuarioIdAndEnqueteId(@Param("usuarioId") Long usuarioId, @Param("enqueteId") Long enqueteId);
	
	@Query("SELECT COUNT(v.id) FROM Voto v WHERE v.usuario.id = :usuarioId")
	long countByUsuarioId(@Param("usuarioId") Long usuarioId);
	
	@Query("SELECT v.enquete.id FROM Voto v WHERE v.usuario.id = :usuarioId")
	Page<Long> findEnqueteVotadaIdsByUsuarioId(@Param("usuarioId") Long usuarioId, Pageable pageable);
}
