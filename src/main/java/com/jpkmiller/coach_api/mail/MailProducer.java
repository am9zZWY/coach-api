package com.jpkmiller.coach_api.mail;

import com.jpkmiller.coach_api.core.Mail;
import com.jpkmiller.coach_api.mail.imap.MailReceiverConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class MailProducer {

    @Autowired
    private KafkaTemplate<String, Mail> kafkaTemplate;

    @Autowired
    private MailReceiverConfiguration mailReceiverConfiguration;

    // @Scheduled(fixedRate = 100)
    public void sendMessage() {
        System.out.println("Sending email...");
        var mail = new Mail("josef.mueller@student.uni-tuebingen.de", "Test", "Hi");
        kafkaTemplate.send("mail", mail)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        System.out.println("Message sent successfully");
                    } else {
                        System.err.println("Failed to send message: " + ex.getMessage());
                    }
                });
    }

}
