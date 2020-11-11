package br.com.hioktec.votacao.seguranca;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

/*
 * Essa classe utilitária será usada para gerar um JWT depois que um usuário fizer login com sucesso,
 * também irá validar o JWT enviado no cabeçalho das solicitações de autorizações.
 * 
 */
@Component
public class JwtTokenProvider {
	
	private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
	
	@Value("${app.jwtSecret}")
	private String jwtSegredo;
	
	@Value("${app.jwtExpirationInMs}")
	private int jwtExpiraEmMs;
	
	public String gerarToken(Authentication authentication) {
		UsuarioPrincipal usuarioPrincipal = (UsuarioPrincipal) authentication.getPrincipal();
		
		Date agora = new Date();
		Date dataExpira = new Date(agora.getTime() + jwtExpiraEmMs);
		
		return Jwts.builder()
				.setSubject(Long.toString(usuarioPrincipal.getId()))
				.setIssuedAt(new Date())
				.setExpiration(dataExpira)
				.signWith(SignatureAlgorithm.HS512, jwtSegredo)
				.compact();
	}
	
	public Long getUsuarioIdfromJWT(String token) {
		Claims claims = Jwts.parser()
				.setSigningKey(jwtSegredo)
				.parseClaimsJws(token)
				.getBody();
		return Long.parseLong(claims.getSubject());
	}
	
	public boolean validarToken(String autenToken) {
		try {
			Jwts.parser().setSigningKey(jwtSegredo).parseClaimsJws(autenToken);
			return true;
		} catch (SignatureException ex) {
			logger.error("Assinatura JWT inválida");
		} catch (MalformedJwtException ex) {
			logger.error("Token JWT inválido");
		} catch (ExpiredJwtException ex) {
			logger.error("Token JWT expirado");
		} catch (UnsupportedJwtException ex) {
			logger.error("Token JWT não suportado");
		} catch (IllegalArgumentException ex) {
			logger.error("A String de declarações do JWT está vazia");
		}
		return false;
	}
	
}
