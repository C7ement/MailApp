package com.example.smartmail_v2_androidx;


import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.example.smartmail_v2_androidx.FolderAndListhierarchy.CustomFolder;
import com.example.smartmail_v2_androidx.database.DAOs.MailDao;
import com.example.smartmail_v2_androidx.database.DAOs.MailboxDao;
import com.example.smartmail_v2_androidx.database.MailRoomDatabase;
import com.example.smartmail_v2_androidx.database.entities.Mail;
import com.example.smartmail_v2_androidx.database.entities.Mailbox;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static java.lang.Math.min;

public class MailRepository {

    private final MailDao mailDao;
    private final MailboxDao mailboxDao;
    private int numberOfMessages = 20;
    private Application application;
    private ExecutorService executor;
    private Future<?> future;

    public MailRepository(Application application) {
        MailRoomDatabase db =  MailRoomDatabase.getDatabase(application);
        mailboxDao = db.mailboxDao();
        mailDao = db.mailDao();
        executor = Executors.newSingleThreadExecutor();
        this.application = application; //idk what i am doing
    }

    public LiveData<List<Mailbox>> getAllMailboxes() {
        return mailboxDao.getAllMailboxes();
    }

    public LiveData<List<Mail>> getAllMails(String mailboxLabel, String folderName) {
        return mailDao.getAllMails(mailboxLabel, folderName);
    }
    public LiveData<List<Mail>> getMailSearchResult(String mailboxLabel, String folderName, String search) {
        return mailDao.getMailSearchResult(mailboxLabel, folderName, "%"+search+"%");
    }

    public LiveData<String> getFolders(String mailboxLabel) {
        return mailboxDao.getFolders(mailboxLabel);
    }
    public LiveData<Character> getFolderSeparator(String mailboxLabel) {
        return mailboxDao.getFolderSeparator(mailboxLabel);
    }

    public LiveData<Mail> getMail(long mailId) {
        return mailDao.getMail(mailId);
    }


