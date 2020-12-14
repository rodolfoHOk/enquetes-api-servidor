package br.com.hioktec.votacao.controlador;

import java.net.URI;
import java.sql.Date;
import java.util.Calendar;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.hioktec.votacao.excecao.AppException;
import br.com.hioktec.votacao.modelo.NomeFuncao;
import br.com.hioktec.votacao.modelo.TokenConfirmacao;
import br.com.hioktec.votacao.modelo.Funcao;
import br.com.hioktec.votacao.modelo.Usuario;
import br.com.hioktec.votacao.repositorio.FuncaoRepository;
import br.com.hioktec.votacao.repositorio.TokenConfirmacaoRepository;
import br.com.hioktec.votacao.repositorio.UsuarioRepository;
import br.com.hioktec.votacao.requisicao.CadastroRequest;
import br.com.hioktec.votacao.requisicao.LoginRequest;
import br.com.hioktec.votacao.requisicao.ReenviarEmailRequest;
import br.com.hioktec.votacao.resposta.ApiResponse;
import br.com.hioktec.votacao.resposta.AutenticacaoJWTResponse;
import br.com.hioktec.votacao.seguranca.JwtTokenProvider;
import br.com.hioktec.votacao.servico.EnvioEmailService;

/**
 * AutenticacaoController que contém APIs para login e registro de usuários.
 * @author rodolfo
 *
 */
@RestController
@RequestMapping("/api/auten")
public class AutenticacaoController {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtTokenProvider tokenProvider;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private FuncaoRepository funcaoRepository;
	
	@Autowired
	private TokenConfirmacaoRepository tokenConfirmacaoRepository;
	
	@Autowired
	private EnvioEmailService envioEmailService;
	
