package com.example.smartmail_v2_androidx.activities.Mailbox;

import android.app.Application;
import android.os.Build;
import android.provider.Settings;
import android.util.Pair;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.smartmail_v2_androidx.FolderAndListhierarchy.CustomFolder;
import com.example.smartmail_v2_androidx.MailRepository;
import com.example.smartmail_v2_androidx.database.entities.Mail;
import com.example.smartmail_v2_androidx.database.entities.Mailbox;

import java.util.List;

public class MbViewModel extends AndroidViewModel {

    private MailRepository mRepository;
    private String mailboxLabelconst;


    private final MutableLiveData<String> mailboxLabel = new MutableLiveData<>();
    private final MutableLiveData<Long> mailId = new MutableLiveData<>();
    private LiveData<Boolean> mailListLoadingSource = null;
    private final MediatorLiveData<Boolean> loadingMailList = new MediatorLiveData<>();
    private final MutableLiveData<String> folderName = new MutableLiveData<>();
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();

    MbViewModel(Application application, String mailboxLabelconst) {
        super(application);
        mRepository = new MailRepository(getApplication());
        this.mailboxLabelconst = mailboxLabelconst;
        searchQuery.setValue("");
    }

    void loadFolder(String folderName) {
        mRepository.loadMailsFromFolder(mailboxLabelconst, folderName);
    }

    void loadFolders() {
        mRepository.loadFolders(mailboxLabelconst);
    }

    public LiveData<String> getMailboxLabel() {
        return mailboxLabel;
    }

    void setMailId(Long id) {
        mailId.setValue(id);
    }
    LiveData<Long> getMailId() {
        return mailId;
    }
    public void markAsRead(Mail mail) {
        mRepository.markAsRead(mail);
    }


    public LiveData<Mailbox> getMailbox() {
        return mRepository.getMailbox(mailboxLabelconst);
    }

    void sendMail(Mailbox mailbox, String to, String subject, String content) {
        mRepository.sendEmail(mailbox, to, subject, content);
    }

    public void saveMailToFolder(String folder, String to, String subject, String content) {
        mRepository.saveMailToFolder(mailboxLabelconst, folder, to, subject, content);
    }

    public LiveData<String> getFolderName() {
        return folderName;
    }

    void setFolderName(String name) {
        folderName.setValue(name);
        setSearchQuery("");
    }
    public void setSearchQuery(String search) {
        searchQuery.setValue(search);
    }

    public MediatorLiveData getLoadingMailList() {
        return loadingMailList;
    }
    void reloadFolder() {
        if (mailListLoadingSource != null) loadingMailList.removeSource(mailListLoadingSource);
        mailListLoadingSource = mRepository.reloadMailsFromFolder(mailboxLabelconst, folderName.getValue());
        loadingMailList.addSource(mailListLoadingSource, loadingMailList::setValue);
    }

    public LiveData<CustomFolder> getRootFolder() {
        return Transformations.map(mRepository.getFolders(mailboxLabelconst), CustomFolder::generateFromString);
    }
    public LiveData<CustomFolder> getFolder(String fullName) {
        return Transformations.map(mRepository.getFolders(mailboxLabelconst), (str)->{
            CustomFolder folder = CustomFolder.generateFromString(str).getFolder(fullName);
            return folder == null ? new CustomFolder() : folder;
        });
    }

    void createSubfolder(String folderName) {
        mRepository.createFolder(folderName, mailboxLabelconst);
    }
    void deleteFolder(String folderName) {
        mRepository.deleteFolder(mailboxLabelconst, folderName);
    }


    public LiveData<Mail> getMail() {
        return Transformations.switchMap(mailId, (id) -> mRepository.getMail(id) );
    }

    class SearchLiveData extends MediatorLiveData<Pair<String, String>> {
        SearchLiveData(LiveData<String> folderName, LiveData<String> search) {
            addSource(folderName, first -> setValue(Pair.create(first, search.getValue())));
            addSource(search, second -> setValue(Pair.create(folderName.getValue(), second)));
        }
    }

    public LiveData<List<Mail>> getMailList() {
        return Transformations.switchMap(new SearchLiveData(folderName, searchQuery), (data) ->
                mRepository.getMailSearchResult(mailboxLabelconst, data.first, data.second) );
    }

    public LiveData<CustomFolder> getSearchList() {
        return Transformations.switchMap(getFolder("[SeachList]"), (folder) -> {
            MediatorLiveData<CustomFolder> searchList = new MediatorLiveData<>();
            searchList.setValue(folder);
            for (CustomFolder child : folder.getAllChildren()) {
                System.out.println("FOOOOOOOOOOOOOOLDER : "+child.getFullName());
                searchList.addSource(mRepository.getAllMails(mailboxLabelconst, child.getFullName()), child::setMails);
            }
            return searchList;
        });
    }
}