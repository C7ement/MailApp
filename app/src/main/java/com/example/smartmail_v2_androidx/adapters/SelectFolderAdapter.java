package com.example.smartmail_v2_androidx.adapters;


        import android.util.Pair;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;

        import androidx.annotation.NonNull;
        import androidx.recyclerview.widget.RecyclerView;

        import com.example.smartmail_v2_androidx.R;
        import com.example.smartmail_v2_androidx.FolderAndListhierarchy.CustomFolder;
        import com.example.smartmail_v2_androidx.fragments.dialog.SelectFolderFragment;

        import java.util.ArrayList;
        import java.util.List;

public class SelectFolderAdapter extends RecyclerView.Adapter<SelectFolderAdapter.ViewHolder> {

    private SelectFolderFragment.SelectFolderListener listener;
    private List<Pair<String, String>> folders = new ArrayList<>(); //Pair<String name, String state>
    private boolean isOpen = false;
    private CustomFolder root = new CustomFolder("");
    private List<String> openFolders;

    public SelectFolderAdapter(SelectFolderFragment.SelectFolderListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_selectfolder_rv_item, parent, false);
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
            System.out.println("Select folder item clicked : "+current.getFullName());
            listener.onClick(current);
        });
        holder.openClose.setOnClickListener(v -> {
            if(current.isOpen()) {
                current.close();
            } else {
                current.open();
            }
            notifyDataSetChanged();
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
        TextView folderName;
        TextView openClose;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            folderName = itemView.findViewById(R.id.fragment_selectfolder_rv_item_folderName);
            openClose = itemView.findViewById(R.id.fragment_selectfolder_rv_item_openclose);
        }
    }
}
