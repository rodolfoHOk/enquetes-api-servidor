package br.com.hioktec.votacao.seguranca;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

/*
 * O Spring Security fornece uma anotação chamada @AuthenticationPrincipal para acessar o usuário atualmente
 *  autenticado nos controladores.
 * Nós criamos uma meta-anotação para não ficarmos muito presos às anotações relacionadas ao Spring Security
 *  em todo o nosso projeto. Isso reduz a dependência do Spring Security.
 * Então, se decidirmos remover Spring Security de nosso projeto, podemos facilmente fazer isso simplesmente
 *  alterando a anotação UsuarioAtual.
 * @author rodolfo
 *
 */
@Target({ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal
public @interface UsuarioAtual {

}
