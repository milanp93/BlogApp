package com.example.milan.proba;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private AsyncTask<Void,Void,Integer> login;
    private EditText emailText;
    private EditText passwordText;
    private Button loginButton;
    private String email;
    private String password;
    private ProgressDialog progressDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedpreferences = MainActivity.this.getSharedPreferences("com.example.milan.proba", Context.MODE_PRIVATE);
        if(sharedpreferences.getString("token",null)!=null){
            Intent intent = new Intent(getApplicationContext(), BlogList.class);
            startActivity(intent, null);
            finish();
        }else {

            emailText = (EditText) findViewById(R.id.input_email);
            passwordText = (EditText) findViewById(R.id.input_password);
            loginButton = (Button) findViewById(R.id.btn_login);
            initializeLogin();

            loginButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    login();
                }
            });
        }
    }

    private void login(){
        if(!validate())
            return;

        if(!NetworkCheck.isNetworkAvailable(this)){
            Toast.makeText(getBaseContext(),"No internet conection",Toast.LENGTH_LONG).show();
            return;
        }
        loginButton.setEnabled(false);

        progressDialog = new ProgressDialog(MainActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        email = emailText.getText().toString();
        password = passwordText.getText().toString();

        login.execute();

    }

    private boolean validate() {
        boolean valid = true;

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("Email should be in valid format.");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 20) {
            passwordText.setError("Password should be at least 6 characters long.");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }

    private void initializeLogin(){
        login = new AsyncTask<Void,Void,Integer>(){
            protected Integer doInBackground(Void... params) {
                String url="http://blogsdemo.creitiveapps.com:16427/login";
                URL object= null;
                try {
                    object = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) object.openConnection();
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("Accept", "application/json");
                    con.setRequestMethod("POST");

                    JSONObject cred   = new JSONObject();

                    cred.put("email",email);//"candidate@creitive.com");
                    cred.put("password", password);//"1234567");

                    OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                    wr.write(cred.toString());
                    wr.flush();

                    //display what returns the POST request

                    StringBuilder sb = new StringBuilder();
                    int HttpResult = con.getResponseCode();
                    if (HttpResult == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(
                                new InputStreamReader(con.getInputStream(), "utf-8"));
                        String line = null;
                        while ((line = br.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        br.close();
                        JSONObject json = new JSONObject(sb.toString());
                        SharedPreferences sharedpreferences = MainActivity.this.getSharedPreferences("com.example.milan.proba", Context.MODE_PRIVATE);
                        sharedpreferences.edit().putString("token",json.getString("token")).commit();

                        Log.d("MP",sb.toString());
                        Log.d("MP",sharedpreferences.getString("token",null));
                        return 1;
                    } else {
                        progressDialog.dismiss();
                        Log.d("MP",con.getResponseMessage());
                        Log.d("MP",HttpResult+"");
                        if(HttpResult == 400 || HttpResult == 401)
                            return 2;
                        else
                            return 3;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 3;
            }

            @Override
            protected void onPostExecute(Integer success) {
                if(success==1){
                    Intent intent = new Intent(getApplicationContext(), BlogList.class);
                    startActivity(intent, null);
                    finish();
                }else {
                    if(success==2)
                        Toast.makeText(MainActivity.this,"You didnt write correct email and password",Toast.LENGTH_LONG).show();
                    if(success==3)
                        Toast.makeText(MainActivity.this,"Some error happen, please try again later",Toast.LENGTH_LONG).show();
                    initializeLogin();
                    loginButton.setEnabled(true);
                }
            }
        };
    }
}
