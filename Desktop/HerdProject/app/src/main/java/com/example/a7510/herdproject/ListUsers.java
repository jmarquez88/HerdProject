package com.example.a7510.herdproject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.a7510.herdproject.Adapter.ListUserAdapter;
import com.example.a7510.herdproject.Common.Common;
import com.example.a7510.herdproject.ViewHolder.QBUsersHolder;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.request.QBDialogRequestBuilder;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;

import java.util.ArrayList;
import java.util.List;

public class ListUsers extends AppCompatActivity {

    ListView firstUsers;
    Button buttonCreateChat;

    String mode = "";
    QBChatDialog qbChatDialog;
    List<QBUser> addUser = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);

        mode = getIntent().getStringExtra(Common.UPDATE_MODE);
        qbChatDialog = (QBChatDialog)getIntent().getSerializableExtra(Common.UPDATE_DIALOG_EXTRA);


        firstUsers = (ListView)findViewById(R.id.first_users);
        firstUsers.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        
        buttonCreateChat = (Button)findViewById(R.id.button_create_chat);
        buttonCreateChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mode == null) {

                    int countChoice = firstUsers.getCount();

                    if (firstUsers.getCheckedItemPositions().size() == 1) {
                        createPrivateChat(firstUsers.getCheckedItemPositions());

                    } else if (firstUsers.getCheckedItemPositions().size() > 1) {
                        createGroupChat(firstUsers.getCheckedItemPositions());
                    } else {
                        Toast.makeText(ListUsers.this, "Please select a user to chat", Toast.LENGTH_SHORT).show();

                    }
                }
                else if(mode.equals(Common.UPDATE_ADD_MODE) && qbChatDialog != null){
                    if(addUser.size() > 0){
                        QBDialogRequestBuilder requestBuilder = new QBDialogRequestBuilder();

                        int countChoice = firstUsers.getCount();
                        SparseBooleanArray checkItemPositions = firstUsers.getCheckedItemPositions();

                        for(int i = 0; i < countChoice; i++){
                            if(checkItemPositions.get(i)){
                                QBUser user = (QBUser)firstUsers.getItemAtPosition(i);
                                requestBuilder.addUsers(user);
                            }
                        }

                        //Call services
                        QBRestChatService.updateGroupChatDialog(qbChatDialog, requestBuilder).performAsync(new QBEntityCallback<QBChatDialog>() {
                            @Override
                            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                                Toast.makeText(getBaseContext(), "User was added successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void onError(QBResponseException e) {

                            }
                        });

                    }
                }
                else if(mode.equals(Common.UPDATE_REMOVE_MODE)  && qbChatDialog != null){
                    if(addUser.size() > 0){
                        QBDialogRequestBuilder requestBuilder = new QBDialogRequestBuilder();
                        int countChoice = firstUsers.getCount();
                        SparseBooleanArray checkItemPositions = firstUsers.getCheckedItemPositions();
                        for(int i = 0; i < countChoice; i++){
                            if(checkItemPositions.get(i)){
                                QBUser user = (QBUser)firstUsers.getItemAtPosition(i);
                                requestBuilder.removeUsers(user);
                            }
                        }

                        //Call services
                        QBRestChatService.updateGroupChatDialog(qbChatDialog, requestBuilder).performAsync(new QBEntityCallback<QBChatDialog>() {
                            @Override
                            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                                Toast.makeText(getBaseContext(), "The user was removed successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void onError(QBResponseException e) {

                            }
                        });
                    }
                }
            }
        });

        //Check if mode is null and QBChatDialog is null
        if(mode == null && qbChatDialog == null){
            retrieveAllUsers();
        }
        else{
            if(mode.equals(Common.UPDATE_ADD_MODE)){
                loadListAvailableUser();
            }
            else if(mode.equals(Common.UPDATE_REMOVE_MODE)){
                loadListUserInGroup();
            }
        }

    }

    //This function will show all availabe users in group
    private void loadListUserInGroup() {
        buttonCreateChat.setText("Remove User");
        QBRestChatService.getChatDialogById(qbChatDialog.getDialogId()).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                List<Integer> occupantsId = qbChatDialog.getOccupants();
                List<QBUser> listUserAlreadyInGroup = QBUsersHolder.getInstance().getUsersByIds(occupantsId);
                ArrayList<QBUser> users = new ArrayList<QBUser>();

                users.addAll(listUserAlreadyInGroup);

                ListUserAdapter adapter = new ListUserAdapter(getBaseContext(), users);
                firstUsers.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                addUser = users;
            }

            @Override
            public void onError(QBResponseException e) {
                Toast.makeText(ListUsers.this, e.getMessage() + "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadListAvailableUser() {
        buttonCreateChat.setText("Add User");

        QBRestChatService.getChatDialogById(qbChatDialog.getDialogId()).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                ArrayList<QBUser> listUsers = QBUsersHolder.getInstance().getAllUsers();
                //Get occupant ID's from chat
                List<Integer> occupantsId = qbChatDialog.getOccupants();
                List<QBUser> listUserAlreadyInChatGroup = QBUsersHolder.getInstance().getUsersByIds(occupantsId);

                //Remove all user already in chat group
                for(QBUser user:listUserAlreadyInChatGroup){
                    listUsers.remove(user);
                }

                if(listUsers.size() > 0){
                    ListUserAdapter adapter = new ListUserAdapter(getBaseContext(), listUsers);
                    firstUsers.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    addUser = listUsers;
                }
            }

            @Override
            public void onError(QBResponseException e) {
                Toast.makeText(ListUsers.this, e.getMessage() + "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createGroupChat(SparseBooleanArray checkedItemPositions) {
        final ProgressDialog messageDialog = new ProgressDialog(ListUsers.this);
        messageDialog.setMessage("Please wait...");
        messageDialog.setCanceledOnTouchOutside(false);
        messageDialog.show();

        int countChoice = firstUsers.getCount();
        ArrayList<Integer> occupantIdsList = new ArrayList<>();

        for(int i = 0; i < countChoice; i++){
            if (checkedItemPositions.get(i)){
                QBUser user = (QBUser)firstUsers.getItemAtPosition(i);
                occupantIdsList.add(user.getId());
            }
        }

        //Create the chat dialog
        QBChatDialog dialog = new QBChatDialog();
        dialog.setName(Common.createChatDialogName(occupantIdsList));
        dialog.setType(QBDialogType.GROUP);
        dialog.setOccupantsIds(occupantIdsList);

        QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                messageDialog.dismiss();
                Toast.makeText(getBaseContext(), "Create chat dialog successful", Toast.LENGTH_SHORT).show();

                //Send the system message to recipient ID user
                QBSystemMessagesManager qbSystemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
                QBChatMessage qbChatMessage = new QBChatMessage();
                qbChatMessage.setBody(qbChatDialog.getDialogId());

                for(int i = 0; i < qbChatDialog.getOccupants().size(); i++){
                    qbChatMessage.setRecipientId(qbChatDialog.getOccupants().get(i));

                    try {
                        qbSystemMessagesManager.sendSystemMessage(qbChatMessage);
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                }


                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("ERROR", e.getMessage());
            }
        });

    }

    private void createPrivateChat(SparseBooleanArray checkedItemPositions){
        final ProgressDialog messageDialog = new ProgressDialog(ListUsers.this);
        messageDialog.setMessage("Please wait...");
        messageDialog.setCanceledOnTouchOutside(false);
        messageDialog.show();

        int countChoice = firstUsers.getCount();

        for(int i = 0; i < countChoice; i++){
            if(checkedItemPositions.get(i)){
                final QBUser user = (QBUser)firstUsers.getItemAtPosition(i);
                QBChatDialog dialog = DialogUtils.buildPrivateDialog(user.getId());

                QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                        messageDialog.dismiss();
                        Toast.makeText(getBaseContext(), "Create private chat dialog successful", Toast.LENGTH_SHORT).show();

                        //Send the system message to recipient ID user
                        QBSystemMessagesManager qbSystemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
                        QBChatMessage qbChatMessage = new QBChatMessage();
                        qbChatMessage.setRecipientId(user.getId());
                        qbChatMessage.setBody(qbChatDialog.getDialogId());

                        try {
                            qbSystemMessagesManager.sendSystemMessage(qbChatMessage);
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }

                        finish();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.e("ERROR", e.getMessage());
                    }
                });
            }
        }

    }


    private void retrieveAllUsers() {
        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {

                //Add to cache
                QBUsersHolder.getInstance().putUsers(qbUsers);

                ArrayList<QBUser> qbUserWithoutCurrent = new ArrayList<QBUser>();
                for(QBUser user : qbUsers){
                    if(!user.getLogin().equals(QBChatService.getInstance().getUser().getLogin())){
                        qbUserWithoutCurrent.add(user);
                    }
                }

                ListUserAdapter adapter = new ListUserAdapter(getBaseContext(), qbUserWithoutCurrent);
                firstUsers.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("ERROR", e.getMessage());
            }
        });
    }
}
