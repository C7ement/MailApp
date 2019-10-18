package com.example.smartmail_v2_androidx.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.smartmail_v2_androidx.database.DAOs.MailboxDao;
import com.example.smartmail_v2_androidx.database.DAOs.MailDao;
import com.example.smartmail_v2_androidx.database.entities.Mail;
import com.example.smartmail_v2_androidx.database.entities.Mailbox;

@Database(entities = {Mailbox.class, Mail.class}, version = 1)
public abstract class MailRoomDatabase extends RoomDatabase {

    public abstract MailboxDao mailboxDao();
    public abstract MailDao mailDao();
    private static final String DB_NAME = "mail.db";

    private static volatile MailRoomDatabase INSTANCE;

    public static MailRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MailRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE =
                            Room.databaseBuilder(context.getApplicationContext(),
                                    MailRoomDatabase.class, DB_NAME)
                                    .addCallback(sRoomDatabaseCallback)
                                    .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback(){

                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final MailboxDao mDao;

        PopulateDbAsync(MailRoomDatabase db) {
            mDao = db.mailboxDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            Mailbox mailbox = new Mailbox("Zimbra", "", "", "");
            mDao.insert(mailbox);
            mailbox = new Mailbox("Laposte", "", "", "");
            mDao.insert(mailbox);
            return null;
        }
    }
}