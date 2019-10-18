package com.example.smartmail_v2_androidx.fragments.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartmail_v2_androidx.FolderAndListhierarchy.CustomFolder;
import com.example.smartmail_v2_androidx.activities.Mailbox.MbViewModel;
import com.example.smartmail_v2_androidx.adapters.SelectFolderAdapter;

public class SelectFolderFragment extends DialogFragment {

    private RecyclerView recyclerView;
    private SelectFolderListener listener;
    public SelectFolderFragment() {
    }
    public void setListener(SelectFolderListener listener) {
        this.listener = listener;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        recyclerView = new RecyclerView(getContext());

        SelectFolderAdapter adapter = new SelectFolderAdapter(listener);
        MbViewModel model = ViewModelProviders.of(getActivity()).get(MbViewModel.class);
        model.getRootFolder().observe(this, adapter::setRootFolder);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return new AlertDialog.Builder(getActivity())
                .setTitle("Move To")
                .setView(recyclerView)
                .setNegativeButton("Cancel",
                        (dialog, whichButton) -> Toast.makeText(getContext(), "CANCELED", Toast.LENGTH_SHORT).show()
                ).create();
    }

    public interface SelectFolderListener {
        void onClick(CustomFolder folder);
    }
}
