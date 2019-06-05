package com.example.a7510.herdproject.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.a7510.herdproject.ViewHolder.QBUnreadMessageHolder;
import com.example.a7510.herdproject.R;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

//An Adapter object acts as a bridge between an AdapterView and the underlying data for that view. The Adapter provides access to the data items. The Adapter is also responsible for making a View for each item in the data set.

public class ChatDialogsAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<QBChatDialog> qbChatDialogs;

    public ChatDialogsAdapter(Context context, ArrayList<QBChatDialog> qbChatDialogs) {
        this.context = context;
        this.qbChatDialogs = qbChatDialogs;
    }

    @Override
    public int getCount() {
        return qbChatDialogs.size();
    }

    @Override
    public Object getItem(int i) {
        return qbChatDialogs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View view1 = view;

        if (view1 == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view1 = inflater.inflate(R.layout.list_chat_dialog, null);

            TextView textTitle, textMessage;
            final ImageView imageView, image_unread;

            textMessage = (TextView)view1.findViewById(R.id.list_chat_dialog_message);
            textTitle = (TextView)view1.findViewById(R.id.list_chat_dialog_title);
            imageView = (ImageView)view1.findViewById(R.id.image_chat_dialog);
            image_unread = (ImageView)view1.findViewById(R.id.unread_image);

            textMessage.setText(qbChatDialogs.get(i).getLastMessage());
            textTitle.setText(qbChatDialogs.get(i).getName());

            ColorGenerator generator = ColorGenerator.MATERIAL;
            int randomColor = generator.getRandomColor();

            if(qbChatDialogs.get(i).getPhoto().equals("null")) {
                TextDrawable.IBuilder builder = TextDrawable.builder().beginConfig().withBorder(4).endConfig().round();


                //Get the first character from the chat dialog title to create chat dialog image
                TextDrawable drawable = builder.build(textTitle.getText().toString().substring(0, 1).toUpperCase(), randomColor);

                imageView.setImageDrawable(drawable);
            }
            else{
                //Download bitmap from QuickBlox server and set for dialog
                QBContent.getFile(Integer.parseInt(qbChatDialogs.get(i).getPhoto())).performAsync(new QBEntityCallback<QBFile>() {
                    @Override
                    public void onSuccess(QBFile qbFile, Bundle bundle) {
                        String fileURL = qbFile.getPublicUrl();
                        Picasso.with(context).load(fileURL).resize(50, 50).centerCrop().into(imageView);

                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.e("ERROR_IMAGE", e.getMessage() +"");
                    }
                });
            }

            //Set unread message count
            TextDrawable.IBuilder unreadBuilder = TextDrawable.builder().beginConfig().withBorder(4).endConfig().round();
            int unread_count = QBUnreadMessageHolder.getInstance().getBundle().getInt(qbChatDialogs.get(i).getDialogId());

            if(unread_count > 0){
                TextDrawable unread_drawable = unreadBuilder.build(unread_count + "", Color.RED);
                image_unread.setImageDrawable(unread_drawable);
            }
        }

        return view1;
    }

}
