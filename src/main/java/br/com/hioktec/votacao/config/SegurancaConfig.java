package br.com.hioktec.votacao.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.hioktec.votacao.seguranca.CustomUserDetailsService;
import br.com.hioktec.votacao.seguranca.JwtAuthenticationEntryPoint;
import br.com.hioktec.votacao.seguranca.JwtAuthenticationFilter;

/**
 * Configurando o Spring Security e o JWT.
 * Esta classe é o ponto crucial de nossa implementação de segurança.
 * Ele contém quase todas as configurações de segurança necessárias para o nosso projeto.
 * A anotação EnableWebSecurity é um marcador informando ao spring que será uma classe de configuração,
 * 	para ativar o suporte de segurança do Spring Security e fornecer a integração do Spring MVC.
 * A anotação EnableGlobalMethodSecurity fornece segurança AOP(Programação Orientada a Aspecto) nos métodos:
 * 	securedEnabled: Ele habilita a anotação Secured com a qual você pode proteger seus métodos de controlador e serviço.
 * 	(Secured: usada para segurança de nível de método apenas fornecendo o nome da função(role)).
 *  jsr250Enabled: Habilita a anotação RolesAllowed que funciona da mesma forma que a anotação Secured.
 * 	 A única diferença é que Secured é baseado na segurança do Spring e RolesAllowed é uma anotação padrão Java
 *   baseada na especificação JSR 250.
 *  prePostEnabled: habilita a sintaxe de controle de acesso baseada em expressões mais complexas com as anotações
 *   PreAuthorize e PostAuthorize.
 *  (PreAuthorize: verifica a autorização antes da execução do método, usa SpEL para verificar a autorização com base
 *   nas funções do usuário registradas e no conteúdo dos parâmetros do método).
 *  (PostAuthorize: verifica a autorização após a execução do método, usa SpEL para verificar a autorização especialmente
 *   em usuários registrados e retornar o conteúdo do objeto).
 * WebSecurityConfigurerAdapter esta classe implementa a interface WebSecurityConfigurer do Spring Security.
 * 	Ele fornece configurações de segurança padrão e permite que outras classes estendam e personalizem
 *  as configurações de segurança substituindo seus métodos.
 * 
 * @author rodolfo
 *
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class SegurancaConfig extends WebSecurityConfigurerAdapter{
	
	/* 
	 * customUserDetailsService será utilizado para realizar a validação de usuário e senha retornando um UserDetails
	 * que será chamado no método configure com assinatura [AuthenticationManagerBuilder].
	 */
	@Autowired
	private CustomUserDetailsService customUserDetailsService;
	
	/* 
	 * unauthorizedHandler, por padrão quando há o erro Não autorizado [401] o spring retorna para uma página inteira,
	 * sendo renderizada pelo navegador, para cenários de API Rest não é uma boa prática, 
	 * portanto esse é criado para configurar uma nova resposta para o erro.
	 */
	@Autowired
	private JwtAuthenticationEntryPoint unauthorizedHandler;
	
	/*
	 * o bean jwtAuthenticationFilter é criado para filtrar a requisição e validar o token.
	 */
	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter();
	}
	
	/*
	 * este bean serve para codificar a senha.
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	/*
	 * AuthenticationManagerBuilder é usado para criar uma instância AuthenticationManager que é a interface principal
	 *  do Spring Security para autenticar um usuário.
	 * Você pode usar AuthenticationManagerBuilder para construir autenticação na memória, autenticação LDAP,
	 *  autenticação JDBC ou adicionar seu provedor de autenticação personalizado.
	 * Em nosso exemplo, fornecemos nosso customUserDetailsService e um passwordEncoder para construir
	 *  o AuthenticationManager.
	 * Usaremos o AuthenticationManager configurado para autenticar um usuário na API de login.
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(customUserDetailsService)
			.passwordEncoder(passwordEncoder());
	}
	
	/*
	 * authenticationManagerBean é um contêiner para provedores de autenticação.
	 */
	@Bean(BeanIds.AUTHENTICATION_MANAGER)
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	/*
	 * As configurações de HttpSecurity são usadas para configurar funcionalidades de segurança
	 * 	e adicionar regras para proteger recursos com base em várias condições.
	 * Em nosso exemplo, estamos permitindo o acesso a recursos estáticos e algumas outras APIs públicas
	 *  para todos e restringindo o acesso a outras APIs apenas para usuários autenticados.
	 * Também adicionamos o JWTAuthenticationEntryPoint e o JWTAuthenticationFilter personalizado na configuração
	 *  HttpSecurity.
	 * Configuração do configure com a assinatura [HttpSecurity],
	 * cors(): Adicionar um corsFilter para ser usado, se um bean com o nome corsFilter for fornecido, esse será utilizado.
	 * csrf(): Referente ao ataque Cross-Site Request Forgery, o mesmo serve para configurar uma outra tratativa
	 * 	de segurança, no nosso caso vamos desativar, pois usaremos o JWT.
	 * exceptionHandling(): para configurar o retorno do erro 401, utilizando a classe JwtAuthenticationEntryPoint,
	 *  já explicado acima.
	 * sessionManagement(): utilizado para controlar as sessões HTTP, no nosso caso estamos utilizando STATELESS.
	 * authorizeRequests(): responsável por definir quais uris deverão ser autenticadas, no nosso exemplo a uri: 
	 * 	/api/auth/** não será autenticada pois se trata do endpoint para realizar o login. assim como as imagens, js, 
	 * 	html e css.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and()
			.csrf().disable()
			.exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
			.authorizeRequests()
				.antMatchers("/", 
					"/favicon.ico",
                    "/**/*.png",
                    "/**/*.gif",
                    "/**/*.svg",
                    "/**/*.jpg",
                    "/**/*.html",
                    "/**/*.css",
                    "/**/*.js").permitAll()
				.antMatchers("/api/auten/**").permitAll()
				.antMatchers("/api/usuario/checarNomeUsuarioDisponivel", "/api/usuario/checarEmailDisponivel").permitAll()
				.antMatchers(HttpMethod.GET, "/api/enquetes/**", "/api/usuarios/**").permitAll()
				.anyRequest().authenticated();
		
		// Adicionando uma customização do JWT security filter
		http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
	}
	
}
