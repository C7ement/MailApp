package com.example.smartmail_v2_androidx.fragments.mail;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.smartmail_v2_androidx.R;
import com.example.smartmail_v2_androidx.activities.Mailbox.MailActivityListener;
import com.example.smartmail_v2_androidx.activities.Mailbox.MbViewModel;
import com.example.smartmail_v2_androidx.database.entities.Mail;
import com.example.smartmail_v2_androidx.fragments.dialog.MailDataFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.Objects;

public class MailFragment extends Fragment {

    private MailActivityListener mListener;
    private MbViewModel model;
    private JSONArray files;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = ViewModelProviders.of(getActivity()).get(MbViewModel.class);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mail, container, false);
        final TextView subject = view.findViewById(R.id.fragment_mail_subject);
        final WebView content = view.findViewById(R.id.fragment_mail_content);
        final TextView from = view.findViewById(R.id.fragment_mail_sender);
        content.setWebViewClient(new WebViewClient() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (url.contains("cid:")) {
                    url = url.replace("cid:","");
                    Log.v("WebView", "Looking for image with cid [" + url + "]");
                    for (int i=0; i < files.length(); i++) {
                        try {
                            JSONObject file = files.getJSONObject(i);
                            System.out.println("cid: "+file.get("cid").toString());
                            System.out.println("url: "+url);
                            if (file.get("cid").toString().contains(url)) {
                                String filename = file.get("filename").toString();
                                Log.v("WebView", "Replacing [" + url + "] with "+filename);
                                String extension = MimeTypeMap.getFileExtensionFromUrl(filename);
                                String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                                return new WebResourceResponse(type, null, Objects.requireNonNull(getContext()).openFileInput(filename));
                            }
                        } catch (JSONException | FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return super.shouldInterceptRequest(view, url);
            }
/*
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
                    view.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                } else {
                    return false;
                }
            }*/
        });

        model.getMail().observe(this, mail -> {
            if (mail != null) {
                model.markAsRead(mail);
                files = mail.getFiles();
                subject.setText(mail.getSubject());
                String sender = mail.getSender().getPersonal() == null ? mail.getSender().toString() : mail.getSender().getPersonal();
                from.setText(sender);
                String html =  mail.getContent();
                content.loadDataWithBaseURL("email://", html, "text/html", "utf-8", null);
            }
        });

        subject.setOnClickListener(viewClicked -> mListener.nextMail());

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_mail_contextmenu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.fragment_mail_menu_data) {
            if (model.getMail().getValue() != null) {
                MailDataFragment maildataDialog = new MailDataFragment();
            /*maildataDialog.setListener(() -> {
                displayToast("CLICKED");
                maildataDialog.dismiss();
            });*/
                maildataDialog.setMail(model.getMail().getValue());
                maildataDialog.show(getActivity().getSupportFragmentManager(), "Mail Data Dialog");
            }

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MailActivityListener) {
            mListener = (MailActivityListener) context;
        } else {
            throw new RuntimeException(context.toString()  + " must implement OnMailFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
