package com.example.smartmail_v2_androidx.FolderAndListhierarchy;

import android.text.TextUtils;

import com.example.smartmail_v2_androidx.database.entities.Mail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.mail.Folder;

public class CustomFolder extends Leaf {

    private List<CustomFolder> children = new ArrayList<>();
    private List<Mail> mails = new ArrayList<>();
    private String fullName;
    private String name;
    private int depth;
    private String prefix;
    private boolean isOpen = false;

    public CustomFolder() {}

    public CustomFolder(String fullName) {
        this.fullName = fullName;
        String[] path = fullName.split("/", -1);
        this.depth = fullName.equals("") ? -1 : path.length-1;
        this.name = fullName.equals("") ? "" : path[depth];
        this.prefix = fullName.equals("") ? "" : this.fullName+"/";
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getFullName() {
        return fullName;
    }


    public int getDepth() {
        return this.depth;
    }

    public void open() {
        this.isOpen = true;
    }
    public void close() {
        this.isOpen = false;
        /*
        for (CustomFolder child : children) {
            child.close();
        }*/
    }
    public boolean isOpen() {
        return isOpen;
    }

    public CustomFolder getFolder(String fullName) {
        if (this.fullName.equals(fullName)) {
            return this;
        }
        for (CustomFolder folder : children){
            if (folder != null) {
                return folder;
            }
        }
        return null;
    }

    public String toString() {
        StringBuilder output = new StringBuilder();
        for (CustomFolder child : children) {
            output.append(child.toString());
            output.append("\n");
        }
        output.append(fullName);
        return output.toString();
    }

    public boolean hasChildren() {
        return (children.size()+mails.size()) > 0;
    }

    public void setChildrenFromString(String foldersString) {
        String[] array = foldersString.split("\n");
        List<String> folders = new ArrayList<>(Arrays.asList(array));
        for (String folder : folders) {
            CustomFolder child = new CustomFolder(folder);
            if (child.getDepth() == depth+1 && folder.startsWith(prefix)) {
                children.add(child);
                child.setChildrenFromString(foldersString);
            }
        }
    }

    public static CustomFolder getRootFromArray(Folder[] folders) {
        List<String> folderList = new ArrayList<>();
        for (Folder folder : folders) {
            folderList.add((folder.getFullName()));
        }
        CustomFolder root = new CustomFolder("");
        root.setChildrenFromString(TextUtils.join("\n", folderList));
        return root;
    }

    public List<CustomFolder> getDisplayed() {
        List<CustomFolder> displayed = new ArrayList<>();
        displayed.add(this);
        if (isOpen) {
            for (CustomFolder child : children) {
                displayed.addAll(child.getDisplayed());
            }
        }
        return displayed;
    }



    public List<CustomFolder> getAllChildren() {
        List<CustomFolder> allChildren = new ArrayList<>();
        allChildren.add(this);
        for (CustomFolder child : children) {
            allChildren.addAll(child.getAllChildren());
        }
        return allChildren;
    }

    public List<Leaf> getDisplayedWithMails() {
        List<Leaf> displayed = new ArrayList<>();
        displayed.add(this);
        if (isOpen) {
            for (CustomFolder child : children) {
                displayed.addAll(child.getDisplayedWithMails());
            }
            for (Mail mail : mails) {
                mail.setName(mail.getSubject());
                mail.setDepth(getDepth()+1);
                displayed.add(mail);
            }
        }
        return displayed;
    }

    public static CustomFolder generateFromString(String strFolders) {
        CustomFolder root = new CustomFolder("");
        root.setChildrenFromString(strFolders);
        root.open();
        return root;
    }


    public List<Mail> getMails() {
        return mails;
    }

    public void setMails(List<Mail> mails) {
        this.mails = mails;
    }
}
