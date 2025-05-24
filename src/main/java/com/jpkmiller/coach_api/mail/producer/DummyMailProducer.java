package com.jpkmiller.coach_api.mail.producer;

import com.jpkmiller.coach_api.core.Mail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DummyMailProducer {

    @Autowired
    private KafkaTemplate<String, Mail> kafkaTemplate;

    @Scheduled(fixedRate = 10000)
    public void sendMessage() {
        System.out.print("Sending email...");
        var mail = new Mail("josef.mueller@student.uni-tuebingen.de", "Test", "Hi");
        kafkaTemplate.send("mail", mail)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        System.out.printf("DONE%n");
                    } else {
                        System.err.println("Failed to send message: " + ex.getMessage());
                    }
                });
    }

}
