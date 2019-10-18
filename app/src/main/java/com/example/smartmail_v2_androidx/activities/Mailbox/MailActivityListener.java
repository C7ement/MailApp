package com.example.smartmail_v2_androidx.activities.Mailbox;

import com.example.smartmail_v2_androidx.database.entities.Mail;
import com.example.smartmail_v2_androidx.database.entities.Mailbox;

public interface MailActivityListener {

    void onMailClicked(long mailId);
    void onWriteNewMailClicked();
    void onMailListRefresh();

    void nextMail();


    void onFolderNameClicked(String folderName);
    void onSearchNameClicked(Mail mail);
    void onCreateSubFolder(String folderName);
    void onMoveTo(String folderName);
    void onDelete(String folderName);
    void onNewSimpleMail(String folderName);


    void sendMail(Mailbox mailbox, String to, String subject, String content);

}
