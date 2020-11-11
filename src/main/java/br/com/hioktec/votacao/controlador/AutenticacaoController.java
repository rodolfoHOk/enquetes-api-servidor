package br.com.hioktec.votacao.controlador;

import java.net.URI;
import java.util.Collections;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.hioktec.votacao.excecao.AppException;
import br.com.hioktec.votacao.modelo.NomeFuncao;
import br.com.hioktec.votacao.modelo.Funcao;
import br.com.hioktec.votacao.modelo.Usuario;
import br.com.hioktec.votacao.repositorio.FuncaoRepository;
import br.com.hioktec.votacao.repositorio.UsuarioRepository;
import br.com.hioktec.votacao.requisicao.CadastroRequest;
import br.com.hioktec.votacao.requisicao.LoginRequest;
import br.com.hioktec.votacao.resposta.ApiResponse;
import br.com.hioktec.votacao.resposta.AutenticacaoJWTResponse;
import br.com.hioktec.votacao.seguranca.JwtTokenProvider;

/**
 * AutenticacaoController que contém APIs para login e registro de usuários.
 * @author rodolfo
 *
 */
@RestController
@RequestMapping("/api/auten")
public class AutenticacaoController {
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	JwtTokenProvider tokenProvider;
	
	@Autowired
	UsuarioRepository usuarioRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	FuncaoRepository funcaoRepository;
	
	@PostMapping("/acessar")
	public ResponseEntity<?> autenticarUsuario(@Valid @RequestBody LoginRequest loginRequest){
		
		Authentication autenticacao = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						loginRequest.getNomeUsuarioOuEmail(),
						loginRequest.getSenha()));
		
		SecurityContextHolder.getContext().setAuthentication(autenticacao);
		
		String jwt = tokenProvider.gerarToken(autenticacao);
		
		return ResponseEntity.ok(new AutenticacaoJWTResponse(jwt));
	}
	
	@PostMapping("/inscrever")
	public ResponseEntity<?> registrarUsuario(@Valid @RequestBody CadastroRequest cadastroRequest){
		
			if(usuarioRepository.existsByNomeUsuario(cadastroRequest.getNomeUsuario())) {
				return new ResponseEntity<ApiResponse>(
						new ApiResponse(false, "Nome de usuário já existe"), HttpStatus.BAD_REQUEST);
			}
			
			if(usuarioRepository.existsByEmail(cadastroRequest.getEmail())) {
				return new ResponseEntity<ApiResponse>(
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
			
			Usuario resultadoCadastro = usuarioRepository.save(usuario);
			
			URI localizacao= ServletUriComponentsBuilder
					.fromCurrentContextPath().path("/api/usuarios/{nomeUsuario}")
					.buildAndExpand(resultadoCadastro.getNomeUsuario()).toUri();
			
			return ResponseEntity.created(localizacao).body(new ApiResponse(true, "Usuário registrado com sucesso"));
	}
	
}
