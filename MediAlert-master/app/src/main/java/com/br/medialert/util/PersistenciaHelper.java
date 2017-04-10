package com.br.medialert.util;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Spinner;

import com.br.medialert.Cadastro;
import com.br.medialert.MainActivity;
import com.br.medialert.R;
import com.br.medialert.model.Prescricao;

/**
 * Igor Costa on 06/10/2016.
 */
public class PersistenciaHelper {

    private Prescricao prescricao;
    private EditText edMedicamento, edDosagem, edHoraInicio, edQtdDias, edID;
    private Spinner spIntervalos;

    public PersistenciaHelper(Cadastro activity){
        edMedicamento = (EditText) activity.findViewById(R.id.edNomeMedicamento);
        edDosagem = (EditText) activity.findViewById(R.id.edDosagem);
        edHoraInicio = (EditText) activity.findViewById(R.id.hrInicio);
        spIntervalos = (Spinner) activity.findViewById(R.id.spinnerIntervalo);
        edQtdDias = (EditText) activity.findViewById(R.id.edQtdDia);
        edID = (EditText) activity.findViewById(R.id.edID);
        prescricao = new Prescricao();
    }

    //Método que pega os valores dos EditTexts e retorna um objeto Prescricao
    public Prescricao pegaPrescricao(){
        if (edID.getText().length()>0)
            prescricao.set_id(edID.getId());

        prescricao.setMedicamento(edMedicamento.getText().toString());
        prescricao.setDosagem(edDosagem.getText().toString());
        prescricao.setHoraInicio(edHoraInicio.getText().toString());
        prescricao.setIntervalo(spIntervalos.toString());
        prescricao.setQtdDias(edQtdDias.toString());
        return prescricao;
    }

    //Método que preenche o Edits com os valores de um objeto Prescricao
    public void preencherEdts(Prescricao pres){
        if (pres != null){
            edMedicamento.setText((CharSequence) pres.getMedicamento());
            edDosagem.setText(pres.getDosagem());
            edHoraInicio.setText((CharSequence) pres.getHoraInicio());
            edQtdDias.setText(pres.getQtdDias());
        }
    }


    //Método que válida se os EditTexts estão preenchidos
    public boolean validacaoCampos(){
        String nome = edMedicamento.getText().toString().trim();
        String dosagem = edDosagem.getText().toString().trim();
        String dataInicio = edHoraInicio.getText().toString().trim();
        String qtddias = edQtdDias.getText().toString().trim();
        return (   !TextUtils.isEmpty(nome)
                && !TextUtils.isEmpty(dosagem)
                && !TextUtils.isEmpty(dataInicio)
                && !TextUtils.isEmpty(qtddias));
    }
}
