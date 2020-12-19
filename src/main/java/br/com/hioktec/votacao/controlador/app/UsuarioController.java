package br.com.hioktec.votacao.controlador.app;

import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.hioktec.votacao.excecao.ResourceNotFoundException;
import br.com.hioktec.votacao.modelo.TokenConfirmacao;
import br.com.hioktec.votacao.modelo.Usuario;
import br.com.hioktec.votacao.repositorio.TokenConfirmacaoRepository;
import br.com.hioktec.votacao.repositorio.UsuarioRepository;
import br.com.hioktec.votacao.repositorio.app.EnqueteRepository;
import br.com.hioktec.votacao.repositorio.app.VotoRepository;
import br.com.hioktec.votacao.requisicao.app.AtualizarUsuarioRequest;
import br.com.hioktec.votacao.resposta.ApiResponse;
import br.com.hioktec.votacao.resposta.app.EnqueteResponse;
import br.com.hioktec.votacao.resposta.app.PagedResponse;
import br.com.hioktec.votacao.resposta.app.UsuarioCompleto;
import br.com.hioktec.votacao.resposta.app.UsuarioIdentidadeDisponivel;
import br.com.hioktec.votacao.resposta.app.UsuarioPerfil;
import br.com.hioktec.votacao.resposta.app.UsuarioSucinto;
import br.com.hioktec.votacao.seguranca.UsuarioAtual;
import br.com.hioktec.votacao.seguranca.UsuarioPrincipal;
import br.com.hioktec.votacao.servico.EnqueteService;
import br.com.hioktec.votacao.servico.EnvioEmailService;
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
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private TokenConfirmacaoRepository tokenConfirmacaoRepository;
	
	@Autowired
	private EnvioEmailService envioEmailService;

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
	
	@GetMapping("/usuarios/{id}/atualizar")
	@PreAuthorize("hasAuthority('FUNCAO_USUARIO')")
	public UsuarioCompleto getUsuarioCompleto(@PathVariable(value = "id") Long id) {
		Optional<Usuario> consultaUsuario = usuarioRepository.findById(id);
		Usuario usuario = consultaUsuario.get();
		UsuarioCompleto usuarioCompleto = new UsuarioCompleto(
				usuario.getId(),
				usuario.getNome(),
				usuario.getNomeUsuario(),
				usuario.getEmail());
		return usuarioCompleto;
	}
	
	@PutMapping("/usuarios/{id}/atualizar")
	@PreAuthorize("hasAuthority('FUNCAO_USUARIO')")
	public ResponseEntity<?> atualizarUsuario(@PathVariable(value = "id") Long id, @Valid @RequestBody AtualizarUsuarioRequest atualizarUsuarioRequest){
		Optional<Usuario> consultaUsuario = usuarioRepository.findById(id);
		Boolean mudou = false;
		Boolean mudouEmail = false;
		if (consultaUsuario.isPresent()) {
			Usuario usuario = consultaUsuario.get();
			if (!atualizarUsuarioRequest.getNome().equals(usuario.getNome())) {
				usuario.setNome(atualizarUsuarioRequest.getNome());
				mudou = true;
			}
			if (!atualizarUsuarioRequest.getNomeUsuario().equals(usuario.getNomeUsuario())) {
				if(usuarioRepository.existsByNomeUsuario(atualizarUsuarioRequest.getNomeUsuario())) {
					return new ResponseEntity<>(
							new ApiResponse(false, "Nome de usuário já existe"), HttpStatus.BAD_REQUEST);
				}
				usuario.setNomeUsuario(atualizarUsuarioRequest.getNomeUsuario());
				mudou = true;
			}
			if (!atualizarUsuarioRequest.getEmail().equals(usuario.getEmail())) {
				if(usuarioRepository.existsByEmail(atualizarUsuarioRequest.getEmail())) {
					return new ResponseEntity<>(
							new ApiResponse(false, "Email já existe"), HttpStatus.BAD_REQUEST);
				}
				usuario.setEmail(atualizarUsuarioRequest.getEmail());
				usuario.setHabilitado(false);
				mudou = true;
				mudouEmail = true;
			}
			if (atualizarUsuarioRequest.getSenha().length() > 0) {
				usuario.setSenha(passwordEncoder.encode(atualizarUsuarioRequest.getSenha()));
				mudou = true;
			}
			if (mudou) {
				Usuario usuarioCadastrado = usuarioRepository.save(usuario);
				if (mudouEmail) {
					// mandar email de verificação.
					String token = UUID.randomUUID().toString();
					TokenConfirmacao tokenConfirmacao =	new TokenConfirmacao(token, usuarioCadastrado);
					TokenConfirmacao tokenSalvo = tokenConfirmacaoRepository.save(tokenConfirmacao);
					
					SimpleMailMessage mensagemEmail = new SimpleMailMessage();
					mensagemEmail.setTo(usuarioCadastrado.getEmail());
					mensagemEmail.setSubject("Complete o registro para a aplicação WEB (Site) Votação");
					mensagemEmail.setText("Para confirmar sua conta, por favor click aqui: "
							+ "http://localhost:5000/api/auten/confirmar-conta?token=" + tokenSalvo.getToken());
					
					envioEmailService.enviarEmail(mensagemEmail);
					return ResponseEntity.ok(new ApiResponse(true, "Usuario atualizado com sucesso. "
							+ "Um email de confirmação foi enviado."));
				} else {
					return ResponseEntity.ok(new ApiResponse(true, "Usuario atualizado com sucesso."));
				}
			}
			return ResponseEntity.badRequest().body(new ApiResponse(false, "Não detectado mudanças para atualizar"));
		} else {
			return ResponseEntity.badRequest().body(new ApiResponse(false, "Usuario não encontrado."));
		}
	}
	
}
