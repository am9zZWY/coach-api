package com.jpkmiller.coach_api.mail.imap.providers;

public abstract class MailProviderConfig {
    final ImapConfig config;

    MailProviderConfig(String host, String port, String username, String password) {
        this.config = new ImapConfig(host, port, username, password);
    }

    public String getStoreUrl() {
        return "imaps://" + config.username() + ":" + config.password() + "@" + config.host() + ":" + config.port() + "/INBOX";
    }
}
