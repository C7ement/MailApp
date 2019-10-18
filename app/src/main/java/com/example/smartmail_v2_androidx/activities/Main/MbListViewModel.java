package com.example.smartmail_v2_androidx.activities.Main;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import com.example.smartmail_v2_androidx.MailRepository;
import com.example.smartmail_v2_androidx.database.entities.Mailbox;

public class MbListViewModel extends AndroidViewModel {

    private MailRepository mRepository;

    private LiveData<List<Mailbox>> mAllWords;

    public MbListViewModel(Application application) {
        super(application);
        mRepository = new MailRepository(application);
        mAllWords = mRepository.getAllMailboxes();
    }

    public LiveData<List<Mailbox>> getAllMailboxes() { return mAllWords; }

    public void insert(Mailbox mailbox) { mRepository.insert(mailbox); }
}