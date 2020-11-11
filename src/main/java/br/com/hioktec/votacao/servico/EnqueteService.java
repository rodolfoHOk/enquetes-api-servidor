package br.com.hioktec.votacao.servico;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import br.com.hioktec.votacao.excecao.BadRequestException;
import br.com.hioktec.votacao.excecao.ResourceNotFoundException;
import br.com.hioktec.votacao.modelo.Usuario;
import br.com.hioktec.votacao.modelo.app.ContagemVotosPorOpcao;
import br.com.hioktec.votacao.modelo.app.Enquete;
import br.com.hioktec.votacao.modelo.app.Opcao;
import br.com.hioktec.votacao.modelo.app.Voto;
import br.com.hioktec.votacao.repositorio.UsuarioRepository;
import br.com.hioktec.votacao.repositorio.app.EnqueteRepository;
import br.com.hioktec.votacao.repositorio.app.VotoRepository;
import br.com.hioktec.votacao.requisicao.app.EnqueteRequest;
import br.com.hioktec.votacao.requisicao.app.VotoRequest;
import br.com.hioktec.votacao.resposta.app.EnqueteResponse;
import br.com.hioktec.votacao.resposta.app.PagedResponse;
import br.com.hioktec.votacao.seguranca.UsuarioPrincipal;
import br.com.hioktec.votacao.util.AppConstantes;
import br.com.hioktec.votacao.util.ModeloMapeamento;

/**
 * Ambos os controladores EnqueteController e UsuarioController usam a classe EnqueteService para obter
 *  a lista de pesquisas formatadas na forma de carga úteis EnqueteResponse que são retornadas aos clientes.
 * @author rodolfo
 */
@Service
public class EnqueteService {
	
	@Autowired
	private EnqueteRepository enqueteRepository;
	
	@Autowired
	private VotoRepository votoRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(EnqueteService.class);
	
	/**
	 * @param usuarioAtual
	 * @param pagina
	 * @param tamanho
	 * @return PagedResponse com todas as enquetes existentes
	 */
	public PagedResponse<EnqueteResponse> getTodasEnquetes(UsuarioPrincipal usuarioAtual, int pagina, int tamanho){
		validarNumeroPaginaETamanho(pagina, tamanho);
		
		// recupera enquetes
		Pageable pageable = PageRequest.of(pagina, tamanho, Sort.Direction.DESC, "criadoAs");
		Page<Enquete> enquetes = enqueteRepository.findAll(pageable);
		
		if(enquetes.getNumberOfElements() == 0) {
			return new PagedResponse<>(Collections.emptyList(), enquetes.getNumber(), enquetes.getSize(),
					enquetes.getTotalElements(), enquetes.getTotalPages(), enquetes.isLast());
		}
		
		// Mapear enquetes para EnqueteResponses contendo contagens de votos e detalhes do criador da enquete
		List<Long> enquetesIds = enquetes.map(Enquete::getId).getContent();
		Map<Long, Long> mapaContagemVotosPorOpcao = getMapaContagemVotosPorOpcao(enquetesIds);
		Map<Long, Long> mapaVotacaoUsuario = getMapaVotacaoUsuario(usuarioAtual, enquetesIds);
		Map<Long, Usuario> mapaCriadoresEnquetes = getMapaCriadoresEnquetes(enquetes.getContent());
		
		List<EnqueteResponse> enqueteResponses = enquetes.map(enquete -> {
			return ModeloMapeamento.mapEnqueteToEnqueteResponse(
					enquete,
					mapaContagemVotosPorOpcao,
					mapaCriadoresEnquetes.get(enquete.getCriadoPor()),
					mapaVotacaoUsuario == null ? null : mapaVotacaoUsuario.getOrDefault(enquete.getId(), null));
		}).getContent();
		
		return new PagedResponse<EnqueteResponse>(enqueteResponses, enquetes.getNumber(), enquetes.getSize(),
				enquetes.getTotalElements(), enquetes.getTotalPages(), enquetes.isLast());
	}
	
