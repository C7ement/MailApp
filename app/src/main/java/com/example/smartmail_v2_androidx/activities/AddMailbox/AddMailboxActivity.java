package com.example.smartmail_v2_androidx.activities.AddMailbox;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartmail_v2_androidx.R;

public class AddMailboxActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "com.example.android.wordlistsql.REPLY";

    private EditText editLabel;
    private EditText editHost;
    private EditText editUsername;
    private EditText editPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newmb);
        editLabel = findViewById(R.id.edit_label);
        editHost = findViewById(R.id.edit_host);
        editUsername = findViewById(R.id.edit_username);
        editPassword = findViewById(R.id.edit_password);

        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent replyIntent = new Intent();
                if (TextUtils.isEmpty(editLabel.getText())
                        | TextUtils.isEmpty(editHost.getText())
                        | TextUtils.isEmpty(editUsername.getText())
                        | TextUtils.isEmpty(editPassword.getText())) {
                    setResult(RESULT_CANCELED, replyIntent);
                } else {
                    replyIntent.putExtra("label", editLabel.getText().toString());
                    replyIntent.putExtra("host", editHost.getText().toString());
                    replyIntent.putExtra("username", editUsername.getText().toString());
                    replyIntent.putExtra("password", editPassword.getText().toString());
                    setResult(RESULT_OK, replyIntent);
                }
                finish();
            }
        });
    }
}