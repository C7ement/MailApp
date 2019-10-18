package com.example.smartmail_v2_androidx.database.entities;



import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.smartmail_v2_androidx.FolderAndListhierarchy.Leaf;
import com.example.smartmail_v2_androidx.database.converters.AddressConverter;
import com.example.smartmail_v2_androidx.database.converters.AddressListConverter;
import com.example.smartmail_v2_androidx.database.converters.DateConverter;
import com.example.smartmail_v2_androidx.database.converters.JsonConverter;
import com.example.smartmail_v2_androidx.database.converters.StringArrayConverter;
import com.example.smartmail_v2_androidx.database.converters.StringListConverter;

import org.json.JSONArray;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.Flags;
import javax.mail.internet.InternetAddress;

@Entity(tableName = "mail_table",
        indices = {@Index(value = {"mailboxLabel", "folder", "UID"}, unique = true)},
        foreignKeys = @ForeignKey(entity = Mailbox.class,
                parentColumns = "label",
                childColumns = "mailboxLabel"//,
                //onDelete = CASCADE
        ))
@TypeConverters({DateConverter.class, JsonConverter.class, AddressConverter.class, AddressListConverter.class, StringArrayConverter.class})
public class Mail extends Leaf {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long id;


    @NonNull
    private String mailboxLabel;
    private String subject;
    private String content;

    private Date receivedDate;
    private Date sentDate;
    private boolean textIsHtml;
    private int msgNumber;
    @NonNull
    private String folder;
    @NonNull
    private List<InternetAddress> recipients;
    private InternetAddress sender;
    private JSONArray files;
    @NonNull
    private Long UID;

    //Flags
    private boolean DELETED = false;
    private boolean ANSWERED = false;
    private boolean DRAFT = false;
    private boolean FLAGGED = false;
    private boolean RECENT = false;
    private boolean SEEN = false;
    private boolean USER = false;
    private String[] userFlags;

    public Mail(String mailboxLabel) {
        this.id = 0;
        this.mailboxLabel = mailboxLabel;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getMailboxLabel() { return mailboxLabel; }
    public void setMailboxLabel(String mailboxLabel) { this.mailboxLabel = mailboxLabel; }
    public InternetAddress getSender() { return sender == null ? new InternetAddress() : sender; }
    public void setSender(InternetAddress sender) { this.sender = sender; }
    public List<InternetAddress> getRecipients() { return recipients; }
    public void setRecipients(List<InternetAddress> recipients) { this.recipients = recipients; }
    public void addRecipient(InternetAddress recipient) { this.recipients.add(recipient); }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public boolean isTextIsHtml() { return textIsHtml; }
    public void setTextIsHtml(boolean textIsHtml) { this.textIsHtml = textIsHtml; }


    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public JSONArray getFiles() {
        return files;
    }

    public void setFiles(JSONArray files) {
        this.files = files;
    }


    public boolean isDELETED() {
        return DELETED;
    }

    public void setDELETED(boolean DELETED) {
        this.DELETED = DELETED;
    }

    public boolean isANSWERED() {
        return ANSWERED;
    }

    public void setANSWERED(boolean ANSWERED) {
        this.ANSWERED = ANSWERED;
    }

    public boolean isDRAFT() {
        return DRAFT;
    }

    public void setDRAFT(boolean DRAFT) {
        this.DRAFT = DRAFT;
    }

    public boolean isFLAGGED() {
        return FLAGGED;
    }

    public void setFLAGGED(boolean FLAGGED) {
        this.FLAGGED = FLAGGED;
    }

    public boolean isRECENT() {
        return RECENT;
    }

    public void setRECENT(boolean RECENT) {
        this.RECENT = RECENT;
    }

    public boolean isSEEN() {
        return SEEN;
    }

    public void setSEEN(boolean SEEN) {
        this.SEEN = SEEN;
    }

    public boolean isUSER() {
        return USER;
    }

    public void setUSER(boolean USER) {
        this.USER = USER;
    }

    public String[] getUserFlags() {
        return userFlags;
    }

    public void setUserFlags(String[] userFlags) {
        this.userFlags = userFlags;
    }

    public int getMsgNumber() {
        return msgNumber;
    }

    public void setMsgNumber(int messageId) {
        this.msgNumber = messageId;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public ArrayList<String> getFlags() {
        ArrayList<String> flags = new ArrayList<>();
        if (DELETED) {
            flags.add("DELETED");
        }
        if (ANSWERED) {
            flags.add("ANSWERED");
        }
        if (DRAFT) {
            flags.add("DRAFT");
        }
        if (FLAGGED) {
            flags.add("FLAGGED");
        }
        if (RECENT) {
            flags.add("RECENT");
        }
        if (SEEN) {
            flags.add("SEEN");
        }
        if (USER) {
            flags.add("USER");
        }
        return flags;
    }

    public Long getUID() {
        return UID;
    }

    public void setUID(Long UID) {
        if (UID == -1) {
            System.out.println("Error uid invalid == -1");
        }
        //System.out.println("UID: "+UID);
        this.UID = UID;
    }
}
