package com.br.medialert.util;


import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Igor Costa on 08/10/2016.
 */
public class HttpCloudant extends AsyncTask<String, Void, String> {


    private final String userName = "----------" ;
    private final String password =  "---------------" ;


    @Override
    protected String doInBackground(String... urls) {

        HttpURLConnection client;
        String retorno = "";

        try {
            URL url = new URL(urls[0]);
            client = (HttpURLConnection) url.openConnection();

            client.setRequestProperty("Content-Type", "application/json");
            client.setRequestProperty("charset", "utf-8");
            client.setRequestMethod(urls[1]);
            client.setDoOutput(true);
            client.setDoInput(true);

            String encodedPassword = userName + ":" + password;
            String encoded = Base64.encodeToString(encodedPassword.getBytes(), Base64.NO_WRAP);
            client.setRequestProperty("Authorization", "Basic " + encoded);
            OutputStreamWriter wr;

            wr = new OutputStreamWriter(client.getOutputStream());
            wr.write(urls[2]);
            Log.e("Filtro",urls[2]);
            wr.close();
            StringBuilder sb = new StringBuilder();
            int statusCodeHTTP = client.getResponseCode();

            if (statusCodeHTTP == HttpURLConnection.HTTP_OK) {

                BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();

                retorno = sb.toString();

            } else {
                Log.e("Erro GET ALL ", "Erro na Requisição! Código do Erro: " + statusCodeHTTP + "\nMensagem de Erro: "
                        + client.getResponseMessage());
            }

        }catch (Exception e){
            Log.e("erro", e.getMessage() + e.getCause() );
        }
        return retorno;
    }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {

    }
}
