package com.example.a7510.herdproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.a7510.herdproject.Adapter.ChatDialogsAdapter;
import com.example.a7510.herdproject.Common.Common;
import com.example.a7510.herdproject.ViewHolder.QBChatDialogHolder;
import com.example.a7510.herdproject.ViewHolder.QBUnreadMessageHolder;
import com.example.a7510.herdproject.ViewHolder.QBUsersHolder;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.BaseService;
import com.quickblox.auth.session.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBSystemMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ChatDialogs extends AppCompatActivity implements QBSystemMessageListener, QBChatDialogMessageListener {

    FloatingActionButton floatingActionButton;
    ListView firstChatDialogs;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.chat_dialog_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        switch(item.getItemId()){
            case R.id.context_delete_dialog:
                deleteDialog(info.position);
                break;
        }

        return true;
    }

    private void deleteDialog(int index) {
        final QBChatDialog chatDialog = (QBChatDialog)firstChatDialogs.getAdapter().getItem(index);
        QBRestChatService.deleteDialog(chatDialog.getDialogId(), false).performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                QBChatDialogHolder.getInstance().removeDialog(chatDialog.getDialogId());
                ChatDialogsAdapter adapter = new ChatDialogsAdapter(getBaseContext(), QBChatDialogHolder.getInstance().getAllChatDialogs());
                firstChatDialogs.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_dialog_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.chat_dialog_menu_user:
                showUserProfile();
                break;
            default:
                break;
        }

        return true;
    }

    private void showUserProfile() {
        Intent intent = new Intent(ChatDialogs.this, UserProfile.class);
        startActivity(intent);
    }

    protected void onResume(){
        super.onResume();
        loadChatDialogs();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_dialogs);

        //Add Toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.chat_dialog_toolbar);
        toolbar.setTitle("Herd Chat");
        setSupportActionBar(toolbar);

        createSessionForChat();

        firstChatDialogs = (ListView)findViewById(R.id.firstChatDialogs);

        registerForContextMenu(firstChatDialogs);

        //Send the chat dialog information to another activity
        firstChatDialogs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                QBChatDialog qbChatDialog = (QBChatDialog)firstChatDialogs.getAdapter().getItem(i);
                Intent intent = new Intent(ChatDialogs.this, ChatMessage.class);
                intent.putExtra(Common.DIALOG_EXTRA, qbChatDialog);
                startActivity(intent);
            }
        });

        loadChatDialogs();





        floatingActionButton = (FloatingActionButton)findViewById(R.id.chat_dialog_add_user);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatDialogs.this, ListUsers.class);
                startActivity(intent);
            }
        });



    }

    private void loadChatDialogs() {
        QBRequestGetBuilder requestGetBuilder = new QBRequestGetBuilder();
        requestGetBuilder.setLimit(100);

        QBRestChatService.getChatDialogs(null, requestGetBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatDialog>>() {
            @Override
            public void onSuccess(final ArrayList<QBChatDialog> qbChatDialogs, Bundle bundle) {

                //Put all dialogs to the cache
                QBChatDialogHolder.getInstance().putDialogs(qbChatDialogs);

                //Unread Settings
                Set<String> setIds = new HashSet<>();

                for(QBChatDialog chatDialog : qbChatDialogs){
                    setIds.add(chatDialog.getDialogId());
                }

                //Get Messages that are unread
                QBRestChatService.getTotalUnreadMessagesCount(setIds, QBUnreadMessageHolder.getInstance().getBundle()).performAsync(new QBEntityCallback<Integer>() {
                    @Override
                    public void onSuccess(Integer integer, Bundle bundle) {
                        //Save to the cache
                        QBUnreadMessageHolder.getInstance().setBundle(bundle);

                        //Refresh list dialogs

                        ChatDialogsAdapter adapter = new ChatDialogsAdapter(getBaseContext(), QBChatDialogHolder.getInstance().getAllChatDialogs());
                        firstChatDialogs.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("ERROR", e.getMessage());
            }
        });
    }

    private void createSessionForChat() {
        final ProgressDialog messageDialog = new ProgressDialog(ChatDialogs.this);
        messageDialog.setMessage("Please wait...");
        messageDialog.setCanceledOnTouchOutside(false);
        messageDialog.show();

        String user, password;
        user = getIntent().getStringExtra("user");
        password = getIntent().getStringExtra("password");

        //Load all the users and save to the cache
        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                QBUsersHolder.getInstance().putUsers(qbUsers);
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });

        final QBUser qbUser = new QBUser(user, password);
        QBAuth.createSession(qbUser).performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
                qbUser.setId(qbSession.getUserId());
                try {
                    qbUser.setPassword(BaseService.getBaseService().getToken());
                } catch (BaseServiceException e) {
                    e.printStackTrace();
                }

                QBChatService.getInstance().login(qbUser, new QBEntityCallback() {
                    @Override
                    public void onSuccess(Object o, Bundle bundle) {
                        messageDialog.dismiss();

                        QBSystemMessagesManager qbSystemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
                        qbSystemMessagesManager.addSystemMessageListener(ChatDialogs.this);

                        QBIncomingMessagesManager qbIncomingMessagesManager = QBChatService.getInstance().getIncomingMessagesManager();
                        qbIncomingMessagesManager.addDialogMessageListener(ChatDialogs.this);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.e("ERROR", e.getMessage() + "");
                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

    @Override
    public void processMessage(QBChatMessage qbChatMessage) {
        //Put dialog to cache
        QBRestChatService.getChatDialogById(qbChatMessage.getBody()).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                //Put to cache
                QBChatDialogHolder.getInstance().putDialog(qbChatDialog);
                ArrayList<QBChatDialog> adapterSource = QBChatDialogHolder.getInstance().getAllChatDialogs();
                ChatDialogsAdapter adapters = new ChatDialogsAdapter(getBaseContext(), adapterSource);
                firstChatDialogs.setAdapter(adapters);
                adapters.notifyDataSetChanged();
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

    @Override
    public void processError(QBChatException e, QBChatMessage qbChatMessage) {
        Log.e("ERROR", e.getMessage() + "");
    }

    @Override
    public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
        loadChatDialogs();
    }

    @Override
    public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {

    }
}
