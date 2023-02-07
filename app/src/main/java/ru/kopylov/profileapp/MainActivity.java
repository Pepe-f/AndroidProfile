package ru.kopylov.profileapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Date;

import ru.kopylov.profileapp.utils.ImageUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private TextView profile;
    private SharedPreferences preferences;
    public static String PREF = "PROFILEAPP_PREFERENCES_FILE";
    public static final String USER_FULL_NAME = "userfullname";
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 2;
    private int code = 0;
    private Toast toast;
    private Button sendInfoButton;
    private Button getContactButton;
    private ImageButton selfImageButton;
    private ImageView selfImageView;
    private Intent getContactIntent;
    private Intent getImageIntent;
    private File selfImageFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profile = findViewById(R.id.profile_text);
        sendInfoButton = findViewById(R.id.send_info_button);

        String userFullName;
        if ((userFullName = loadUserFullNameFromPref()) == null) {
            getUserFullName();
        } else {
            profile.setText("Hello " + userFullName);
            sendInfoButton.setOnClickListener(this);

            getContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            PackageManager packageManager = this.getPackageManager();
            if (packageManager.resolveActivity(getContactIntent, packageManager.MATCH_DEFAULT_ONLY) == null) {
                getContactButton.setEnabled(false);
            } else {
                getContactButton = findViewById(R.id.get_contact_button);
                getContactButton.setOnClickListener(this);
            }

            selfImageView = findViewById(R.id.self_image_view);
            selfImageButton = findViewById(R.id.self_image_button);
            getImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            String imageFileName = getImageFileName();
            selfImageFile = getImageFile(imageFileName);
            boolean canTakeImage = selfImageFile != null && getImageIntent.resolveActivity(packageManager) != null;
            selfImageButton.setEnabled(canTakeImage);

            if (canTakeImage) {
                Uri uri = FileProvider.getUriForFile(this, "ru.kopylov.profileapp.fileprovider", selfImageFile);
                getImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            }

            selfImageButton.setOnClickListener(this);
        }
    }

    private String getImageFileName() {
        Date date = new Date();

        return "IMG+" + "20230702" + ".jpg";
    }

    private File getImageFile(String fileName) {
        File fileDir = this.getFilesDir();

        if (fileDir == null) {
            return null;
        }

        return new File(fileDir, fileName);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == sendInfoButton.getId()) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "I have get up at 5:40!");
            intent.putExtra(Intent.EXTRA_SUBJECT, "early report");

            Intent intentChooser = Intent.createChooser(intent, "SendReport");
            startActivity(intentChooser);
        }

        if (view.getId() == getContactButton.getId()) {
            startActivityForResult(getContactIntent, REQUEST_CONTACT);
        }

        if (view.getId() == selfImageButton.getId()) {
            startActivityForResult(getImageIntent, REQUEST_PHOTO);
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

    private void updateSelfImageView(File imageFile) {
        if (imageFile == null || !imageFile.exists()) {
            selfImageView.setImageDrawable(null);
        } else {
            Point size = new Point();
            this.getWindowManager().getDefaultDisplay().getSize(size);
            Bitmap bitmap = ImageUtils.getScalesBitmap(imageFile.getPath(), size.x, size.y);
            selfImageView.setImageBitmap(bitmap);
        }
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

        if (requestCode == REQUEST_CONTACT) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri contactUri = data.getData();
                    String[] queryFields = new String[]{ContactsContract.Contacts.DISPLAY_NAME};

                    Cursor cursor = this.getContentResolver().query(contactUri, queryFields, null, null, null);
                    try {
                        if (cursor.getCount() == 0) {
                            return;
                        }
                        cursor.moveToFirst();
                        String name = cursor.getString(0);
                        Log.d(TAG, "onActivityResult: name of contact = " + name);
                    } finally {
                        cursor.close();
                    }
                }
            }
        }

        if (requestCode == REQUEST_PHOTO) {
            if (resultCode == RESULT_OK) {
                updateSelfImageView(selfImageFile);
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