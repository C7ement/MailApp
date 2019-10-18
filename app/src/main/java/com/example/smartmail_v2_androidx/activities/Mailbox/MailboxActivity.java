package com.example.smartmail_v2_androidx.activities.Mailbox;

import android.content.Intent;
import android.os.Bundle;

import com.example.smartmail_v2_androidx.R;
import com.example.smartmail_v2_androidx.database.entities.Mail;
import com.example.smartmail_v2_androidx.database.entities.Mailbox;
import com.example.smartmail_v2_androidx.fragments.dialog.SelectFolderFragment;
import com.example.smartmail_v2_androidx.fragments.mail.MailFragment;
import com.example.smartmail_v2_androidx.fragments.mail.MailListFragment;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.example.smartmail_v2_androidx.fragments.MailWritingFragment;
import com.example.smartmail_v2_androidx.fragments.NavviewFragment;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

public class MailboxActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MailActivityListener{

    private MbViewModel mMbViewModel;
    private String mailboxLabel;
    private boolean displayContent = false;
    private MailFragment mailFragment = new MailFragment();
    private NavigationView navigationView;
    private SearchView searchView;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mb);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Mailbox");
        setSupportActionBar(toolbar);

        //get fragment_maillist_contextmenu id
        Intent intent = getIntent();
        mailboxLabel = intent.getStringExtra("mailboxLabel");

        //drawer
        drawer = findViewById(R.id.mb_drawer);
        navigationView = findViewById(R.id.mb_navview);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);



        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment mailListFragment;
        mailListFragment = new MailListFragment();
        fragmentTransaction.add(R.id.mailbox_activity_container, mailListFragment);

        NavviewFragment navviewFragment = NavviewFragment.newInstance("");
        fragmentTransaction.add(R.id.mb_navview, navviewFragment);

        fragmentTransaction.commit();

        // Get a new or existing ViewModel from the ViewModelProvider.
        mMbViewModel = ViewModelProviders.of(this, new MbViewModelFactory(this.getApplication(), mailboxLabel)).get(MbViewModel.class);
        mMbViewModel.loadFolders();
        mMbViewModel.loadFolder("INBOX");
        mMbViewModel.setFolderName("INBOX");


    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMailClicked(long mailId) {
        System.out.println("Opening mail...");

        mMbViewModel.setMailId(mailId);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mailbox_activity_container, mailFragment);
        transaction.addToBackStack(null);//test with adding to backstack
        transaction.commit();

    }

    @Override
    public void onWriteNewMailClicked() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mailbox_activity_container, new MailWritingFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onMailListRefresh() {
        mMbViewModel.reloadFolder();
        displayToast("Loading New Messages...");
    }

    @Override
    public void onFolderNameClicked(String folderName) {
        System.out.println("Folder clicked : " + folderName);
        drawer.closeDrawers();
        displayToast("Loading "+folderName);
        mMbViewModel.setFolderName(folderName);
        mMbViewModel.loadFolder(folderName);
    }

    @Override
    public void onSearchNameClicked(Mail mail) {
        System.out.println("Search item clicked : "+mail.getSubject());
        drawer.closeDrawers();
        displayToast("Loading list "+mail.getSubject());
        mMbViewModel.setFolderName("INBOX");
        mMbViewModel.setSearchQuery(mail.getContent());
    }

    @Override
    public void onCreateSubFolder(String folderName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText input = new EditText(this);
        //input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        builder.setTitle("New sublfoder")
                .setMessage("Enter name for subfolder of '"+folderName+"' :")
                .setView(input)
                .setPositiveButton("Create", (dialog, id) -> mMbViewModel.createSubfolder(folderName+"/"+input.getText().toString()))
                .setNegativeButton("Cancel", (dialog, id) -> {
                    displayToast("CANCELED");
                    dialog.cancel();
                });
        builder.show();
    }

    @Override
    public void onMoveTo(String folderName) {
        SelectFolderFragment moveToDialogue = new SelectFolderFragment();
        moveToDialogue.setListener((folder) -> {
            displayToast("CLICKED");
            moveToDialogue.dismiss();
        });
        moveToDialogue.show(getSupportFragmentManager(), "Move To Dialog");
    }

    @Override
    public void onDelete(String folderName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New mail")
                .setMessage("")
                .setPositiveButton("Delete", (dialog, id) -> mMbViewModel.deleteFolder(folderName))
                .setNegativeButton("Cancel", (dialog, id) -> displayToast("CANCELED"));
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void nextMail() {
        mMbViewModel.setMailId(mMbViewModel.getMailId().getValue()+1);
    }

    @Override
    public void sendMail(Mailbox mailbox, String to, String subject, String content) {
        mMbViewModel.sendMail(mailbox, to, subject, content);
        getSupportFragmentManager().popBackStack();
        InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    @Override
    public void onNewSimpleMail(String folderName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view = getLayoutInflater().inflate(R.layout.dialog_simplemailwritting, null);
        final TextView from = view.findViewById(R.id.dialog_simplemailwriting_from);
        final EditText to = view.findViewById(R.id.dialog_simplemailwriting_to);
        final EditText subject = view.findViewById(R.id.dialog_simplemailwriting_subject);
        final EditText content = view.findViewById(R.id.dialog_simplemailwriting_content);

        mMbViewModel.getMailbox().observe(this, (mailbox) -> {
            if(mailbox != null) {
                from.setText(mailbox.getUsername());//todo change for select
            }
        });

        builder.setTitle("New simple mail")
                .setMessage("Edit mail to save in '"+folderName+"' folder :")
                .setView(view)
                .setPositiveButton("Save", (dialog, id) -> {
                    mMbViewModel.saveMailToFolder(folderName, to.getText().toString(), subject.getText().toString(), content.getText().toString());
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    displayToast("CANCELED");
                    dialog.cancel();
                });
        builder.show();
    }

    private void displayToast(CharSequence text) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
        toast.show();
    }
}