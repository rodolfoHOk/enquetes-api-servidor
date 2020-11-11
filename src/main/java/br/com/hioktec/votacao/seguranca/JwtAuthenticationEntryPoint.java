package br.com.hioktec.votacao.seguranca;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/*
 * Esta classe é usada para retornar um erro 401 não autorizado para clientes que tentam acessar um recurso protegido
 *  sem autenticação adequada. Ele implementa a interface AuthenticationEntryPoint do Spring Security e fornece
 *  a implementação para seu método commence (). Este método é chamado sempre que uma exceção é lançada devido
 *  a um usuário não autenticado tentando acessar um recurso que requer autenticação.
 * Nesse caso, simplesmente responderemos com um erro 401 contendo a mensagem de exceção.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
	
	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

	@Override
	public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			AuthenticationException authException) throws IOException, ServletException {
		logger.error("\"Respondendo com erro não autorizado. Mensagem - {}", authException.getMessage());
		httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
	}
}
