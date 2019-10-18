package com.example.smartmail_v2_androidx.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

@Entity(tableName = "mailbox_table")
public class Mailbox {

    @PrimaryKey
    @NonNull
    private String label;

    @NonNull
    private String host;

    @NonNull
    private String username;

    @NonNull
    private String password;

    @NonNull
    private String port;

    @NonNull
    private char folderSeparator;

    private String folders = "";



    public Mailbox(String label, @NonNull String host, @NonNull String username, @NonNull String password) {
        this.label = label;
        this.host = host;
        this.username = username;
        this.password = password;
        this.port = "993";
    }

    @NonNull
    public String getLabel(){ return this.label; }
    @NonNull
    public void setLabel(String label){ this.label = label; }

    @NonNull
    public String getHost() {
        return host;
    }
    public void setHost(@NonNull String host) {
        this.host = host;
    }

    @NonNull
    public String getUsername() {
        return username;
    }
    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    @NonNull
    public String getPassword() {
        return password;
    }
    public void setPassword(@NonNull String password) {
        this.password = password;
    }

    public String getPort() {
        return this.port;
    }
    public void setPort(@NonNull String port) {
        this.port = port;
    }

    @NonNull
    public String getFolders() {
        return folders;
    }
    public void setFolders(@NonNull String folders) {
        this.folders = folders;
    }

    public Session getSession() {

        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imaps.port", port);
        properties.put("mail.imaps.host", host);
        properties.put("mail.imaps.starttls.enable", "true");

        Authenticator authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };

        return Session.getInstance(properties, authenticator);
    }

    public char getFolderSeparator() {
        return folderSeparator;
    }

    public void setFolderSeparator(char folderSeparator) {
        this.folderSeparator = folderSeparator;
    }
}