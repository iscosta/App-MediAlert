package com.br.medialert;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.br.medialert.dao.PrescricaoDAOHttp;
import com.br.medialert.model.Prescricao;
import com.br.medialert.util.PersistenciaHelper;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class Cadastro extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{


    private EditText txtDateTime;
    private PersistenciaHelper helper;
    private EditText edID;

    int day, month, year, hour, minute;
    int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal;
    int dayNow, monthNow, yearNow, hourNow, minuteNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        helper = new PersistenciaHelper(this);
        edID = (EditText) findViewById(R.id.edID);
        Intent intent = getIntent();

        Prescricao pres = (Prescricao) intent.getSerializableExtra("prescricao");
        if(pres != null){
            helper.preencherEdts(pres);
        }

        //Definindo dia-mes-ano-hora-minutos
        txtDateTime = (EditText) findViewById(R.id.hrInicio);
        Calendar c = new GregorianCalendar(TimeZone.getTimeZone("GMT-3"));
        yearNow = c.get(Calendar.YEAR);
        monthNow = c.get(Calendar.MONTH)+1;
        dayNow = c.get(Calendar.DAY_OF_MONTH);
        hourNow = c.get(Calendar.HOUR);
        minuteNow = c.get(Calendar.MINUTE);

        txtDateTime.setText(dayNow+"/"+monthNow +"/"+ yearNow+"\t"+hourNow+ ":"+minuteNow);

        txtDateTime.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {

                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Cadastro.this, Cadastro.this,
                        year, month, day);
                datePickerDialog.show();
            }
        });
    }


    //Mostrar Calendário e Relógio
    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        yearFinal =i;
        monthFinal = i1+1;
        dayFinal = i2;

        Calendar c= Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minuteFinal = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(Cadastro.this, Cadastro.this,
                hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();

    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {

        hourFinal = i;
        minuteFinal = i1;
        txtDateTime.setText(dayFinal+"/"+monthFinal +"/"+ yearFinal+"\t"+hourFinal+ ":"+minuteFinal);
    }


    public void Inserir (View view){


                PrescricaoDAOHttp prescricaoDAO = new PrescricaoDAOHttp();
                Prescricao pres = new Prescricao();

                //Pega o ID do Edit edID
                String strId = edID.getText().toString().trim();
                pres = helper.pegaPrescricao();
                Log.e("pres", pres.toString());

                //Valida se os campos estão preenchidos
                if (helper.validacaoCampos()) {
                    //Se o ID for Vazio
                    if (TextUtils.isEmpty(strId)) {
                        prescricaoDAO.insert(pres);
                    } else {
                        Toast.makeText(Cadastro.this, "Alerta para " + pres.getMedicamento() + " salvo!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Preencha os campos obrigatórios! ", Toast.LENGTH_LONG).show();
                }
                finish();

        }

    }



