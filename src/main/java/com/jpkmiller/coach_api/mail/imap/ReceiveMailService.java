package com.jpkmiller.coach_api.mail.imap;

import jakarta.mail.internet.MimeMessage;

public interface ReceiveMailService {

    void handleReceivedMail(MimeMessage message);

}