	/**
	 * @param nomeUsuario
	 * @param usuarioAtual
	 * @param pagina
	 * @param tamanho
	 * @return PagedResponse com todas as enquetes criadas pelo nome de usuário fornecido.
	 */
	public PagedResponse<EnqueteResponse> getEnquetesCriadaPor(String nomeUsuario, UsuarioPrincipal usuarioAtual,
				int pagina, int tamanho){
		validarNumeroPaginaETamanho(pagina, tamanho);
		
		Usuario usuario = usuarioRepository.findByNomeUsuario(nomeUsuario)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário", "nomeUsuario", nomeUsuario));
		
		// Recupera todas as enquetes criadas pelo nome de usuário fornecido
		Pageable pageable = PageRequest.of(pagina, tamanho, Sort.Direction.DESC, "criadoAs");
		Page<Enquete> enquetes = enqueteRepository.findByCriadoPor(usuario.getId(), pageable);
		
		if(enquetes.getNumberOfElements() == 0) {
			return new PagedResponse<>(Collections.emptyList(), enquetes.getNumber(), enquetes.getSize(),
					enquetes.getTotalElements(), enquetes.getTotalPages(), enquetes.isLast());
		}
		
		// Mapear enquetes para EnqueteResponses contendo contagens de votos e detalhes do criador de enquetes
		List<Long> enquetesIds = enquetes.map(Enquete::getId).getContent();
		Map<Long, Long> mapaVotosPorOpcao = getMapaContagemVotosPorOpcao(enquetesIds);
		Map<Long, Long> mapaVotacaoUsuario = getMapaVotacaoUsuario(usuarioAtual, enquetesIds);
		
		List<EnqueteResponse> enqueteResponses = enquetes.map(enquete -> {
			return ModeloMapeamento.mapEnqueteToEnqueteResponse(
					enquete,
					mapaVotosPorOpcao,
					usuario,
					mapaVotacaoUsuario == null ? null: mapaVotacaoUsuario.getOrDefault(enquete.getId(), null));
		}).getContent();
		
		return new PagedResponse<EnqueteResponse>(enqueteResponses, enquetes.getNumber(), enquetes.getSize(),
				enquetes.getTotalElements(), enquetes.getTotalPages(), enquetes.isLast());
	}
	
	/**
	 * @param nomeUsuario
	 * @param usuarioAtual
	 * @param pagina
	 * @param tamanho
	 * @return PagedResponse com as enquetes votadas pelo nome de usuário fornecido.
	 */
	public PagedResponse<EnqueteResponse> getEnquetesVotadasPor(String nomeUsuario, UsuarioPrincipal usuarioAtual,
				int pagina, int tamanho){
		
		Usuario usuario = usuarioRepository.findByNomeUsuario(nomeUsuario)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário", "nomeUsuario", nomeUsuario));
		
		// Recupera todas as enquetesIds em que o nome de usuário fornecido votou.		
		Pageable pageable = PageRequest.of(pagina, tamanho, Sort.Direction.DESC, "criadoAs");
		Page<Long> enquetesVotadasPeloUsuarioIds = votoRepository.findEnqueteVotadaIdsByUsuarioId(usuario.getId(), pageable);
		
		if(enquetesVotadasPeloUsuarioIds.getNumberOfElements() == 0) {
			return new PagedResponse<>(Collections.emptyList(), enquetesVotadasPeloUsuarioIds.getNumber(),
					enquetesVotadasPeloUsuarioIds.getSize(), enquetesVotadasPeloUsuarioIds.getTotalElements(),
					enquetesVotadasPeloUsuarioIds.getTotalPages(), enquetesVotadasPeloUsuarioIds.isLast());
		}
		
		// Recupera todos os detalhes das enquetes dos enquetesIds votados.
		List<Long> enquetesIds = enquetesVotadasPeloUsuarioIds.getContent();
		Sort sort = Sort.by(Sort.Direction.DESC, "criadoAs");
		List<Enquete> enquetes = enqueteRepository.findByIdIn(enquetesIds, sort);
		
		Map<Long,Long> mapaVotosPorOpcao = getMapaContagemVotosPorOpcao(enquetesIds);
		Map<Long, Long> mapaVotacaoUsuario = getMapaVotacaoUsuario(usuarioAtual, enquetesIds);
		Map<Long, Usuario> mapaCriadoresEnquetes = getMapaCriadoresEnquetes(enquetes); 
		
		List<EnqueteResponse> enqueteResponses = enquetes.stream().map(enquete -> {
			return ModeloMapeamento.mapEnqueteToEnqueteResponse(
					enquete,
					mapaVotosPorOpcao,
					mapaCriadoresEnquetes.get(enquete.getCriadoPor()),
					mapaVotacaoUsuario == null ? null : mapaVotacaoUsuario.getOrDefault(enquete.getId(), null));
		}).collect(Collectors.toList());
		
		return new PagedResponse<EnqueteResponse>(enqueteResponses, enquetesVotadasPeloUsuarioIds.getNumber(),
				enquetesVotadasPeloUsuarioIds.getSize(), enquetesVotadasPeloUsuarioIds.getTotalElements(),
				enquetesVotadasPeloUsuarioIds.getTotalPages(), enquetesVotadasPeloUsuarioIds.isLast());
	}
	
