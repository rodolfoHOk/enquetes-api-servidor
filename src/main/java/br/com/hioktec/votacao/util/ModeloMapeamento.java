package br.com.hioktec.votacao.util;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import br.com.hioktec.votacao.modelo.Usuario;
import br.com.hioktec.votacao.modelo.app.Enquete;
import br.com.hioktec.votacao.resposta.app.EnqueteResponse;
import br.com.hioktec.votacao.resposta.app.OpcaoResponse;
import br.com.hioktec.votacao.resposta.app.UsuarioSucinto;

/**
 * Estaremos mapeando a entidade Enquete para uma carga útil EnqueteResponse que contém um monte de informações
 *  como o nome do criador da Enquete, contagens de votos de cada escolha na Enquete, a escolha em que o usuário
 *  conectado no momento votou, se a Enquete expirou etc.
 * Todas essas informações serão utilizadas no cliente front-end para apresentação.
 * @author rodolfo
 */
public class ModeloMapeamento {
	
	public static EnqueteResponse mapEnqueteToEnqueteResponse(Enquete enquete, Map<Long, Long> mapaVotosPorOpcao,
			Usuario criador, Long votoUsuario) {
		
		EnqueteResponse enqueteResponse = new EnqueteResponse();
		enqueteResponse.setId(enquete.getId());
		enqueteResponse.setPergunta(enquete.getPergunta());
		enqueteResponse.setDataHoraCriacao(enquete.getCriadoAs());
		enqueteResponse.setDataHoraExpiracao(enquete.getDataHoraExpiracao());
		Instant agora = Instant.now();
		enqueteResponse.setIsExpirado(enquete.getDataHoraExpiracao().isBefore(agora));
		
		List<OpcaoResponse> opcaoResponses = enquete.getOpcoes().stream().map(opcao -> {
			OpcaoResponse opcaoResponse = new OpcaoResponse();
			opcaoResponse.setId(opcao.getId());
			opcaoResponse.setTexto(opcao.getTexto());
			if(mapaVotosPorOpcao.containsKey(opcao.getId())) {
				opcaoResponse.setContagemVotos(mapaVotosPorOpcao.get(opcao.getId()));
			} else {
				opcaoResponse.setContagemVotos(0L);
			}
			return opcaoResponse;
		}).collect(Collectors.toList());
		
		enqueteResponse.setOpcoes(opcaoResponses);
		UsuarioSucinto criadorSucinto = new UsuarioSucinto(criador.getId(), criador.getNomeUsuario(), criador.getNome());
		enqueteResponse.setCriadoPor(criadorSucinto);
		
		if(votoUsuario != null) {
			enqueteResponse.setOpcaoSelecionada(votoUsuario);
		}
		
		long totalVotos = enqueteResponse.getOpcoes().stream().mapToLong(OpcaoResponse::getContagemVotos).sum();
		enqueteResponse.setTotalVotos(totalVotos);
		
		return enqueteResponse;
	}

}
