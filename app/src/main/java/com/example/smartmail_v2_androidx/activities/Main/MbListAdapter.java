package com.example.smartmail_v2_androidx.activities.Main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.example.smartmail_v2_androidx.R;
import com.example.smartmail_v2_androidx.database.entities.Mailbox;

public class MbListAdapter extends RecyclerView.Adapter<MbListAdapter.MbListViewHolder> {

    private Context context;
    private final LayoutInflater mInflater;
    private List<Mailbox> mMailboxes;
    private OnMbClickListener onMbClickListener;

    //try remove on of the param snce their are the same
    public MbListAdapter(Context context, OnMbClickListener onMbClickListener) {
        mInflater = LayoutInflater.from(context);
        this.onMbClickListener = onMbClickListener;
    }

    @Override
    public MbListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.mblist_recyclerview_item, parent, false);
        return new MbListViewHolder(itemView, onMbClickListener);
    }

    @Override
    public void onBindViewHolder(MbListViewHolder holder, int position) {
        if (mMailboxes != null) {
            final Mailbox current = mMailboxes.get(position);
            holder.mbItemView.setText(current.getLabel());
        } else {
            // Covers the case of data not being ready yet.
            holder.mbItemView.setText("No Mailbox");
        }
    }

    public void setMailboxes(List<Mailbox> mailboxes){
        mMailboxes = mailboxes;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mMailboxes has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mMailboxes != null)
            return mMailboxes.size();
        else return 0;
    }

    class MbListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView mbItemView;
        OnMbClickListener onMbClickListener;
        private MbListViewHolder(View itemView, OnMbClickListener onMbClickListener) {
            super(itemView);
            mbItemView = itemView.findViewById(R.id.mailboxLabel);
            this.onMbClickListener = onMbClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onMbClickListener.onMbClick(mMailboxes.get(getAdapterPosition()).getLabel());
        }
    }

    public interface OnMbClickListener {
        void onMbClick(String mailboxLabel);
    }
}