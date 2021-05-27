package com.example.bususerapp.Classes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.example.bususerapp.R;

import java.util.List;

public class PostList extends ArrayAdapter<Post> {

    // Declare variable

    private FragmentActivity context;

    private List<Post> postList;

    private TextView textViewTitle, textViewDescription;

    // Pass post list
    public PostList(FragmentActivity context, List<Post> postList){
        super(context, R.layout.card_post,postList);
        this.context = context;
        this.postList = postList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View view = inflater.inflate(R.layout.card_post, null, true);

        // Initialize
        textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
        textViewDescription = (TextView)view.findViewById(R.id.textViewDescription);

        textViewDescription.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        Post post = postList.get(position);

        String userId = post.getUserId();

        // Set title and description to textView
        textViewTitle.setText(post.getTitle());
        textViewDescription.setText(post.getDescription());

        return view;
    }
}