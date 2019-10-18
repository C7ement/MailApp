package com.example.smartmail_v2_androidx.activities.Main;

import android.content.Intent;
import android.os.Bundle;

import com.example.smartmail_v2_androidx.activities.Mailbox.MailboxActivity;
import com.example.smartmail_v2_androidx.activities.AddMailbox.AddMailboxActivity;
import com.example.smartmail_v2_androidx.R;
import com.example.smartmail_v2_androidx.database.entities.Mailbox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MbListAdapter.OnMbClickListener {

    public static final int NEW_MAILBOX_ACTIVITY_REQUEST_CODE = 1;

    private MbListViewModel mMbListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mblist);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.mbListRecyclerview);

        final MbListAdapter adapter = new MbListAdapter(this,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get a new or existing ViewModel from the ViewModelProvider.
        mMbListViewModel = ViewModelProviders.of(this).get(MbListViewModel.class);

        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        mMbListViewModel.getAllMailboxes().observe(this, new Observer<List<Mailbox>>() {
            @Override
            public void onChanged(@Nullable final List<Mailbox> mailboxes) {
                // Update the cached copy of the mailboxes in the adapter.
                adapter.setMailboxes(mailboxes);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddMailboxActivity.class);
                startActivityForResult(intent, NEW_MAILBOX_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_MAILBOX_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            String label = data.getStringExtra("label");
            String host = data.getStringExtra("host");
            String username = data.getStringExtra("username");
            String password = data.getStringExtra("password");
            Mailbox mailbox = new Mailbox(label, host, username, password);
            mMbListViewModel.insert(mailbox);
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public void onMbClick(String mailboxLabel) {

        System.out.println("Opening fragment_navview...");
        Intent intent = new Intent(this, MailboxActivity.class);
        intent.putExtra("mailboxLabel", mailboxLabel);
        startActivity(intent);
    }

}