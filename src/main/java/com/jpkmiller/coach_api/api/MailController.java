package com.jpkmiller.coach_api.api;

import com.jpkmiller.coach_api.core.Mail;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MailController {

    MailConsumer mailConsumer;

    public MailController(MailConsumer mailConsumer) {
        this.mailConsumer = mailConsumer;
    }

    @GetMapping("/mail")
    List<Mail> mail() {
        return mailConsumer.mails;
    }

}
