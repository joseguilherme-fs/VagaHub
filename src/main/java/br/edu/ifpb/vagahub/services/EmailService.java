package br.edu.ifpb.vagahub.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String remetente;

    private static final SecureRandom random = new SecureRandom();

    private String gerarCodigo() {
        int codigo = 1000 + random.nextInt(9000);
        return String.valueOf(codigo);
    }

    public String enviarEmailTexto(String destinatario, String assunto, String mensagem) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(remetente);
            simpleMailMessage.setTo(destinatario);
            simpleMailMessage.setSubject(assunto);
            simpleMailMessage.setText(mensagem);
            javaMailSender.send(simpleMailMessage);
        } catch (Exception e) {
            return "Erro ao enviar código: " + e.getMessage();
        }
        return "Código enviado com sucesso!";
    }

    public String enviarCodigoVerificacao(String destinatario) {
        String codigo = gerarCodigo();
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(remetente);
            simpleMailMessage.setTo(destinatario);
            simpleMailMessage.setSubject("Código de verificação");
            simpleMailMessage.setText("Olá! Seu código de verificação é: " + codigo);
            javaMailSender.send(simpleMailMessage);

            return codigo;

        } catch (Exception e) {
            return "Erro ao enviar código: " + e.getMessage();
        }
    }
}
