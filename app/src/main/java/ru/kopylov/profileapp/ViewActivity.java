package ru.kopylov.profileapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class ViewActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        TextView profile = findViewById(R.id.profile_text);
        Intent intent = getIntent();
        String fullName = intent.getStringExtra("full_name");

        profile.setText("Hello " + fullName);
    }
}
