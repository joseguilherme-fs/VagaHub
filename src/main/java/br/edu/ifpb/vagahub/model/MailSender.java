package br.edu.ifpb.vagahub.model;

import br.edu.ifpb.vagahub.services.EmailService;

public class MailSender implements Runnable{
    private String destinario;
    private String assunto;
    private String mensagem;
    private EmailService emailService;

    public MailSender(String destinatario, String assunto, String mensagem, EmailService emailService){
        this.destinario = destinatario;
        this.assunto = assunto;
        this.mensagem = mensagem;
        this.emailService = emailService;
    }

    @Override
    public void run() {
        System.out.println("VagaHub enviando e-mail de lembrete para "+destinario);
        emailService.enviarEmailTexto(destinario, assunto, mensagem);
    }
}
