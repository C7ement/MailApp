package com.example.smartmail_v2_androidx.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartmail_v2_androidx.R;
import com.example.smartmail_v2_androidx.activities.Mailbox.MailActivityListener;
import com.example.smartmail_v2_androidx.activities.Mailbox.MbViewModel;
import com.example.smartmail_v2_androidx.adapters.FolderlistAdapter;
import com.example.smartmail_v2_androidx.adapters.SearchlistAdapter;

public class NavviewFragment extends Fragment {

    private MailActivityListener listener;
    private MbViewModel model;
    private String pathPrefix;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NavviewFragment() {
    }

    public static NavviewFragment newInstance(String prefix) {
        NavviewFragment myFragment = new NavviewFragment();

        Bundle args = new Bundle();
        args.putString("prefix", prefix);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = ViewModelProviders.of(getActivity()).get(MbViewModel.class);
        pathPrefix = getArguments().getString("prefix");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_navview, container, false);

        //RecyclerView
        FolderlistAdapter folderlistAdapter = new FolderlistAdapter(listener,getChildFragmentManager());
        model.getRootFolder().observe(this, folderlistAdapter::setRootFolder);
        RecyclerView recyclerView = view.findViewById(R.id.navview_folderlist_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(folderlistAdapter);

        SearchlistAdapter searchlistAdapter = new SearchlistAdapter(listener,getChildFragmentManager());
        model.getSearchList().observe(this, searchlistAdapter::setRootFolder);
        recyclerView = view.findViewById(R.id.navview__searchlist_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(searchlistAdapter);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MailActivityListener) {
            listener = (MailActivityListener) context;
        } else {
            throw new RuntimeException(context.toString()  + " must implement OnNavViewFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }



}
