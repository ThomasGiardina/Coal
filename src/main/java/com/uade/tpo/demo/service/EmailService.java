package com.uade.tpo.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.uade.tpo.demo.entity.GiftCardCode;
import com.uade.tpo.demo.entity.Pedido;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public void enviarGiftCards(String destinatario, List<GiftCardCode> codes, Pedido pedido) {
        try {
            if (mailSender == null) {
                System.out.println("[WARN] JavaMailSender no configurado; se omite envío de correo. Destinatario: " + destinatario);
                return;
            }
            String lista = codes.stream()
                    .map(c -> String.format("%s - USD %.2f", c.getCode(), c.getValor()))
                    .collect(Collectors.joining("\n"));

            String subject = "Tus gift cards de Coal";
            String text = "Hola!\n\n" +
                    "Gracias por tu compra en Coal. Adjuntamos tus códigos de gift card generados para el pedido #" + pedido.getId() + ".\n\n" +
                    lista +
                    "\n\nPuedes canjearlos en tu cuenta cuando quieras.\n\n" +
                    "¡Que los disfrutes!";

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(destinatario);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error enviando mail de gift cards: " + e.getMessage());
        }
    }
}
