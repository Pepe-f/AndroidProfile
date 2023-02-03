package ru.kopylov.profileapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView profile;
    private SharedPreferences preferences;
    public static String PREF = "PROFILEAPP_PREFERENCES_FILE";
    public final static String USER_FULL_NAME = "userfullname";
    private int code = 0;
    private Toast toast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profile = findViewById(R.id.profile_text);

        String userFullName;
        if ((userFullName = loadUserFullNameFromPref()) == null) {
            getUserFullName();
        } else {
            profile.setText("Hello " + userFullName);
        }
    }

    private String loadUserFullNameFromPref() {
        preferences = getApplicationContext().getSharedPreferences(PREF, MODE_PRIVATE);
        String userFullName = preferences.getString(USER_FULL_NAME, "");
        if (userFullName.isEmpty()) {
            return null;
        }

        return userFullName;
    }

    private void getUserFullName() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intent, code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == code) {
            if (resultCode == RESULT_OK) {
                String userFullName = data.getStringExtra(USER_FULL_NAME);
                saveUserFullName(userFullName);
                profile.setText("Hello " + userFullName);
            } else {
                if (toast != null) {
                    toast.cancel();
                }

                toast = Toast.makeText(this, "This user not found", Toast.LENGTH_SHORT);
                toast.show();
                getUserFullName();
            }
        }
    }

    private void saveUserFullName(String userFullName) {
        preferences = getApplicationContext().getSharedPreferences(PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_FULL_NAME, userFullName);
        editor.commit();
    }
}