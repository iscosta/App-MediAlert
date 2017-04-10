package com.br.medialert;

        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.content.Intent;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.ListView;
        import android.bluetooth.BluetoothAdapter;
        import android.bluetooth.BluetoothDevice;
        import android.widget.TextView;
        import android.widget.Toast;

        import java.util.ArrayList;
        import java.util.Set;


public class ListaDispositivos extends AppCompatActivity
{

    Button btbPareado;
    ListView listaDeDispositivos;
    private BluetoothAdapter meuBlootooth = null;
    private Set<BluetoothDevice> dispsositivosPareados;
    public static String EXTRA_ADDRESS = "device_address";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_dispositivos);


        btbPareado = (Button)findViewById(R.id.button);
        listaDeDispositivos = (ListView)findViewById(R.id.listView);


        meuBlootooth = BluetoothAdapter.getDefaultAdapter();

        if(meuBlootooth == null)
        {

            Toast.makeText(getApplicationContext(), "Bluetooth não encontrado.", Toast.LENGTH_LONG).show();

            finish();
        }
        else if(!meuBlootooth.isEnabled())
        {

            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon,1);
        }
        btbPareado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listaDispositivosPareados();
            }
        });

    }

    private void listaDispositivosPareados()
    {
        dispsositivosPareados = meuBlootooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if (dispsositivosPareados.size()>0)
        {
            for(BluetoothDevice bt : dispsositivosPareados)
            {
                list.add(bt.getName() + "\n" + bt.getAddress());
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Nenhum dispositivo Bluetooth foi encontrado.", Toast.LENGTH_LONG).show();
        }

        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        listaDeDispositivos.setAdapter(adapter);
        listaDeDispositivos.setOnItemClickListener(meuDispositivoSelecionado);

    }

    private AdapterView.OnItemClickListener meuDispositivoSelecionado = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3)
        {

            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);


            Intent intentControle = new Intent(ListaDispositivos.this, Controle.class);


            intentControle.putExtra(EXTRA_ADDRESS, address);

            startActivity(intentControle);
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        getMenuInflater().inflate(R.menu.menu_lista_dispositivos, menu);
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
}
