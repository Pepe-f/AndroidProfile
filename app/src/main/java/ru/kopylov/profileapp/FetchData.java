package ru.kopylov.profileapp;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class FetchData extends AsyncTask<String, Void, String> {
    
    public interface AsyncResponse {
        void processFinish(String output);
    }

    public AsyncResponse delegate;

    public FetchData(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String login = strings[0];
            String password = strings[1];
            String urlLink = strings[2];

            String data = URLEncoder.encode("login", "UTF-8") + "=" + URLEncoder.encode(login, "UTF-8");
            data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
            
            URL url = new URL(urlLink);
            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            OutputStreamWriter streamWriter = new OutputStreamWriter(connection.getOutputStream());

            streamWriter.write(data);
            streamWriter.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                break;
            }

            return stringBuilder.toString();
        } catch (Exception e) {
            return "Exception: " + e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null && !result.equals("")) {
            delegate.processFinish(result);
        }
    }
}