    public void sendEmail(Mailbox mailbox, String to, String subject, String content) {
        executor.execute(() -> {


            Session session = mailbox.getSession();

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(mailbox.getUsername()));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
                message.setSubject(subject);
                message.setContent(content, "text/html");
                Transport.send(message);

                displayToast("Message Sent Successfully !");

            } catch (MessagingException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }

    public void saveMailToFolder(String mailboxLabel, String folderName, String to, String subject, String content) {
        executor.execute(() -> {

            Mailbox mailbox = mailboxDao.getMailboxAsync(mailboxLabel);
            Session session = mailbox.getSession();

            Store store = null;
            try {
                store = session.getStore();
                IMAPStore imapStore = (IMAPStore) store;
                imapStore.connect();
                Folder folder = imapStore.getFolder(folderName);
                folder.open(Folder.READ_WRITE);

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(mailbox.getUsername()));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
                message.setSubject(subject);
                message.setContent(content, "text/html");

                MimeMessage messages[] = {(MimeMessage) message};
                folder.appendMessages(messages);

                displayToast("Message Saved Successfully !");

            } catch (MessagingException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }


    public void markAsRead(Mail mail) {
        executor.execute(() -> {

            int msgNumber = mail.getMsgNumber();
            Mailbox mailbox = mailboxDao.getMailboxAsync(mail.getMailboxLabel());

            PRINT("fetching messages");

            Session session = mailbox.getSession();

            Store store = null;
            try {
                store = session.getStore();
                IMAPStore imapStore = (IMAPStore) store;
                imapStore.connect();
                Folder folder = imapStore.getFolder(mail.getFolder());
                folder.open(Folder.READ_WRITE);
                folder.getMessage(msgNumber).setFlag(Flags.Flag.SEEN, true);
                PRINT("marked as seen");
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });
    }


    public void insert(Mailbox mailbox) {
        executor.execute(() -> {
            this.mailboxDao.insert(mailbox);
        });
    }

    public LiveData<Mailbox> getMailbox(String mailboxLabel) {
        return mailboxDao.getMailbox(mailboxLabel);
    }

    public LiveData<Boolean> reloadMailsFromFolder(String mailboxLabel, String folderName) {
        return loadMailsFromFolder(mailboxLabel, folderName, true);
    }

    public LiveData<Boolean> loadMailsFromFolder(String mailboxLabel, String folderName) {
        return loadMailsFromFolder(mailboxLabel, folderName, false);
    }



    public LiveData<Boolean> loadMailsFromFolder(String mailboxLabel, String folderName, boolean reload) {
        if (future!=null) future.cancel(true);
        MutableLiveData<Boolean> isReloading = new MutableLiveData<Boolean>();
        isReloading.postValue(reload);
        future = executor.submit( new Callable<Void>() {
            @Override
            public Void call() {

                Mailbox mailbox = mailboxDao.getMailboxAsync(mailboxLabel);

                PRINT("fetching messages");

                Session session = mailbox.getSession();

                try {
                    IMAPStore imapStore = (IMAPStore) session.getStore();
                    imapStore.connect();

                    IMAPFolder folder = (IMAPFolder) imapStore.getFolder(folderName);
                    folder.open(Folder.READ_ONLY);
                    Message[] messages = folder.getMessages();
                    for (int i = 1; i <= min(messages.length, numberOfMessages); i++) {
                        Message msg = messages[messages.length - i];
                        Mail mail = getMailFromMessage(msg, mailboxLabel, folder.getUID(msg));
                        mailDao.insert(mail);
                    }
                    folder.close(false); // parameter: expunge - expunges all deleted messages if this flag is true
                    imapStore.close();

                } catch (NoSuchProviderException e) {
                    e.printStackTrace();
                } catch (MessagingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                isReloading.postValue(false);
                return null;
            }
        });
        return isReloading;
    }


    private Mail getMailFromMessage(Message msg, String mailboxLabel, long UID) throws MessagingException, IOException, JSONException {
        Mail mail = new Mail(mailboxLabel);

        mail.setMsgNumber(msg.getMessageNumber());
        mail.setSubject(msg.getSubject());
        mail.setReceivedDate(msg.getReceivedDate());
        mail.setSentDate(msg.getSentDate());
        if (msg.getFrom() != null) {
            mail.setSender((InternetAddress) msg.getFrom()[0]);
        }
        mail.setFolder(msg.getFolder().getFullName());

        if (msg.isSet(Flags.Flag.DELETED)) {
            mail.setDELETED(true);
        }
        if (msg.isSet(Flags.Flag.ANSWERED)) {
            mail.setANSWERED(true);
        }
        if (msg.isSet(Flags.Flag.DRAFT)) {
            mail.setDRAFT(true);
        }
        if (msg.isSet(Flags.Flag.FLAGGED)) {
            mail.setFLAGGED(true);
        }
        if (msg.isSet(Flags.Flag.RECENT)) {
            mail.setRECENT(true);
        }
        if (msg.isSet(Flags.Flag.SEEN)) {
            mail.setSEEN(true);
        }
        if (msg.isSet(Flags.Flag.USER)) {
            mail.setUSER(true);
            mail.setUserFlags(msg.getFlags().getUserFlags());
        }

        Address[] recipients = msg.getRecipients(Message.RecipientType.TO);
        mail.setRecipients(new ArrayList<>());
        if (recipients != null) {
            for (Address addr : recipients) {
                mail.addRecipient((InternetAddress) addr);
            }
        }
        mail.setContent(getText(msg));
        mail.setUID(UID);
        JSONArray files = new JSONArray();
        getAttachements(msg, files);
        mail.setFiles(files);
        PRINT(String.valueOf(files));
        return mail;
    }

    private String getText(Part p) throws MessagingException, IOException {


        if (p.isMimeType("TEXT/X-VCARD")) {
            InputStream inputStream = (InputStream) p.getContent();
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder total = new StringBuilder();
            for (String line; (line = r.readLine()) != null; ) {
                total.append(line).append('\n');
            }
            return String.valueOf(total);
        }

        if (p.isMimeType("text/*")) {
            String s = p.getContent().toString();
            //textIsHtml = p.isMimeType("text/html");
            return s;
        }
        if (p.isMimeType("multipart/alternative")) {
            Multipart mp = (Multipart)p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null) {
                        text = getText(bp);
                    }
                } else if (bp.isMimeType("text/html")) {
                    String s = getText(bp);
                    if (s != null) {
                        return s;
                    }
                } else {
                    return getText(bp);
                }
            }
            return text;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart)p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String s = getText(mp.getBodyPart(i));
                if (s != null) {
                    return s;
                }
            }
        }
        PRINT("No Content - Returning Empty String");
        return "";
    }

