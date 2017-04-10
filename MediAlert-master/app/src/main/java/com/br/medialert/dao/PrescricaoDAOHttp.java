package com.br.medialert.dao;

import android.util.Log;

import com.br.medialert.model.Prescricao;
import com.br.medialert.util.HttpCloudant;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Igor Costa on 08/10/2016.
 */
public class PrescricaoDAOHttp {

    private final String URL_POST = "https://e6bc6c13-c42a-46d3-a2b9-6a25809f988d-bluemix.cloudant.com/fiap-prescricao/" ;
    private final String URL_POST_FIND = "https://e6bc6c13-c42a-46d3-a2b9-6a25809f988d-bluemix.cloudant.com/fiap-prescricao/_find" ;
    private final String URL_GET = "https://e6bc6c13-c42a-46d3-a2b9-6a25809f988d-bluemix.cloudant.com/fiap-prescricao/_all_docs?include_docs=true" ;

    HttpCloudant httpCloudant = new HttpCloudant();

    //Método para inserir o Prescricao na Base
    public void insert(Prescricao prescricao){
        Gson gson = new Gson();
        prescricao.setDevCode("1");
        prescricao.setId(0);
        String json = gson.toJson(prescricao);
        HttpCloudant httpCloudant = new HttpCloudant();
        try {
            String jsonResult = httpCloudant.execute(URL_POST, "POST",json).get();
            Log.e("res", jsonResult);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //Método que atualiza o Prescricao na Base
    public void update(Prescricao prescricao){
        Gson gson = new Gson();
        String json = gson.toJson(prescricao);
        HttpCloudant httpCloudant = new HttpCloudant();

        try{
            String jsonResult = httpCloudant.execute(URL_POST,"POST", json).get();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Método que Exclui o Prescricao na Base
    public void delete(Prescricao prescricao){
        HttpCloudant httpCloudant = new HttpCloudant();
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(prescricao);
        jsonElement.getAsJsonObject().addProperty("_deleted",true);
        String json = gson.toJson(jsonElement);
        try {
            String jsonResult = httpCloudant.execute(URL_POST, "POST", json).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Método que carrega o listAlertas
    public List<Prescricao> buscaAlertas() throws JSONException {
        String jsonFilter = "{\"selector\": { \"devCode\": \"1\" }, \"limit\": 10 }";
        HttpCloudant httpCloudant = new HttpCloudant();
        List<Prescricao> prescricaos = new ArrayList<>();
        try{
            String jsonResult = httpCloudant.execute(URL_POST_FIND, "POST", jsonFilter).get();

            // Exibe os dados retornados pela requisicao POST
            JSONObject jsonSaida = new JSONObject(jsonResult);
            JSONArray jsonArray = (JSONArray) jsonSaida.get("docs");
            Gson gson = new Gson();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                // Populando o ArrayList
                prescricaos.add(gson.fromJson(jsonObj.toString(), Prescricao.class));
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e("Erro", e.getMessage() + e.getCause());
        } catch (ExecutionException e) {
            e.printStackTrace();
            Log.e("Erro", e.getMessage() + e.getCause());
        }
        return prescricaos;
    }
}

