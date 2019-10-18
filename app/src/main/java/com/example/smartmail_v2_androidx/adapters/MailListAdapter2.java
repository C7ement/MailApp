package com.example.smartmail_v2_androidx.adapters;

import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.smartmail_v2_androidx.R;
import com.example.smartmail_v2_androidx.activities.Mailbox.MailActivityListener;
import com.example.smartmail_v2_androidx.database.entities.Mail;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.mail.internet.InternetAddress;

public class MailListAdapter2 extends RecyclerView.Adapter<MailListAdapter2.ViewHolder> {

    private List<Mail> mails;
    private MailActivityListener listener;

    public MailListAdapter2(MailActivityListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_maillist_rv_item2, parent, false);
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mails != null) {//delete
            final Mail current = mails.get(position);

            InternetAddress addr = current.getSender();
            if (addr != null) {
                if (addr.getPersonal() != null) {
                    holder.sender.setText(addr.getPersonal());
                } else {
                    holder.sender.setText(addr.getAddress());
                }
            }

            holder.subject.setText(current.getSubject());

            holder.content.setVisibility(View.VISIBLE);
            String html =  current.getContent();
            holder.content.loadDataWithBaseURL("email://", html, "text/html", "utf-8", null);

            if(!current.isSEEN()) {
                holder.sender.setTypeface(holder.sender.getTypeface(), Typeface.BOLD);
                holder.subject.setTypeface(holder.subject.getTypeface(), Typeface.BOLD);
                holder.date.setTypeface(holder.date.getTypeface(), Typeface.BOLD);
            }

            Locale locale = Locale.FRANCE;
            long dateLong = current.getReceivedDate().getTime();
            String pattern;
            if(DateUtils.isToday(dateLong)) {
                pattern = "H:mm";
            } else {
                pattern = "d MMM";
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, locale);
            String dateString = simpleDateFormat.format(dateLong);
            holder.date.setText(dateString);

            holder.view.setOnClickListener(v -> {
                if (null != listener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    listener.onMailClicked(current.getId());
                    System.out.println(current.getId());
                }
            });
        } else {
            // Covers the case of data not being ready yet.
            holder.subject.setText("No Mails");
        }
    }

    // getItemCount() is called many times, and when it is first called,
    // mMailboxes has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        return mails == null ? 0 : mails.size();
    }

    public void setMails(List<Mail> mails){
        this.mails = mails;
        if (mails == null || mails.size()==0)  {
            System.out.println("MailListAdapter mails == null - "+mails.get(0).getFolder());
        } else {
            System.out.println("MailListAdapter  "+mails.get(0).getFolder());
        }
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private final View view;
        private final TextView sender;
        private final TextView subject;
        private final WebView content;
        private final TextView date;
        private ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            sender = itemView.findViewById(R.id.fragment_maillist2_rv_item_sender);
            subject = itemView.findViewById(R.id.fragment_maillist2_rv_item_subject);
            content = itemView.findViewById(R.id.fragment_maillist2_rv_item_content);
            date = itemView.findViewById(R.id.fragment_maillist2_rv_item_date);
        }

    }
}