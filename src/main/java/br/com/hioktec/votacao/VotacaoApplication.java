package br.com.hioktec.votacao;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

/**
 * Aplicativo criado no spring boot: http://start.spring.io/
 * configuração: Maven, Java 8 e jar
 * dependencias: Web, JPA, Security, mySQL
 * adicionado mais dependencias depois no pom.xml
 * @author rodolfo
 */
@SpringBootApplication
@EntityScan(basePackageClasses = { // Configurando Spring Boot para usar os conversores Java 8 Date/Time e UTC Timezone.
		VotacaoApplication.class,
		Jsr310JpaConverters.class 
})
public class VotacaoApplication {
	
	@PostConstruct // Configurando Spring Boot para usar os conversores Java 8 Date/Time e UTC Timezone para persistir no banco de dados.
	void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	public static void main(String[] args) {
		SpringApplication.run(VotacaoApplication.class, args);
	}
}
