package com.br.medialert;

        import android.app.ProgressDialog;
        import android.bluetooth.BluetoothAdapter;
        import android.bluetooth.BluetoothDevice;
        import android.bluetooth.BluetoothSocket;
        import android.content.Intent;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.os.Handler;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Base64;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.widget.CompoundButton;
        import android.widget.EditText;
        import android.widget.SeekBar;
        import android.widget.Switch;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.gson.Gson;

        import org.json.JSONObject;
        import java.io.BufferedInputStream;
        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.io.OutputStreamWriter;
        import java.net.HttpURLConnection;
        import java.net.URL;
        import java.text.SimpleDateFormat;
        import java.util.Iterator;
        import java.util.List;
        import java.util.UUID;

        import com.br.medialert.model.Captura;
        import com.br.medialert.dao.CapturaDAO;



public class Controle extends AppCompatActivity {

    private Switch swLed, swPorta, swCon, swWS;
    private SeekBar brilho;
    private TextView luminosidade;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter meuBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean btConectado = false;

    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    boolean paraAThread;
    byte buffer[];
    private InputStream inputStream;
    EditText textView;
    JSONObject objJSON;
    StringBuffer strBuffer, strBufferView;
    int contLer= 0;


    private boolean enviarWS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent newInt = getIntent();
        address = newInt.getStringExtra(ListaDispositivos.EXTRA_ADDRESS);


        setContentView(R.layout.activity_controle);


        brilho = (SeekBar) findViewById(R.id.seekBar);
        luminosidade = (TextView) findViewById(R.id.lum);
        swLed = (Switch) findViewById(R.id.switchLED);
        swPorta = (Switch) findViewById(R.id.switchPorta);
        swCon = (Switch) findViewById(R.id.switchCon);
        swWS = (Switch) findViewById(R.id.switchWS);
        textView = (EditText) findViewById(R.id.editText);
        swLed.setEnabled(false);
        swPorta.setEnabled(false);
        swWS.setEnabled(false);
        brilho.setEnabled(false);



