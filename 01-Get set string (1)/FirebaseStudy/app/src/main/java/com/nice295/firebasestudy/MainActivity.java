package com.nice295.firebasestudy;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("string-edittext");

    private TextView mTextEmotion;
    private EditText mEditText;
    private Button mButtonSave;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Views
        mTextEmotion = (TextView) findViewById(R.id.textEmotion);
        mButtonSave = (Button) findViewById(R.id.buttonSave);
        mEditText = (EditText) findViewById(R.id.editText);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Click listeners
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEditText.getText() != null) {
                    myRef.setValue(mEditText.getText().toString());
                    mEditText.clearFocus();
                    mEditText.setText("");
                }
                else {
                    Log.e(TAG, "Edit text is empty.");
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mProgressBar.setVisibility(View.VISIBLE);

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mProgressBar.setVisibility(View.INVISIBLE);

                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
                mTextEmotion.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
}
