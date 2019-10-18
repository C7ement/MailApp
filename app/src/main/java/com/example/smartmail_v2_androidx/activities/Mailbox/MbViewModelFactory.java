package com.example.smartmail_v2_androidx.activities.Mailbox;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MbViewModelFactory implements ViewModelProvider.Factory {
    private Application mApplication;
    private String mMailboxLabel;


    MbViewModelFactory(Application application, String mailboxLabel) {
        mApplication = application;
        mMailboxLabel = mailboxLabel;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MbViewModel(mApplication, mMailboxLabel);
    }
}