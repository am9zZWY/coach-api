package com.jpkmiller.coach_api.mail.imap;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.mail2.jakarta.util.MimeMessageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class ReceiveMailServiceImpl implements ReceiveMailService {

    private static final Logger log = LoggerFactory.getLogger(ReceiveMailServiceImpl.class);

    @Override
    public void handleReceivedMail(MimeMessage receivedMessage) {
        try {

            Folder folder = receivedMessage.getFolder();
            folder.open(Folder.READ_WRITE);

            Message[] messages = folder.getMessages();
            fetchMessagesInFolder(folder, messages);

            Arrays.stream(messages).filter(message -> {
                MimeMessage currentMessage = (MimeMessage) message;
                try {
                    log.info("Received message from " + currentMessage.getFrom()[0] + ": " + currentMessage.getFrom()[1]);
                    return currentMessage.getMessageID().equalsIgnoreCase(receivedMessage.getMessageID());
                } catch (MessagingException e) {
                    log.error("Error occurred during process message", e);
                    return false;
                }
            }).forEach(this::extractMail);

            folder.close(true);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void fetchMessagesInFolder(Folder folder, Message[] messages) throws MessagingException {
        FetchProfile contentsProfile = new FetchProfile();
        contentsProfile.add(FetchProfile.Item.ENVELOPE);
        contentsProfile.add(FetchProfile.Item.CONTENT_INFO);
        contentsProfile.add(FetchProfile.Item.FLAGS);
        contentsProfile.add(FetchProfile.Item.SIZE);
        folder.fetch(messages, contentsProfile);
    }

    private void extractMail(Message message) {
        try {
            final MimeMessage messageToExtract = (MimeMessage) message;
            final MimeMessageParser mimeMessageParser = new MimeMessageParser(messageToExtract).parse();
            showMailContent(mimeMessageParser);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void showMailContent(MimeMessageParser mimeMessageParser) throws Exception {
        log.debug("From: {} to: {} | Subject: {}", mimeMessageParser.getFrom(), mimeMessageParser.getTo(), mimeMessageParser.getSubject());
        log.debug("Mail content: {}", mimeMessageParser.getPlainContent());
    }
}
