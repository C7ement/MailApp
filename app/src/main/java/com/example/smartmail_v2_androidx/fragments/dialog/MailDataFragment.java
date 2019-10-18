package com.example.smartmail_v2_androidx.fragments.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.smartmail_v2_androidx.R;
import com.example.smartmail_v2_androidx.database.entities.Mail;

import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.InternetAddress;

public class MailDataFragment extends DialogFragment {

    private Mail mail;
    private View view;
    //private MailDataListener listener;

    public MailDataFragment() {
    }
    /*public void setListener(MailDataListener listener) {
        this.listener = listener;
    }*/
    public void setMail(Mail mail) {
        this.mail = mail;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_maildata, null);

        //MbViewModel model = ViewModelProviders.of(getActivity()).get(MbViewModel.class);

        TextView from = view.findViewById(R.id.fragment_maildata_from);
        TextView to = view.findViewById(R.id.fragment_maildata_to);
        TextView tag = view.findViewById(R.id.fragment_maildata_tag);
        TextView flags = view.findViewById(R.id.fragment_maildata_flags);
        String sender = mail.getSender().getPersonal() == null ? mail.getSender().toString() : mail.getSender().getPersonal();
        from.setText(sender);
        List<String> recipients = new ArrayList<>();
        for (InternetAddress addr : mail.getRecipients()) {
            recipients.add( addr.toUnicodeString() );
        }
        to.setText(recipients.toString());
        flags.setText(mail.getFlags().toString());

        return new AlertDialog.Builder(getActivity())
                .setTitle("Data")
                .setView(view)
                .setNegativeButton("Close", (dialog, whichButton) -> {})
                .create();
    }

    /*public interface MailDataListener {
        void onClick();
    }*/

}
