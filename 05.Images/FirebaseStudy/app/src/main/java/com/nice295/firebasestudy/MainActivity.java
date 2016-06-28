package com.nice295.firebasestudy;

import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

public  class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final String MESSAGES_CHILD = "messages-image";
    public static final String DEFAULT_IMAGE_URL = "https://firebasestorage.googleapis.com/v0/b/fir-study-38299.appspot.com/o/food%2Fct-timothy-otooles-offers-hamburger-hop-menu-2-001?alt=media&token=0ba8b2ae-18fd-406d-b391-798eb0ca1d95";

    private Button mAddButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private EditText mEditTextTitle;
    private EditText mEditTextDesc;

    private FirebaseRecyclerAdapter<Message, MessageViewHolder> mFirebaseAdapter;
    private DatabaseReference mFirebaseDatabaseReference;

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView descTextView;
        public ImageView imageView;

        public MessageViewHolder(View v) {
            super(v);
            titleTextView = (TextView) itemView.findViewById(R.id.textViewTitle);
            descTextView = (TextView) itemView.findViewById(R.id.textViewDesc);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(
                Message.class,
                R.layout.item_message,
                MessageViewHolder.class,
                mFirebaseDatabaseReference.child(MESSAGES_CHILD)) {

            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, Message message, int position) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.titleTextView.setText(message.getText());
                viewHolder.descTextView.setText(message.getDesc());

                Log.d(TAG, "URL: "+message.getImageUrl());
                if (message.getImageUrl() == null) {
                    viewHolder.imageView.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,
                            R.drawable.image_placeholder));
                } else {
                    Log.d(TAG, "URL: "+message.getImageUrl());
                    Glide.with(MainActivity.this)
                            .load(message.getImageUrl())
                            .into(viewHolder.imageView);

                }
            }
        };

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        mEditTextTitle = (EditText) findViewById(R.id.messageEditText);
        mEditTextDesc = (EditText) findViewById(R.id.messageEditDesc);
        mEditTextTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mAddButton.setEnabled(true);
                } else {
                    mAddButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mAddButton = (Button) findViewById(R.id.sendButton);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message(mEditTextTitle.getText().toString(),
                                                                    mEditTextDesc.getText().toString(),
                                                                    DEFAULT_IMAGE_URL);
                mFirebaseDatabaseReference.child(MESSAGES_CHILD).push().setValue(message);
                mEditTextTitle.setText("");
                mEditTextDesc.setText("");
            }
        });
    }
}