    //todo: prevent file overwritting but not writte two tmes when emails already loaded
    private void getAttachements(Part p, JSONArray files) throws MessagingException, IOException {
        if (/*Part.ATTACHMENT.equalsIgnoreCase(p.getDisposition()) ||*/ p.isMimeType("IMAGE/*")) {
            files.put(saveFile(p));
        }

        if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart)p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                getAttachements(mp.getBodyPart(i), files);
            }
        }
    }

    private JSONObject saveFile(Part p) {

        JSONObject fileData = new JSONObject();
        try {
            Context context = this.application.getApplicationContext();
            String filename = Uri.parse(p.getFileName()).getLastPathSegment();
            fileData.put("filename",filename);
            fileData.put("content_type",p.getContentType());
            if (p.getHeader("Content-ID") != null) {
                fileData.put("cid",Arrays.toString(p.getHeader("Content-ID")));
            }
            PRINT("saving "+filename);

            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            BufferedInputStream bis = new BufferedInputStream(p.getInputStream());
            int aByte;
            while ((aByte = bis.read()) != -1) {
                bos.write(aByte);
            }
            bos.flush();
            bos.close();
            bis.close();
            fos.close();

            return fileData;
        } catch (IOException | MessagingException | JSONException e) {
            PRINT("Error while creating file");

        }
        return fileData;
    }

    public void loadFolders(String mailboxLabel) {
        executor.execute(() -> {

            Mailbox mailbox = mailboxDao.getMailboxAsync(mailboxLabel);

            PRINT("Loading NavigationMenu...");

            Session session = mailbox.getSession();

            try {
                Store store = session.getStore();
                IMAPStore imapStore = (IMAPStore) store;
                imapStore.connect();

                //Folder[] folders = imapStore.getDefaultFolder().list();
                Folder[] folders = imapStore.getDefaultFolder().list("*");

                char separator = imapStore.getDefaultFolder().getSeparator();
                mailbox.setFolderSeparator((separator));

                String strfolders = CustomFolder.getRootFromArray(folders).toString();
                mailbox.setFolders(strfolders);
                mailboxDao.insert(mailbox);

            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });
    }


    public void createFolder(String folderName, String mailboxLabel) {

        if (future!=null) future.cancel(true);
        executor.execute(() -> {

            Mailbox mailbox = mailboxDao.getMailboxAsync(mailboxLabel);
            PRINT("Creating Folder...");
            Session session = mailbox.getSession();

            try {
                Store store = session.getStore();
                IMAPStore imapStore = (IMAPStore) store;
                imapStore.connect();
                Folder defaultFolder = imapStore.getDefaultFolder();

                Folder newFolder = defaultFolder.getFolder(folderName);
                if (newFolder.exists()) {
                    displayToast("Folder already exist !");
                } else {
                    newFolder.create(Folder.HOLDS_MESSAGES);
                }

            } catch (MessagingException e) {
                PRINT("Error creating folder: " + e.getMessage());
                e.printStackTrace();
            }
        });
        loadFolders(mailboxLabel);
    }


    public void deleteFolder(String mailboxLabel, String folderName) {

        if (future!=null) future.cancel(true);
        executor.execute(() -> {

            PRINT("Deleting folder: "+folderName);
            Mailbox mailbox = mailboxDao.getMailboxAsync(mailboxLabel);
            Session session = mailbox.getSession();

            try {
                Store store = session.getStore();
                IMAPStore imapStore = (IMAPStore) store;
                imapStore.connect();
                Folder defaultFolder = imapStore.getDefaultFolder();

                Folder folder = defaultFolder.getFolder(folderName);
                if (folder.exists()) {
                    folder.delete(true); //true -> subfolders are also deleted
                }

            } catch (MessagingException e) {
                PRINT("Error deleting folder: " + e.getMessage());
                e.printStackTrace();
            }
        });
        loadFolders(mailboxLabel);
    }

    private void displayToast(CharSequence text) {
        new Handler(Looper.getMainLooper()).post(() -> {
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(application.getApplicationContext(), text, duration);
            toast.show();
            PRINT("Toast : " + text);
        });
    }
    private void PRINT(String text) {
        System.out.println("MAIL REPOSITORY : " + text);
    }
}