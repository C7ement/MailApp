package com.example.smartmail_v2_androidx.database.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.smartmail_v2_androidx.database.entities.Mailbox;

import java.util.List;


@Dao
public interface MailboxDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Mailbox mailbox);

    @Query("DELETE FROM mailbox_table where label = :label")
    void deleteMailbox(String label);

    @Query("SELECT * from mailbox_table ORDER BY label ASC")
    LiveData<List<Mailbox>> getAllMailboxes();


    @Query("SELECT * from mailbox_table WHERE label = :label")
    Mailbox getMailboxAsync(String label);

    @Query("SELECT * from mailbox_table WHERE label = :label")
    LiveData<Mailbox> getMailbox(String label);

    @Query("SELECT folders from mailbox_table WHERE label = :label")
    LiveData<String> getFolders(String label);


    @Query("SELECT folderSeparator from mailbox_table WHERE label = :label")
    LiveData<Character> getFolderSeparator(String label);

}