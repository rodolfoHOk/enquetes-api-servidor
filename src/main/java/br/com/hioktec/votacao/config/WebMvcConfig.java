package br.com.hioktec.votacao.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Como estaremos acessando as APIs do cliente react que será executado em seu próprio servidor de desenvolvimento.
 * Para permitir solicitações de origem cruzada do cliente react, criamos esta classe.
 * @author rodolfo
 *
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	
	private final long MAX_IDADE_SEG = 3600;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedMethods("HEAD", "OPTIONS", "GET", "POST", "PUT", "DELETE", "PATCH")
			.allowedOrigins("http://localhost:3000")
			.maxAge(MAX_IDADE_SEG);
	}

}
