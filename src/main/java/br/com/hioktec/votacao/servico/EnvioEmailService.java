package br.com.hioktec.votacao.servico;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Servicos de envio de email para confirmação do mesmo por meio de link com token de confirmação.
 * @author rodolfo
 */
@Service("envioEmailService")
public class EnvioEmailService {
	
	private JavaMailSender javaMailSender;
	
	@Autowired
	public EnvioEmailService(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}
	
	@Async
	public void enviarEmail(SimpleMailMessage email) {
		javaMailSender.send(email);
	}
}
