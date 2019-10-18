package com.example.smartmail_v2_androidx.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.smartmail_v2_androidx.R;
import com.example.smartmail_v2_androidx.activities.Mailbox.MailActivityListener;
import com.example.smartmail_v2_androidx.activities.Mailbox.MbViewModel;
import com.example.smartmail_v2_androidx.database.entities.Mailbox;
import com.example.smartmail_v2_androidx.fragments.dialog.SelectFolderFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;

public class MailWritingFragment extends Fragment {

    private MailActivityListener mListener;
    private MbViewModel model;
    private JSONArray files;
    private EditText subject;
    private EditText to;
    private EditText content;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MailWritingFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = ViewModelProviders.of(getActivity()).get(MbViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mailwritting, container, false);
        final TextView from = view.findViewById(R.id.fragment_mailwriting_from);
        subject = view.findViewById(R.id.fragment_mailwriting_subject);
        to = view.findViewById(R.id.fragment_mailwriting_to);
        content = view.findViewById(R.id.fragment_mailwriting_content);
        final FloatingActionButton fab = view.findViewById(R.id.fragment_mailwriting_fab);

        fab.setOnClickListener(viewClicked -> {
            CharSequence text = "Error: No Mailbox Selected!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getContext(), text, duration);
            toast.show();
        });

        model.getMailbox().observe(this, mailbox -> {
            from.setText(mailbox.getUsername());

            fab.setOnClickListener(viewClicked -> {
                if (TextUtils.isEmpty(to.getText())
                        | TextUtils.isEmpty(subject.getText())
                        | TextUtils.isEmpty(content.getText())) {
                    CharSequence text = "Error: Empty Field!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getContext(), text, duration);
                    toast.show();

                } else {
                    mListener.sendMail(mailbox,to.getText().toString(),subject.getText().toString(),content.getText().toString());
                }
            });
        });

        setHasOptionsMenu(true);

        return view;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_mailwritting_contextmenu, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.fragment_mailwritting_menu_settings) {

            SelectFolderFragment moveToDialogue = new SelectFolderFragment();
            moveToDialogue.setListener((folder) -> {
                model.saveMailToFolder(folder.getFullName(), to.getText().toString(), subject.getText().toString(), content.getText().toString());
                Toast.makeText(getContext(), folder.getName()+" clicked", Toast.LENGTH_SHORT).show();
                moveToDialogue.dismiss();
            });
            moveToDialogue.show(getFragmentManager(), "Move To Dialog");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MailActivityListener) {
            mListener = (MailActivityListener) context;
        } else {
            throw new RuntimeException(context.toString()  + " must implement OnMailWritingFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
