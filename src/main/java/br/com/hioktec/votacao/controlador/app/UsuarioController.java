package br.com.hioktec.votacao.controlador.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.hioktec.votacao.excecao.ResourceNotFoundException;
import br.com.hioktec.votacao.modelo.Usuario;
import br.com.hioktec.votacao.repositorio.UsuarioRepository;
import br.com.hioktec.votacao.repositorio.app.EnqueteRepository;
import br.com.hioktec.votacao.repositorio.app.VotoRepository;
import br.com.hioktec.votacao.resposta.app.EnqueteResponse;
import br.com.hioktec.votacao.resposta.app.PagedResponse;
import br.com.hioktec.votacao.resposta.app.UsuarioIdentidadeDisponivel;
import br.com.hioktec.votacao.resposta.app.UsuarioPerfil;
import br.com.hioktec.votacao.resposta.app.UsuarioSucinto;
import br.com.hioktec.votacao.seguranca.UsuarioAtual;
import br.com.hioktec.votacao.seguranca.UsuarioPrincipal;
import br.com.hioktec.votacao.servico.EnqueteService;
import br.com.hioktec.votacao.util.AppConstantes;

@RestController
@RequestMapping("/api")
public class UsuarioController {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private EnqueteRepository enqueteRepository;
	
	@Autowired
	private VotoRepository votoRepository;
	
	@Autowired
	private EnqueteService enqueteService;

	@GetMapping("/usuario/eu")
	@PreAuthorize("hasAuthority('FUNCAO_USUARIO')")
	public UsuarioSucinto getUsuarioAtual(@UsuarioAtual UsuarioPrincipal usuarioAtual) {
		UsuarioSucinto usuarioSucinto = new UsuarioSucinto(usuarioAtual.getId(),
				usuarioAtual.getUsername(), usuarioAtual.getNome());
		return usuarioSucinto;
	}
	
	@GetMapping("/usuario/checarNomeUsuarioDisponivel")
	public UsuarioIdentidadeDisponivel checarNomeUsuarioDisponivel(@RequestParam(value = "nomeUsuario") String nomeUsuario) {
		Boolean isDisponivel = !usuarioRepository.existsByNomeUsuario(nomeUsuario);
		return new UsuarioIdentidadeDisponivel(isDisponivel);
	}
	
	@GetMapping("/usuario/checarEmailDisponivel")
	public UsuarioIdentidadeDisponivel checarEmailDisponivel(@RequestParam(value = "email") String email) {
		Boolean isDisponivel = !usuarioRepository.existsByEmail(email);
		return new UsuarioIdentidadeDisponivel(isDisponivel);
	}
	
	@GetMapping("/usuarios/{nomeUsuario}")
	public UsuarioPerfil getUsuarioPerfil(@PathVariable(value = "nomeUsuario") String nomeUsuario) {
		Usuario usuario = usuarioRepository.findByNomeUsuario(nomeUsuario)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário", "nomeUsuario", nomeUsuario));
		long contagemEnquetes = enqueteRepository.countByCriadoPor(usuario.getId());
		long contagemVotos = votoRepository.countByUsuarioId(usuario.getId());
		UsuarioPerfil usuarioPerfil = new UsuarioPerfil(usuario.getId(), usuario.getNomeUsuario(), usuario.getNome(),
				usuario.getCriadoAs(), contagemEnquetes, contagemVotos);
		return usuarioPerfil;
	}
	
	@GetMapping("/usuarios/{nomeUsuario}/enquetes")
	public PagedResponse<EnqueteResponse> getEnquetesCriadaPor(
			@PathVariable(value = "nomeUsuario") String nomeUsuario,
			@UsuarioAtual UsuarioPrincipal usuarioAtual, 
			@RequestParam(value = "pagina", defaultValue = AppConstantes.DEFAULT_PAGE_NUMBER) int pagina,
			@RequestParam(value = "tamanho", defaultValue = AppConstantes.DEFAULT_PAGE_SIZE) int tamanho){
		return enqueteService.getEnquetesCriadaPor(nomeUsuario, usuarioAtual, pagina, tamanho);
	}
	
	@GetMapping("/usuarios/{nomeUsuario}/votos")
	public PagedResponse<EnqueteResponse> getEnquetesVotadasPor(
			@PathVariable(value = "nomeUsuario") String nomeUsuario,
			@UsuarioAtual UsuarioPrincipal usuarioAtual, 
			@RequestParam(value = "pagina", defaultValue = AppConstantes.DEFAULT_PAGE_NUMBER) int pagina,
			@RequestParam(value = "tamanho", defaultValue = AppConstantes.DEFAULT_PAGE_SIZE) int tamanho){
		return enqueteService.getEnquetesVotadasPor(nomeUsuario, usuarioAtual, pagina, tamanho);
	}
	
}