	@PostMapping("/acessar")
	public ResponseEntity<?> autenticarUsuario(@Valid @RequestBody LoginRequest loginRequest){
		
		Authentication autenticacao = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						loginRequest.getNomeUsuarioOuEmail(),
						loginRequest.getSenha()));
		
		SecurityContextHolder.getContext().setAuthentication(autenticacao);
		
		String jwt = tokenProvider.gerarToken(autenticacao);
		
		Optional<Usuario> usuario = usuarioRepository.findByNomeUsuarioOrEmail(
				loginRequest.getNomeUsuarioOuEmail(), loginRequest.getNomeUsuarioOuEmail());
		
		if(usuario.get().isHabilitado()) { // adicionada para verificação de email.
			return ResponseEntity.ok(new AutenticacaoJWTResponse(jwt));
		} else {
			return new ResponseEntity<>(
					new ApiResponse(false, "Conta não ativada. Verifique o código de ativação em seu email e tente novamente."),
					HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/inscrever")
	public ResponseEntity<?> registrarUsuario(@Valid @RequestBody CadastroRequest cadastroRequest){
		
			if(usuarioRepository.existsByNomeUsuario(cadastroRequest.getNomeUsuario())) {
				return new ResponseEntity<>(
						new ApiResponse(false, "Nome de usuário já existe"), HttpStatus.BAD_REQUEST);
			}
			
			if(usuarioRepository.existsByEmail(cadastroRequest.getEmail())) {
				return new ResponseEntity<>(
						new ApiResponse(false, "Email já existe"), HttpStatus.BAD_REQUEST);
			}
			
			Usuario usuario = new Usuario(
					cadastroRequest.getNome(),
					cadastroRequest.getNomeUsuario(),
					cadastroRequest.getEmail(),
					cadastroRequest.getSenha());
			
			usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
			
			Funcao funcaoUsuario = funcaoRepository.findByNome(NomeFuncao.FUNCAO_USUARIO)
					.orElseThrow(() -> new AppException("Função de usuário não configurado"));
			
			usuario.setFuncoes(Collections.singleton(funcaoUsuario));
			
			Usuario usuarioCadastrado = usuarioRepository.save(usuario);
			
			// adicionadas para mandar email de verificação.
			String token = UUID.randomUUID().toString();
			TokenConfirmacao tokenConfirmacao =	new TokenConfirmacao(token, usuarioCadastrado);
			TokenConfirmacao tokenSalvo = tokenConfirmacaoRepository.save(tokenConfirmacao);
			
			SimpleMailMessage mensagemEmail = new SimpleMailMessage();
			mensagemEmail.setTo(usuarioCadastrado.getEmail());
			mensagemEmail.setSubject("Complete o registro para a aplicação WEB (Site) Votação");
			mensagemEmail.setText("Para confirmar sua conta, por favor click aqui: "
					+ "http://localhost:5000/api/auten/confirmar-conta?token=" + tokenSalvo.getToken());
			
			envioEmailService.enviarEmail(mensagemEmail);
			//
			
			URI localizacao= ServletUriComponentsBuilder
					.fromCurrentContextPath().path("/api/usuarios/{nomeUsuario}")
					.buildAndExpand(usuarioCadastrado.getNomeUsuario()).toUri();
			
			return ResponseEntity.created(localizacao).body(new ApiResponse(true,
					"Usuário registrado com sucesso. Um email de comfirmação foi enviado para: " 
							+ usuarioCadastrado.getEmail()));
	}
	
	@GetMapping("/confirmar-conta")
	public ResponseEntity<?> confirmarContaUsuario(@RequestParam("token") String token) {
		
		Optional<TokenConfirmacao> tokenConfirmacao = tokenConfirmacaoRepository.findByToken(token);
		
		if(tokenConfirmacao.isPresent()) {
			Date agora = new Date(Calendar.getInstance().getTime().getTime());
			Date expiracaoToken = tokenConfirmacao.get().getDataExpiracao();
			if (agora.after(expiracaoToken)) {
				URI uri = URI.create("http://localhost:3000/confirmacao-conta/erro");
				HttpHeaders headers = new HttpHeaders();
				headers.setLocation(uri);
				return new ResponseEntity<>(headers, HttpStatus.TEMPORARY_REDIRECT);

			}
			Optional<Usuario> consultaUsuario = usuarioRepository.findByEmail(tokenConfirmacao.get().getUsuario().getEmail());
			Usuario usuario = consultaUsuario.get();
			usuario.setHabilitado(true);
			usuarioRepository.save(usuario);
			
			URI uri = URI.create("http://localhost:3000/confirmacao-conta/sucesso");
			HttpHeaders headers = new HttpHeaders();
			headers.setLocation(uri);
			return new ResponseEntity<>(headers, HttpStatus.TEMPORARY_REDIRECT);
		} else {
			URI uri = URI.create("http://localhost:3000/confirmacao-conta/erro");
			HttpHeaders headers = new HttpHeaders();
			headers.setLocation(uri);
			return new ResponseEntity<>(headers, HttpStatus.TEMPORARY_REDIRECT);
		}
	}
	
	@PostMapping("/reenviar-email")
	public ResponseEntity<?> reenviarEmail (@Valid @RequestBody ReenviarEmailRequest reenviarEmailRequest){
		
		Optional<Usuario> consultaUsuario = usuarioRepository.findByEmail(reenviarEmailRequest.getEmail());
		if (consultaUsuario.isPresent()) {
			Optional<TokenConfirmacao> consultaTokenConfirmacao = tokenConfirmacaoRepository.findByUsuario(consultaUsuario.get());
			if(consultaTokenConfirmacao.isPresent()) {
				// setar nova data expiracao
				TokenConfirmacao tokenConfirmacao = consultaTokenConfirmacao.get();
				int EXPIRACAO = 60 * 24;
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.MINUTE, EXPIRACAO);
				tokenConfirmacao.setDataExpiracao(new Date(calendar.getTime().getTime()));
				TokenConfirmacao tokenSalvo = tokenConfirmacaoRepository.save(tokenConfirmacao);
				
				// mandar email de verificação
				SimpleMailMessage mensagemEmail = new SimpleMailMessage();
				mensagemEmail.setTo(reenviarEmailRequest.getEmail());
				mensagemEmail.setSubject("Complete o registro para a aplicação WEB (Site) Votação");
				mensagemEmail.setText("Para confirmar sua conta, por favor click aqui: "
						+ "http://localhost:5000/api/auten/confirmar-conta?token=" + tokenSalvo.getToken());
				
				envioEmailService.enviarEmail(mensagemEmail);
				
				return ResponseEntity.ok().body(new ApiResponse(true, "Reenviado com sucesso"));
				
			} else {
				return ResponseEntity.badRequest().body(new ApiResponse(false, "Erro interno no servidor: token confirmação não encontrado"));
			}
		} else {
			return ResponseEntity.badRequest().body(new ApiResponse(false, "Não existe cadastro com email informado"));
		}
	}
}
