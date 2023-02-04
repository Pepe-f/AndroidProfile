package ru.kopylov.profileapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity implements View.OnClickListener, FetchData.AsyncResponse {

    private EditText loginInput;
    private EditText passwordInput;
    private Button submitButton;
    private String login;
    private String password;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginInput = findViewById(R.id.login_input);
        passwordInput = findViewById(R.id.password_input);
        submitButton = findViewById(R.id.submit_button);

        submitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        login = loginInput.getText().toString();
        password = passwordInput.getText().toString();
        url = "http://f0774500.xsph.ru/getUserProfile.php";

        new FetchData(this).execute(login, password, url);
    }

    @Override
    public void processFinish(String output) {
        if (!output.equals("User not found")) {
            Intent intent = new Intent();
            intent.putExtra(MainActivity.USER_FULL_NAME, output);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }
    }
}
