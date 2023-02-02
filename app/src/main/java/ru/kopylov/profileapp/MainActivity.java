package ru.kopylov.profileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, FetchData.AsyncResponse {

    private EditText loginInput;
    private EditText passwordInput;
    private Button submitButton;
    private String login;
    private String password;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginInput = findViewById(R.id.login_input);
        passwordInput = findViewById(R.id.password_input);
        submitButton = findViewById(R.id.submit_button);

        submitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        login = loginInput.getText().toString();
        password = loginInput.getText().toString();
        url = "http://profilejava.loc/getUserProfile.php";

        new FetchData(this).execute(login, password, url);
    }

    @Override
    public void processFinish(String output) {
        Intent intent = new Intent(this, ViewActivity.class);
        intent.putExtra("full_name", output);
        startActivity(intent);
    }
}