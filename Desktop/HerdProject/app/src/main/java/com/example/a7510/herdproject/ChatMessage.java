package com.example.a7510.herdproject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bhargavms.dotloader.DotLoader;
import com.example.a7510.herdproject.Adapter.ChatMessageAdapter;
import com.example.a7510.herdproject.Common.Common;
import com.example.a7510.herdproject.ViewHolder.QBChatMessagesHolder;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBChatDialogParticipantListener;
import com.quickblox.chat.listeners.QBChatDialogTypingListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.model.QBPresence;
import com.quickblox.chat.request.QBDialogRequestBuilder;
import com.quickblox.chat.request.QBMessageGetBuilder;
import com.quickblox.chat.request.QBMessageUpdateBuilder;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestUpdateBuilder;
import com.squareup.picasso.Picasso;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

public class ChatMessage extends AppCompatActivity implements QBChatDialogMessageListener {

    QBChatDialog qbChatDialog;
    ListView firstChatMessages;
    ImageButton submitButton;
    EditText editContent;
    ChatMessageAdapter adapter;

    //Update dialog
    android.support.v7.widget.Toolbar toolbar;

    //Typing
    DotLoader dotLoader;

    //Variables used for deleting and updating messages
    int contextMenuIndexClicked = -1;
    boolean isEditMode = false;
    QBChatMessage editMessage;

