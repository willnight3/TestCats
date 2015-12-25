package com.testsut.testcatnew;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import java.net.HttpURLConnection;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.URL;

/**
 * Created by Mitrofanova on 23.12.2015.
 */
public class MainActivity extends ActionBarActivity {

    //private TextView textView;
    private static final String TAG = "MyApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button getData = (Button) findViewById(R.id.getservicedata);
        getData.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v){
                String restURL = "http://www.androidexample.com/media/webservice/JsonReturn.php";
                new RestOperation().execute(restURL);

            }
        });

    }

    private class RestOperation extends AsyncTask<String, Void, Void> {

        final HttpClient httpClient = new DefaultHttpClient();
        String content;
        //= "{\"Android\":[{\"name\":\"1\",\"number\":\"1\",\"date_added\":\"1\"},{\"name\":\"2\",\"number\":\"2\",\"date_added\":\"2\"},{\"name\":\"3\",\"number\":\"3\",\"date_added\":\"3\"}]}";
        String error;
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        String data;
        TextView serverDataReceived = (TextView) findViewById(R.id.serverDataReceived);
        TextView showParsedJSON = (TextView) findViewById(R.id.showParsedJSON);
        EditText userinput = (EditText) findViewById(R.id.userinput);


        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            progressDialog.setTitle("Please wait...");
            progressDialog.show();

            try {
                data += "&" + URLEncoder.encode("data","UTF-8") + "-" + userinput.getText();
                Log.i(TAG,data);
            } catch (UnsupportedEncodingException e){
                error = e.getMessage();
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(String... params){
            BufferedReader br = null;

            URL url;
            Log.i(TAG,"log1");

            try{
                url  = new URL(params[0]);

                URLConnection connection = url.openConnection();
                connection.setDoOutput(true);

                OutputStreamWriter outputStreamWr = new OutputStreamWriter(connection.getOutputStream());
                outputStreamWr.write(data);
                outputStreamWr.flush();

                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = br.readLine())!=null){
                    sb.append(line);
                    sb.append(System.getProperty("line.separator"));
                }
                content = sb.toString();

            } catch (MalformedURLException e){
                error = e.getMessage();
                e.printStackTrace();
            } catch (IOException e){
                error = e.getMessage();
                e.printStackTrace();
            } finally {
                try {
                    br.close();
                } catch (IOException e){
                    error = e.getMessage();
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            Log.i(TAG, "log2");
            progressDialog.dismiss();

            if(error!=null){
                serverDataReceived.setText("Error" + error);
                Log.i(TAG,"LogY");
            } else {
                serverDataReceived.setText(content);

                String output = "";
               // JSONObject jsonResponse;
                Log.i(TAG,"log3");
                Log.i(TAG,content);
                try{

                    JSONObject jsonResponse = new JSONObject(content);

                    JSONArray jsonArray = new JSONArray();

                    //JSONArray jsonArray = null;
                    jsonArray = jsonResponse.optJSONArray("Android");

                    Log.i(TAG,"LogJSON");
                    //output = jsonArray.length();

                   for (int i = 0; i < jsonArray.length(); i++){
                        JSONObject child = jsonArray.getJSONObject(i);
                        Log.i(TAG,"log" + i);
                        String name = child.getString("name");
                        String number = child.getString("number");
                        String time = child.getString("date_added");

                        output = "Name" + name + System.getProperty("line.separator") + number + System.getProperty("line.separator") + time;
                        output += System.getProperty("line.separator");

                    }

                    showParsedJSON.setText(output);

                } catch (JSONException e){
                    e.printStackTrace();
                    Log.i(TAG, "log5");
                } catch (NullPointerException e){
                    e.printStackTrace();
                    Log.i(TAG, "log6");
                }





            }

        }
    }

}
