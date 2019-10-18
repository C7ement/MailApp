package com.example.smartmail_v2_androidx.fragments.mail;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.smartmail_v2_androidx.R;
import com.example.smartmail_v2_androidx.activities.Mailbox.MailActivityListener;
import com.example.smartmail_v2_androidx.activities.Mailbox.MbViewModel;
import com.example.smartmail_v2_androidx.adapters.MailListAdapter2;
import com.example.smartmail_v2_androidx.adapters.MailListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MailListFragment extends Fragment {

    private MailActivityListener mListener;
    private MbViewModel model;
    private RecyclerView recyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MailListFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = ViewModelProviders.of(getActivity()).get(MbViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_maillist, container, false);
        final TextView folderNameView = view.findViewById(R.id.fragment_maillist_foldername);
        SwipeRefreshLayout swipeLayout = view.findViewById(R.id.fragment_maillist_swiperefresh);
        swipeLayout.setOnRefreshListener(() -> mListener.onMailListRefresh());
        model.getLoadingMailList().observe(this, refreshing -> swipeLayout.setRefreshing((boolean)refreshing));
        //RecyclerView
        MailListAdapter adapter = new MailListAdapter(mListener);


        model.getFolderName().observe(this, folderNameView::setText);
        model.getMailList().observe(this, adapter::setMails);

        recyclerView = view.findViewById(R.id.fragment_maillist_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.getItemAnimator().setChangeDuration(0);

        //FAB
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(viewClicked -> mListener.onWriteNewMailClicked());

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {



        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.fragment_maillist_contextmenu, menu);
        MenuItem myActionMenuItem = menu.findItem(R.id.fragment_maillist_menu_search);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getContext(), "SearchOnQueryTextSubmit: " + query, Toast.LENGTH_SHORT).show();
                model.setSearchQuery(query);
                if( ! searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                myActionMenuItem.collapseActionView();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.fragment_maillist_menu_settings) {
            return true;
        } else if (id == R.id.fragment_maillist_menu_checkable_item) {
            item.setChecked(!item.isChecked());
            if (item.isChecked()) {
                MailListAdapter2 adapter = new MailListAdapter2(mListener);
                model.getMailList().observe(this, adapter::setMails);
                recyclerView.setAdapter(adapter);
            } else {
                MailListAdapter adapter = new MailListAdapter(mListener);
                model.getMailList().observe(this, adapter::setMails);
                recyclerView.setAdapter(adapter);
            }
            return true;
        } else if (id == R.id.fragment_maillist_menu_newmail) {
            mListener.onNewSimpleMail(model.getFolderName().getValue());
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MailActivityListener) {
            mListener = (MailActivityListener) context;
        } else {
            throw new RuntimeException(context.toString()  + " must implement OnMailListFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