    //Update online user
    ImageView image_online_count, dialog_avatar;
    TextView text_online_count;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Check the type of the dialog, if it GROUP or PUBLIC_GROUP, it must be inflater menu
        if(qbChatDialog.getType() == QBDialogType.GROUP || qbChatDialog.getType() == QBDialogType.PUBLIC_GROUP){
            getMenuInflater().inflate(R.menu.chat_message_group_menu, menu);
        }
        return true;
    }


    //Function that checks what item was selected in the group chat and runs appropiate methods using a switch case
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.chat_group_edit_name:
                editGroupName();
                break;
            case R.id.chat_group_add_user:
                addUser();
                break;
            case R.id.chat_group_remove_user:
                removeUser();
                break;
        }

        return true;
    }

    //This function will get all users who are going to be removed and display them in a list
    private void removeUser() {
        Intent intent = new Intent(this, ListUsers.class);
        intent.putExtra(Common.UPDATE_DIALOG_EXTRA, qbChatDialog);
        intent.putExtra(Common.UPDATE_MODE, Common.UPDATE_REMOVE_MODE);
        startActivity(intent);
    }

    //This function will get all users not joined in the chat group and display it as a list
    private void addUser() {
        Intent intent = new Intent(this, ListUsers.class);
        intent.putExtra(Common.UPDATE_DIALOG_EXTRA, qbChatDialog);
        intent.putExtra(Common.UPDATE_MODE, Common.UPDATE_ADD_MODE);
        startActivity(intent);
    }

    //This function will create an alert dialog so the user can input the new group and update group after user presses OK
    private void editGroupName() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_edit_group_layout, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(view);
        final EditText newName = (EditText)view.findViewById(R.id.edit_new_group_name);

        //Set the dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Set the name of from the dialog box
                qbChatDialog.setName(newName.getText().toString());

                QBDialogRequestBuilder requestBuilder = new QBDialogRequestBuilder();
                QBRestChatService.updateGroupChatDialog(qbChatDialog, requestBuilder).performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                        Toast.makeText(ChatMessage.this, "Group name changed", Toast.LENGTH_SHORT).show();
                        toolbar.setTitle(qbChatDialog.getName());
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(getBaseContext(), e.getMessage() + "", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });


        //Create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    //Function which gets the message selected and either updates it or deletes depending on what he user clicked
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //Get the index context from menu click
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        contextMenuIndexClicked = info.position;

        switch(item.getItemId()){
            case R.id.chat_message_update_message:
                updateMessage();
                break;
            case R.id.chat_message_delete_message:
                deleteMessage();
                break;

        }

        return true;
    }

    //Function that deletes a message in group chat
    private void deleteMessage() {

        final ProgressDialog deleteDialog = new ProgressDialog(ChatMessage.this);
        deleteDialog.setMessage("Please wait...");
        deleteDialog.show();

        editMessage = QBChatMessagesHolder.getInstance().getChatMessagesByDialogId(qbChatDialog.getDialogId()).get(contextMenuIndexClicked);

        QBRestChatService.deleteMessage(editMessage.getId(), false).performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                retrieveMessage();
                deleteDialog.dismiss();
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });

    }

    //Function that updates a message in group chat
    private void updateMessage() {
        //Set the message for editText
        editMessage = QBChatMessagesHolder.getInstance().getChatMessagesByDialogId(qbChatDialog.getDialogId()).get(contextMenuIndexClicked);
        editContent.setText(editMessage.getBody());

        //Set edit mode to true
        isEditMode = true;
    }

    //Populates the context menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.chat_message_context_menu, menu);
    }

    //Removes listener on destroy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        qbChatDialog.removeMessageListrener(this);
    }

    //Removes listener on stop
    @Override
    protected void onStop() {
        super.onStop();
        qbChatDialog.removeMessageListrener(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);

        initializeViews();
        initializeChatDialogs();
        retrieveMessage();

        //Checks whether the chat is private or not and puts the message in the dialog
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editContent.getText().toString().isEmpty()) {


                    if (!isEditMode) {
                        QBChatMessage chatMessage = new QBChatMessage();
                        chatMessage.setBody(editContent.getText().toString());
                        chatMessage.setSenderId(QBChatService.getInstance().getUser().getId());
                        chatMessage.setSaveToHistory(true);

                        try {
                            qbChatDialog.sendMessage(chatMessage);
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }

                        //To make sure private chat does not show message
                        if (qbChatDialog.getType() == QBDialogType.PRIVATE) {
                            //Cache message
                            QBChatMessagesHolder.getInstance().putMessages(qbChatDialog.getDialogId(), chatMessage);
                            ArrayList<QBChatMessage> messages = QBChatMessagesHolder.getInstance().getChatMessagesByDialogId(chatMessage.getDialogId());

                            adapter = new ChatMessageAdapter(getBaseContext(), messages);
                            firstChatMessages.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }

                        //Remove text from the editText
                        editContent.setText("");
                        editContent.setFocusable(true);
                    } else {
                        final ProgressDialog updateDialog = new ProgressDialog(ChatMessage.this);
                        updateDialog.setMessage("Please wait...");
                        updateDialog.show();

                        QBMessageUpdateBuilder messageUpdateBuilder = new QBMessageUpdateBuilder();
                        messageUpdateBuilder.updateText(editContent.getText().toString()).markDelivered().markRead();

                        QBRestChatService.updateMessage(editMessage.getId(), qbChatDialog.getDialogId(), messageUpdateBuilder).performAsync(new QBEntityCallback<Void>() {
                            @Override
                            public void onSuccess(Void aVoid, Bundle bundle) {
                                //Refresh the data
                                retrieveMessage();

                                //Reset the variable
                                isEditMode = false;

                                updateDialog.dismiss();

                                //Reset editText
                                editContent.setText("");
                                editContent.setFocusable(true);
                            }

                            @Override
                            public void onError(QBResponseException e) {
                                Toast.makeText(getBaseContext(), e.getMessage() + "", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });

    }

    //Function that retrieves a message and puts into an adapter for use
    private void retrieveMessage() {
        QBMessageGetBuilder messageGetBuilder = new QBMessageGetBuilder();
        messageGetBuilder.setLimit(500); // the limit is 500 messages

        if(qbChatDialog != null){
            QBRestChatService.getDialogMessages(qbChatDialog, messageGetBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatMessage>>() {
                @Override
                public void onSuccess(ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {
                    //Put messages to the cache
                    QBChatMessagesHolder.getInstance().putMessages(qbChatDialog.getDialogId(), qbChatMessages);

                    adapter = new ChatMessageAdapter(getBaseContext(), qbChatMessages);
                    firstChatMessages.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onError(QBResponseException e) {

                }
            });
        }
    }

    //Initializes chat dialogs
    private void initializeChatDialogs() {
        qbChatDialog = (QBChatDialog)getIntent().getSerializableExtra(Common.DIALOG_EXTRA);

        if(qbChatDialog.getPhoto() != null && !qbChatDialog.getPhoto().equals("null")){
            QBContent.getFile(Integer.parseInt(qbChatDialog.getPhoto())).performAsync(new QBEntityCallback<QBFile>() {
                @Override
                public void onSuccess(QBFile qbFile, Bundle bundle) {
                    String fileURL = qbFile.getPublicUrl();
                    Picasso.with(getBaseContext()).load(fileURL).resize(50, 50).centerCrop().into(dialog_avatar);

                }

                @Override
                public void onError(QBResponseException e) {
                    Log.e("ERROR_IMAGE", e.getMessage() + "");
                }
            });
        }

        qbChatDialog.initForChat(QBChatService.getInstance());

        //Register listener to the incoming messages
        QBIncomingMessagesManager incomingMessage = QBChatService.getInstance().getIncomingMessagesManager();
        incomingMessage.addDialogMessageListener(new QBChatDialogMessageListener() {
            @Override
            public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {

            }

            @Override
            public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {

            }
        });

        //Add typing listener
        registerTypingForChatDialog(qbChatDialog);

        //Add join group to enable the group chat
        if(qbChatDialog.getType() == QBDialogType.PUBLIC_GROUP || qbChatDialog.getType() == QBDialogType.GROUP){
            DiscussionHistory discussionHistory = new DiscussionHistory();
            discussionHistory.setMaxStanzas(0);

            qbChatDialog.join(discussionHistory, new QBEntityCallback() {
                @Override
                public void onSuccess(Object o, Bundle bundle) {

                }

                @Override
                public void onError(QBResponseException e) {
                    Log.d("ERROR", e.getMessage() + "");
                }
            });
        }

         QBChatDialogParticipantListener participantListener = new QBChatDialogParticipantListener() {
            @Override
            public void processPresence(String dialogId, QBPresence qbPresence) {
                if(dialogId == qbChatDialog.getDialogId()){
                    QBRestChatService.getChatDialogById(dialogId).performAsync(new QBEntityCallback<QBChatDialog>() {
                        @Override
                        public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                            //Get online user
                            try {
                                Collection<Integer> onlineList = qbChatDialog.getOnlineUsers();
                                TextDrawable.IBuilder builder = TextDrawable.builder().beginConfig().withBorder(4).endConfig().round();
                                TextDrawable online = builder.build("", Color.RED);
                                image_online_count.setImageDrawable(online);

                                text_online_count.setText(String.format("%d/%d online", onlineList.size(), qbChatDialog.getOccupants().size()));
                            } catch (XMPPException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(QBResponseException e) {

                        }
                    });

                }
            }
        };

        qbChatDialog.addParticipantListener(participantListener);

        qbChatDialog.addMessageListener(this);

        //Set title for the toolbar
        toolbar.setTitle(qbChatDialog.getName());
        setSupportActionBar(toolbar);

    }

    //Function that is used to display status typing
    private void registerTypingForChatDialog(QBChatDialog qbChatDialog) {
        QBChatDialogTypingListener typingListener = new QBChatDialogTypingListener() {
            @Override
            public void processUserIsTyping(String dialogId, Integer integer) {
                if(dotLoader.getVisibility() != View.VISIBLE){
                    dotLoader.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void processUserStopTyping(String dialogId, Integer integer) {
                if(dotLoader.getVisibility() != View.INVISIBLE){
                    dotLoader.setVisibility(View.INVISIBLE);
                }
            }
        };

        qbChatDialog.addIsTypingListener(typingListener);
    }

    //Initializes views
    private void initializeViews() {
        dotLoader = (DotLoader)findViewById(R.id.dot_loader);

        firstChatMessages = (ListView)findViewById(R.id.list_of_messages);
        submitButton = (ImageButton)findViewById(R.id.send_button);
        editContent = (EditText)findViewById(R.id.edit_content);

        editContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    qbChatDialog.sendIsTypingNotification();
                } catch (XMPPException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    qbChatDialog.sendStopTypingNotification();
                } catch (XMPPException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }
        });

        image_online_count = (ImageView)findViewById(R.id.image_online_count);
        text_online_count = (TextView)findViewById(R.id.text_online_count);

        //Dialog avatar
        dialog_avatar = (ImageView)findViewById(R.id.dialog_avatar);
        dialog_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent selectImage = new Intent();
                selectImage.setType("image/*");
                selectImage.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(selectImage, "Select Picture"), Common.SELECT_PICTURE);
            }
        });


        //Add menu context
        registerForContextMenu(firstChatMessages);

        //Add Toolbar
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.chat_message_toolbar);

    }

    //When the image is clicked inside a group chat it runs this function and this function processes that action
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == Common.SELECT_PICTURE){
                Uri selectedImageUri = data.getData();
                final ProgressDialog messageDialog = new ProgressDialog(ChatMessage.this);
                messageDialog.setMessage("Please wait...");
                messageDialog.setCancelable(false);
                messageDialog.show();

                try{
                    //Convert uri to file
                    InputStream in = getContentResolver().openInputStream(selectedImageUri);
                    final Bitmap bitmap = BitmapFactory.decodeStream(in);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                    File file = new File(Environment.getExternalStorageDirectory() + "/image.png");
                    FileOutputStream fileOut = new FileOutputStream(file);
                    fileOut.write(bos.toByteArray());
                    fileOut.flush();
                    fileOut.close();

                    int imageSizeKb = (int)file.length() / 1024;

                    //if size greater than maximum size just display error message
                    if(imageSizeKb >= (1024 * 100)){
                        Toast.makeText(this, "Error: file size too large", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //Upload the file
                    QBContent.uploadFileTask(file, true, null).performAsync(new QBEntityCallback<QBFile>() {
                        @Override
                        public void onSuccess(QBFile qbFile, Bundle bundle) {
                            qbChatDialog.setPhoto(qbFile.getId().toString());

                            //Update the chat dialog
                            QBRequestUpdateBuilder requestUpdateBuilder = new QBRequestUpdateBuilder();
                            QBRestChatService.updateGroupChatDialog(qbChatDialog, requestUpdateBuilder).performAsync(new QBEntityCallback<QBChatDialog>() {
                                @Override
                                public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                                    messageDialog.dismiss();
                                    dialog_avatar.setImageBitmap(bitmap);
                                }

                                @Override
                                public void onError(QBResponseException e) {
                                    Toast.makeText(ChatMessage.this, e.getMessage() + "", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }

                        @Override
                        public void onError(QBResponseException e) {

                        }
                    });

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //Function that processes a message
    @Override
    public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
        //Cache message
        QBChatMessagesHolder.getInstance().putMessages(qbChatMessage.getDialogId(), qbChatMessage);
        ArrayList<QBChatMessage> messages = QBChatMessagesHolder.getInstance().getChatMessagesByDialogId(qbChatMessage.getDialogId());
        adapter = new ChatMessageAdapter(getBaseContext(), messages);
        firstChatMessages.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    //Function that processes an error
    @Override
    public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {
        Log.e("ERROR", e.getMessage() + "");
    }
}
