package com.jpkmiller.coach_api.mail.imap;

import com.jpkmiller.coach_api.mail.imap.providers.MailProviderConfig;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mail.ImapMailReceiver;
import org.springframework.integration.mail.MailReceiver;
import org.springframework.integration.mail.MailReceivingMessageSource;
import org.springframework.messaging.Message;

import java.util.Properties;

@Configuration
public class MailReceiverConfiguration {

    private static final Logger log = LoggerFactory.getLogger(MailReceiverConfiguration.class);

    private final ReceiveMailService receiveMailService;
    private final MailProviderConfig mailProviderConfig;

    public MailReceiverConfiguration(ReceiveMailService receiveMailService, MailProviderConfig mailProviderConfig) {
        this.receiveMailService = receiveMailService;
        this.mailProviderConfig = mailProviderConfig;
    }

    @ServiceActivator(inputChannel = "receiveEmailChannel")
    public void receive(Message<?> message) {
        receiveMailService.handleReceivedMail((MimeMessage) message.getPayload());
    }

    @Bean("receiveEmailChannel")
    public DirectChannel defaultChannel() {
        DirectChannel directChannel = new DirectChannel();
        directChannel.setDatatypes(jakarta.mail.internet.MimeMessage.class);
        return directChannel;
    }

    @Bean()
    @InboundChannelAdapter(
            channel = "receiveEmailChannel",
            poller = @Poller(fixedDelay = "5000", taskExecutor = "asyncTaskExecutor")
    )
    public MailReceivingMessageSource mailMessageSource(MailReceiver mailReceiver) {
        return new MailReceivingMessageSource(mailReceiver);
    }

    @Bean
    public MailReceiver imapMailReceiver() {
        var storeUrl = mailProviderConfig.getStoreUrl();
        log.info("IMAP connection url: {}", storeUrl);

        ImapMailReceiver imapMailReceiver = new ImapMailReceiver(storeUrl);
        imapMailReceiver.setShouldMarkMessagesAsRead(true);
        imapMailReceiver.setShouldDeleteMessages(false);
        imapMailReceiver.setMaxFetchSize(10);
        // imapMailReceiver.setAutoCloseFolder(true);

        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        javaMailProperties.put("mail.imap.socketFactory.fallback", false);
        javaMailProperties.put("mail.store.protocol", "imaps");
        javaMailProperties.put("mail.imap.ssl.enable", true);  // Add this
        javaMailProperties.put("mail.debug", true);

        imapMailReceiver.setJavaMailProperties(javaMailProperties);

        return imapMailReceiver;
    }

}
