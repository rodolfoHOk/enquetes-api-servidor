package br.com.hioktec.votacao.controlador.app;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.hioktec.votacao.modelo.app.Enquete;
import br.com.hioktec.votacao.requisicao.app.EnqueteRequest;
import br.com.hioktec.votacao.requisicao.app.VotoRequest;
import br.com.hioktec.votacao.resposta.ApiResponse;
import br.com.hioktec.votacao.resposta.app.EnqueteResponse;
import br.com.hioktec.votacao.resposta.app.PagedResponse;
import br.com.hioktec.votacao.seguranca.UsuarioAtual;
import br.com.hioktec.votacao.seguranca.UsuarioPrincipal;
import br.com.hioktec.votacao.servico.EnqueteService;
import br.com.hioktec.votacao.util.AppConstantes;

/**
 * Na EnqueteController, escreveremos as APIs Rest para:
 * 	Criar uma votação.
 * Obter uma lista paginada de enquetes classificadas por sua hora de criação.
 * Obter uma enquete pela enqueteId.
 * Votar em uma escolha em uma enquete.
 * 
 * A EnqueteController também usa um serviço chamado EnqueteService para validar e processar algumas das solicitações.
 * @author rodolfo
 */
@RestController
@RequestMapping("/api/enquetes")
public class EnqueteController {
	
	@Autowired
	private EnqueteService enqueteService;
	
	@GetMapping
	public PagedResponse<EnqueteResponse> getEnquetes(@UsuarioAtual UsuarioPrincipal usuarioAtual,
			@RequestParam(value = "pagina", defaultValue = AppConstantes.DEFAULT_PAGE_NUMBER) int pagina,
			@RequestParam(value = "tamanho", defaultValue = AppConstantes.DEFAULT_PAGE_SIZE) int tamanho){
		return enqueteService.getTodasEnquetes(usuarioAtual, pagina, tamanho);
	}
	
	@PostMapping
	@PreAuthorize("hasAuthority('FUNCAO_USUARIO')")
	public ResponseEntity<?> criarEnquete(@Valid @RequestBody EnqueteRequest enqueteRequest){
		Enquete enquete = enqueteService.criarEnquete(enqueteRequest);
		
		URI localizacao = ServletUriComponentsBuilder
				.fromCurrentRequest().path("/{enqueteId}")
				.buildAndExpand(enquete.getId()).toUri();
		
		return ResponseEntity.created(localizacao).body(new ApiResponse(true, "Enquete criada com sucesso"));
	}
	
	@GetMapping("/{enqueteId}")
	public EnqueteResponse getEnqueteById(@UsuarioAtual UsuarioPrincipal usuarioAtual, @PathVariable Long enqueteId) {
		return enqueteService.getEnqueteById(enqueteId, usuarioAtual);	
	}
	
	@PostMapping("/{enqueteId}/votos")
	@PreAuthorize("hasAuthority('FUNCAO_USUARIO')")
	public EnqueteResponse votar(@UsuarioAtual UsuarioPrincipal usuarioAtual,
			@PathVariable Long enqueteId, @Valid @RequestBody VotoRequest votoRequest) {
		return enqueteService.votarEObterEnqueteAtualizada(enqueteId, votoRequest, usuarioAtual);
	}

}
