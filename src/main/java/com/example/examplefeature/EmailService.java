package com.example.examplefeature;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço responsável por simular o envio de emails.
 */
@Service
public class EmailService {

    @Transactional
    public void sendEmail(String to, String subject, String body) {
        System.out.println("--------------------------------------------------");
        System.out.println("📧 SIMULAÇÃO DE ENVIO DE EMAIL");
        System.out.println("Para: " + to);
        System.out.println("Assunto: " + subject);
        System.out.println("Mensagem:");
        System.out.println(body);
        System.out.println("------------------------------------------------");
    }
}
