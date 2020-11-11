package br.com.hioktec.votacao.seguranca;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/*
 * Usaremos JWTAuthenticationFilter para implementar um filtro que:
 * 	lê o token de autenticação JWT do cabeçalho de autorização de todas as solicitações
 * 	valida o token
 *  carrega os detalhes do usuário associados a esse token.
 *  Define os detalhes do usuário no SecurityContext do Spring Security. Spring Security usa os detalhes
 *   do usuário para realizar verificações de autorização. Também podemos acessar os detalhes do usuário armazenados
 *   no SecurityContext em nossos controladores para realizar nossa lógica de negócios.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter{

	@Autowired
	private JwtTokenProvider tokenProvider;
	
	@Autowired
	private CustomUserDetailsService customUserDetailsService;
	
	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
	
	/**
	 * No filtro, primeiro analisamos o JWT recuperado do cabeçalho de autorização da solicitação.
	 * Então obtemos o ID do usuário.
	 * Depois disso, estamos carregando os detalhes do usuário no banco de dados através do customUserDetailsService.
	 * E configuramos a autenticação dentro do contexto de segurança do Spring.
	 * 
	 * Observe que a ocorrência do banco de dados no filtro é opcional.
	 * Você também pode codificar o nome de usuário e as funções do usuário nas declarações JWT
	 *  e criar o objeto UserDetails analisando essas declarações do JWT.
	 * Isso evitaria o acerto no banco de dados.
	 * 
	 * No entanto, carregar os detalhes atuais do usuário do banco de dados ainda pode ser útil.
	 * Por exemplo, você pode querer proibir o login com este JWT se a função do usuário tiver mudado
	 *  ou se o usuário tiver atualizado sua senha após a criação deste JWT.
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			String jwt = getJwtFromRequest(request);
			if(StringUtils.hasText(jwt) && tokenProvider.validarToken(jwt)) {
				Long usuarioId = tokenProvider.getUsuarioIdfromJWT(jwt);
				
				UserDetails userDetails = customUserDetailsService.loadUserById(usuarioId);
				
				UsernamePasswordAuthenticationToken autenticacao = 
						new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				autenticacao.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(autenticacao);
			}
		} catch (Exception ex){
			logger.error("Não foi possível definir a autenticação do usuário no contexto de segurança", ex);
		}
		filterChain.doFilter(request, response);
	}

	/**
	 * Recupera o token jwt do cabeçalho de autorização da solicitação.
	 * @param request (solicitação)
	 * @return token jwt
	 */
	private String getJwtFromRequest(HttpServletRequest request) {
		String portadorToken = request.getHeader("autorizacao");
		if(StringUtils.hasText(portadorToken) && portadorToken.startsWith("Portador ")) {
			return portadorToken.substring(9, portadorToken.length());
		}
		return null;
	}
	
}
