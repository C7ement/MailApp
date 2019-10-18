package com.example.smartmail_v2_androidx.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartmail_v2_androidx.FolderAndListhierarchy.Leaf;
import com.example.smartmail_v2_androidx.R;
import com.example.smartmail_v2_androidx.FolderAndListhierarchy.CustomFolder;
import com.example.smartmail_v2_androidx.activities.Mailbox.MailActivityListener;

public class FolderlistAdapter extends RecyclerView.Adapter<FolderlistAdapter.ViewHolder> {


    private final FragmentManager fragmentManager;
    private MailActivityListener listener;
    private CustomFolder root = new CustomFolder("");

    public FolderlistAdapter(MailActivityListener listener, FragmentManager childFragmentManager) {
        this.listener = listener;
        this.fragmentManager = childFragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_navview_recyclerview_folder, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CustomFolder current = root.getDisplayed().get(position);
        StringBuilder displayedName = new StringBuilder();
        for(int i = 0; i < current.getDepth(); i++){
            displayedName.append("\t");
        }
        displayedName.append(current.getName());
        holder.folderName.setText(displayedName);
        if (current.hasChildren()) {
            holder.openClose.setVisibility(View.VISIBLE);
            if(current.isOpen()) {
                holder.openClose.setText("-");
            } else {
                holder.openClose.setText("+");
            }
        } else {
            holder.openClose.setVisibility(View.GONE);
        }
        holder.folderName.setOnClickListener(v -> {
            if (listener != null) listener.onFolderNameClicked(current.getFullName());
        });
        holder.openClose.setOnClickListener(v -> {
            if(current.isOpen()) {
                current.close();
            } else {
                current.open();
            }
            notifyDataSetChanged();
        });
        holder.menu.setOnClickListener(view -> {

            //creating a popup menu
            PopupMenu popup = new PopupMenu(view.getContext(), view);
            //inflating menu from xml resource
            popup.inflate(R.menu.fragment_navview_rv_item_menu);
            //adding click listener
            popup.setOnMenuItemClickListener(item -> {
                System.out.println("popup item clicked : "+item.getTitle());
                switch (item.getItemId()) {
                    case R.id.fragment_navvieww_rv_item_menu_newfolder:
                        if (listener != null) listener.onCreateSubFolder(current.getFullName());
                        return true;
                    case R.id.fragment_navvieww_rv_item_menu_moveto:
                        if (listener != null) listener.onMoveTo(current.getFullName());
                        return true;
                    case R.id.fragment_navvieww_rv_item_menu_delete:
                        if (listener != null) listener.onDelete(current.getFullName());
                        return true;
                    case R.id.fragment_navvieww_rv_item_menu_addmail:
                        if (listener != null) listener.onNewSimpleMail(current.getFullName());
                        return true;
                    default:
                        return false;
                }
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return root.getDisplayed().size();
    }

    public void setRootFolder(CustomFolder root) {
        this.root = root;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final View view;
        TextView folderName;
        TextView openClose;
        TextView menu;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            folderName = itemView.findViewById(R.id.fragment_navview_rv_item_folderName);
            openClose = itemView.findViewById(R.id.fragment_navview_rv_item_openclose);
            menu = itemView.findViewById(R.id.fragment_navview_rv_item_menu);
        }
    }
}
