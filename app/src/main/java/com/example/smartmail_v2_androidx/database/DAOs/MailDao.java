package com.example.smartmail_v2_androidx.database.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.smartmail_v2_androidx.database.entities.Mail;

import java.util.List;

@Dao
public interface MailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)// maybe ignore instead of replace if it is faster
    long insert(Mail mail);

    @Query("DELETE FROM mail_table where id = :id")
    void deleteMail(long id);

    @Query("SELECT * from mail_table WHERE mailboxLabel = :mailboxLabel AND folder = :folderName ORDER BY receivedDate DESC")
    LiveData<List<Mail>> getAllMails(String mailboxLabel, String folderName);

    @Query("SELECT * from mail_table WHERE mailboxLabel = :mailboxLabel AND folder = :folderName AND content LIKE :search ORDER BY receivedDate DESC")
    LiveData<List<Mail>> getMailSearchResult(String mailboxLabel, String folderName, String search);

    @Query("SELECT * from mail_table WHERE id = :mailID")
    LiveData<Mail> getMail(long mailID);

    @Query("SELECT msgNumber from mail_table WHERE id = :mailID")
    int getMessageNumber(long mailID);


}