	/**
	 * Cria uma enquete
	 * @param enqueteRequest
	 * @return enquete salva no repositório.
	 */
	public Enquete criarEnquete(EnqueteRequest enqueteRequest) {
		Enquete enquete = new Enquete();
		enquete.setPergunta(enqueteRequest.getPergunta());
		
		enqueteRequest.getOpcoes().forEach(opcaoRequest -> {
			enquete.adicionarOpcao(new Opcao(opcaoRequest.getTexto()));
		});
		
		Instant agora = Instant.now();
		Instant dataHoraExpiracao = agora.plus(Duration.ofDays(enqueteRequest.getDuracaoEnquete().getDias()))
				.plus(Duration.ofHours(enqueteRequest.getDuracaoEnquete().getHoras()));
		
		enquete.setDataHoraExpiracao(dataHoraExpiracao);
		
		return enqueteRepository.save(enquete);
	}
	
	/**
	 * pega uma enquete por id
	 * @param enqueteId
	 * @param usuarioAtual
	 * @return EnqueteResponse pela enqueteId fornecida.
	 */
	public EnqueteResponse getEnqueteById(Long enqueteId, UsuarioPrincipal usuarioAtual) {
		Enquete enquete = enqueteRepository.findById(enqueteId)
				.orElseThrow(()-> new ResourceNotFoundException("Enquete", "id", enqueteId));
		// Recupera contagens de votos de cada escolha pertencente à enquete atual
		List<ContagemVotosPorOpcao> votos = votoRepository.countByEnqueteIdGroupByOpcaoId(enqueteId);
		Map<Long, Long> mapaVotosPorOpcao = votos.stream()
				.collect(Collectors.toMap(ContagemVotosPorOpcao::getOpcaoId, ContagemVotosPorOpcao::getContagemVotos));
		// Recupera detalhes do criador da enquete
		Usuario criador = usuarioRepository.findById(enquete.getCriadoPor())
				.orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", enquete.getCriadoPor()));
		// Recupera voto feito pelo usuário logado
		Voto votoUsuario = null;
		if(usuarioAtual != null) {
			votoUsuario = votoRepository.findByUsuarioIdAndEnqueteId(usuarioAtual.getId(), enqueteId);
		}
		
		return ModeloMapeamento.mapEnqueteToEnqueteResponse(enquete, mapaVotosPorOpcao, criador,
				votoUsuario != null ? votoUsuario.getOpcao().getId() : null);
	}
	
