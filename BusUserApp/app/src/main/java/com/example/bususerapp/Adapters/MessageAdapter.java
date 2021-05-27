package com.example.bususerapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bususerapp.Classes.Message;
import com.example.bususerapp.R;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {

    private AppCompatActivity context;

    private List<Message> messageList;

    private TextView textViewUser, textViewMessage;

    // Pass in message list
    public MessageAdapter(AppCompatActivity context, List<Message> messageList){
        super(context, R.layout.card_message,messageList);
        this.context = context;
        this.messageList = messageList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View messageView = inflater.inflate(R.layout.card_message, null, true);

        // Initialize
        textViewUser = (TextView) messageView.findViewById(R.id.textViewUser);
        textViewMessage = (TextView) messageView.findViewById(R.id.textViewMessage);

        textViewMessage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        Message message = messageList.get(position);

        // Set message to textview
        textViewUser.setText(message.getUser());
        textViewMessage.setText(message.getText());

        return messageView;
    }
}