        brilho.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                luminosidade.setText(String.valueOf(progress)+":");
                try {

                    btSocket.getOutputStream().write(luminosidade.getText().toString().getBytes());
                } catch (IOException e) {
                    Log.e("Erro SeekBar: ", e.getMessage() );
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        swCon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {
                    new ConectaBT().execute();
                    swLed.setEnabled(true);
                    swPorta.setEnabled(true);
                    swWS.setEnabled(true);
                } else {
                    swLed.setChecked(false);
                    swPorta.setChecked(false);
                    swWS.setChecked(false);
                    swLed.setEnabled(false);
                    swPorta.setEnabled(false);
                    swWS.setEnabled(false);
                    desligarLED();
                    Desconectar();
                }

            }
        });

        swLed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    ligarLED();
                } else {
                    desligarLED();
                }

            }
        });

        swPorta.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {
                    escutaDadosDoBluetooth();
                } else {
                    paraAThread = true;
                    textView.setText("");
                }

            }
        });

        swWS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {
                    enviarWS = true;
                } else {
                    enviarWS = false;
                }

            }
        });
    }

    @Override
    protected void onDestroy()
    {

        desligarLED();
        Desconectar();
        super.onDestroy();
    }

    private void Desconectar() {
        if (btSocket != null)
        {
            try {
                btConectado = false;
                btSocket.close();
                paraAThread = true;
                enviarWS = false;
                if(inputStream != null) inputStream.close();
                msg("Conexão Finalizada.");
            } catch (IOException e) {
                msg("Erro ao Desconectar.");
            }
        }
    }

    private void desligarLED() {
        if (btSocket != null) {
            try {

                luminosidade.setText("0:");
                brilho.setProgress(0);
                brilho.setEnabled(false);
                btSocket.getOutputStream().write("TF".toString().getBytes());
            } catch (IOException e) {
                Log.e("Erro ao desligar LED", e.getMessage());
            }
        }
    }

    private void ligarLED() {
        if (btSocket != null) {
            try {
                brilho.setProgress(0);
                brilho.setEnabled(true);
                btSocket.getOutputStream().write("TO".toString().getBytes());
            } catch (IOException e) {
                Log.e("Erro ao ligar LED", e.getMessage() );
            }
        }
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_controle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void escutaDadosDoBluetooth() {

        final Handler handler = new Handler();
        paraAThread = false;
        buffer = new byte[1024];

        strBuffer = new StringBuffer();
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss:SSS");

        Thread thread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !paraAThread) {
                    try {
                        inputStream = btSocket.getInputStream();
                        int byteCount = inputStream.available();

                        if (byteCount > 0) {
                            byte[] rawBytes = new byte[byteCount];
                            inputStream.read(rawBytes);
                            strBuffer.append(new String(rawBytes, "UTF-8"));
                            String timestamp = formatter.format(new java.util.Date());

                            String[] lines = strBuffer.toString().split("\r\n");

                            if (lines.length > 2) {
                                try {
                                    objJSON = new JSONObject(lines[lines.length - 2]);
                                    objJSON.put("timestamp", timestamp);
                                    strBufferView = new StringBuffer(objJSON.toString());
                                    objJSON = null;
                                } catch (Throwable t) {
                                    strBufferView = new StringBuffer("Não foi possível o parse no JSON malformado.");
                                    Log.e("JSON", "Não foi possível o parse no JSON malformado: \"" + lines[lines.length - 2] + "\"");

                                }
                            }


                            handler.post(new Runnable() {
                                public void run() {
                                    textView.setText("JSON Recebido - Arduino:" + "\n" + " " + strBufferView.toString() + "\n");
                                    if(enviarWS)
                                        new gravarNoBluemix().execute();
                                }
                            });

                        }
                    } catch (Exception ex) {
                        paraAThread = true;
                        Log.e("Erro - Thread While", ex.getMessage());
                    }
                }
            }
        });
        try {
            thread.start();
        }catch (Exception e){
            Log.e("Erro - Thread Start", e.getMessage());
        }
    }

    private class ConectaBT extends AsyncTask<Void, Void, Void>
    {
        private boolean conectado = true;


        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(Controle.this, "Conectando...", "Por favor, aguarde!!!");
        }

        @Override
        protected Void doInBackground(Void... devices)
        {
            try {
                if (btSocket == null || !btConectado) {
                    meuBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = meuBluetooth.getRemoteDevice(address);
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    meuBluetooth.cancelDiscovery();

                    btSocket.connect();

                }
            } catch (IOException e) {
                conectado = false;
                Log.e("Conexão ", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);

            if (!conectado) {
                msg("Falha na Conexão. É uma conexão SPP Bluetooth? Tente novamente.");
                finish();
            } else {
                msg("Conectado!");
                btConectado = true;
            }
            progress.dismiss();
        }

    }



    private Captura carregaCapturaDoJSON(String jsonString) {
        Gson gson = new Gson();
        Captura captura = gson.fromJson(jsonString, Captura.class);
        return captura;
    }



    private void insereCapturaNaoSincronizada(String jsonString){
        try {
            Captura cap = carregaCapturaDoJSON(jsonString);
            CapturaDAO dao = new CapturaDAO(this);
            dao.insere(cap);
            dao.close();
        }catch (Exception e){
            Log.e("Insert", e.getMessage());
        }
    }

    private List<Captura> leCapturasNaoSincronizadas(){
        CapturaDAO dao = new CapturaDAO(this);
        List<Captura> list =  dao.buscaCapturas();
        dao.close();
        return list;
    }

    private void imprimeCapturaNaoSicronizadas(List<Captura> capturas){
        for (Captura cap : capturas) {
            Log.d("Capturas: ", cap.toString());
        }
    }


    private class gravarNoBluemix extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {

            String request  = "https://e6bc6c13-c42a-46d3-a2b9-6a25809f988d-bluemix.cloudant.com/fiap-remedio/";

            try {

                URL url = new URL(request);

                HttpURLConnection client = (HttpURLConnection) url.openConnection();
                String json = strBufferView.toString();

                client.setRequestProperty("content-Type", "application/json");
                client.setRequestProperty("charset", "utf-8");
                client.setRequestMethod("POST");
                client.setDoOutput(true);
                client.setDoInput(true);

                String username = "kersoldreverstandoweemen";
                String password = "fb40ed1c9ea3394c86d3aeb2182323410dafadd1";

                String encodedPassword = username + ":" + password;
                String encoded = Base64.encodeToString(encodedPassword.getBytes(), Base64.NO_WRAP);

                client.setRequestProperty("Authorization", "Basic " + encoded);

                OutputStreamWriter wr = new OutputStreamWriter(client.getOutputStream());
                wr.write(json.toString());
                wr.close();


                StringBuilder sb = new StringBuilder();

                int statusCodeHTTP = client.getResponseCode();

                if ((statusCodeHTTP == HttpURLConnection.HTTP_OK) || (statusCodeHTTP == HttpURLConnection.HTTP_CREATED))  {


                    BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream(), "utf-8"));
                    String line = null;

                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    Log.d("Retorno do POST: " , sb.toString());
                } else {

                    Log.e("Erro de envio bluemix: ", statusCodeHTTP + "\nMsg: " + client.getResponseMessage());
                    throw new RuntimeException(client.getResponseMessage());
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Erro no doInBackground ", e.getMessage());

            }
            return null;
        }
    }

    private class lerDoBluemix extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {

            String request = "https://e6bc6c13-c42a-46d3-a2b9-6a25809f988d-bluemix.cloudant.com/fiap-remedio/_find";
            try {

                URL url = new URL(request);

                HttpURLConnection client = (HttpURLConnection) url.openConnection();

                String json = "{\"selector\": { \"devCode\": 1 } }";



                client.setRequestProperty("content-Type", "application/json");
                client.setRequestProperty("Accept-Charset", "UTF-8");
                client.setRequestMethod("GET");
                client.setDoOutput(true);
                client.setDoInput(true);

                String username = "kersoldreverstandoweemen";
                String password = "fb40ed1c9ea3394c86d3aeb2182323410dafadd1";


                String encodedPassword = username + ":" + password;
                String encoded = Base64.encodeToString(encodedPassword.getBytes(), Base64.NO_WRAP);
                client.setRequestProperty("Authorization", "Basic " + encoded);


                OutputStreamWriter wr = new OutputStreamWriter(client.getOutputStream());
                wr.write(json.toString());
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



                } else {
                    BufferedInputStream in = new BufferedInputStream(client.getErrorStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        Log.e("Erro", line);
                    }
                    Log.e("Código do Erro: " , statusCodeHTTP + "\nMensagem de Erro: "
                            + client.getResponseMessage().toString() + "\n" + client.getErrorStream().toString());
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Erro ", e.getMessage());
            }
            return null;
        }
    }
}