	/**
	 * vota, salva o voto da requisição, e obtém de volta a enquete atualizada.
	 * @param enqueteId
	 * @param votoRequest
	 * @param usuarioAtual
	 * @return EnqueteResponse da enquete atualizada com o voto do usuário.
	 */
	public EnqueteResponse votarEObterEnqueteAtualizada(Long enqueteId, VotoRequest votoRequest, UsuarioPrincipal usuarioAtual) {
		// Salva o voto na base de dados
		Enquete enquete = enqueteRepository.findById(enqueteId)
				.orElseThrow(() -> new ResourceNotFoundException("Enquete", "id", enqueteId));
		
		if(enquete.getDataHoraExpiracao().isBefore(Instant.now())) {
			throw new BadRequestException("Desculpe! Esta enquete já expirou");
		}
		
		Usuario usuario = usuarioRepository.getOne(usuarioAtual.getId());
		
		Opcao opcaoSelecionada = enquete.getOpcoes().stream()
				.filter(opcao -> opcao.getId().equals(votoRequest.getOpcaoId()))
				.findFirst()
				.orElseThrow(() -> new ResourceNotFoundException("Opcao", "id", votoRequest.getOpcaoId()));
		
		Voto voto = new Voto();
		voto.setEnquete(enquete);
		voto.setUsuario(usuario);
		voto.setOpcao(opcaoSelecionada);
		
		try {
			voto = votoRepository.save(voto);
		} catch (DataIntegrityViolationException ex) {
			logger.info("O usuário {} já votou na Enquete {}", usuarioAtual.getId (), enqueteId);
			throw new BadRequestException("Desculpe! Você já votou nesta enquete");
		}
		// Voto salvo, retornar a resposta da enquete atualizada agora
		// Recuperar contagens de votos de cada escolha pertencente à enquete atual
		List<ContagemVotosPorOpcao> votos = votoRepository.countByEnqueteIdGroupByOpcaoId(enqueteId);
		
		Map<Long, Long> mapaVotosPorOpcao = votos.stream()
				.collect(Collectors.toMap(ContagemVotosPorOpcao::getOpcaoId, ContagemVotosPorOpcao::getContagemVotos));
		
		// Recuperar detalhes do criador da enquete
		Usuario criador = usuarioRepository.findById(enquete.getCriadoPor())
				.orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", enquete.getCriadoPor()));
		
		return ModeloMapeamento.mapEnqueteToEnqueteResponse(enquete, mapaVotosPorOpcao, criador, voto.getOpcao().getId());
	}	
		

	// métodos reutilizados nos métodos públicos.
	
	private void validarNumeroPaginaETamanho(int pagina, int tamanho) {
		if(pagina < 0) {
			throw new BadRequestException("Número da página não pode ser menor que zero");
		}
		if(tamanho > AppConstantes.MAX_PAGE_SIZE) {
			throw new BadRequestException("Tamanho da página não pode ser maior que " + AppConstantes.MAX_PAGE_SIZE);
		}
	}
	
	private Map<Long, Long> getMapaContagemVotosPorOpcao(List<Long> enquetesIds) {
		// Recuperar contagens de voto de cada escolha pertencente a determinada enquetesIds
		List<ContagemVotosPorOpcao> votos = votoRepository.countByEnqueteIdInGroupByOpcaoId(enquetesIds);
		
		Map<Long, Long> mapaVotosPorOpcao = votos.stream()
				.collect(Collectors.toMap(ContagemVotosPorOpcao::getOpcaoId, ContagemVotosPorOpcao::getContagemVotos));
		return mapaVotosPorOpcao;
	}
	
	private Map<Long, Long> getMapaVotacaoUsuario(UsuarioPrincipal usuarioAtual, List<Long> enquetesIds) {
		// Recuperar votos feitos pelo usuário conectado aos enquetesIds fornecidos
		Map<Long, Long> mapaVotacaoUsuario = null;
		if(usuarioAtual != null) {
			List<Voto> votosUsuario = votoRepository.findByUsuarioIdAndEnqueteIdIn(usuarioAtual.getId(), enquetesIds);
			mapaVotacaoUsuario = votosUsuario.stream()
					.collect(Collectors.toMap(voto -> voto.getEnquete().getId(), voto -> voto.getOpcao().getId()));
		}
		return mapaVotacaoUsuario;
	}

	private Map<Long, Usuario> getMapaCriadoresEnquetes(List<Enquete> enquetes) {
		// Obter detalhes do criador de enquetes da lista de enquetes fornecida
		List<Long> criadoresIds = enquetes.stream().map(Enquete::getCriadoPor).distinct().collect(Collectors.toList());
		List<Usuario> criadores = usuarioRepository.findByIdIn(criadoresIds);
		Map<Long, Usuario> mapaCriadoresEnquetes = criadores.stream()
				.collect(Collectors.toMap(Usuario::getId, Function.identity()));
		return mapaCriadoresEnquetes;
	}

}

