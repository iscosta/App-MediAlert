package com.br.medialert.model;

import java.io.Serializable;

/**
 * Created by Igor Costa on 08/10/2016.
 */
public class Prescricao implements Serializable {

    private Integer id;
    private String qtdDias;
    private String medicamento;
    private int _id;
    private String _rev;
    private String devCode;
    private String dosagem;
    private String horaInicio;
    private String intervalo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQtdDias() {
        return qtdDias;
    }

    public void setQtdDias(String qtdDias) {
        this.qtdDias = qtdDias;
    }

    public String getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(String medicamento) {
        this.medicamento = medicamento;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_rev() {
        return _rev;
    }

    public void set_rev(String _rev) {
        this._rev = _rev;
    }

    public String getDevCode() {
        return devCode;
    }

    public void setDevCode(String devCode) {
        this.devCode = devCode;
    }

    public String getDosagem() {
        return dosagem;
    }

    public void setDosagem(String dosagem) {
        this.dosagem = dosagem;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getIntervalo() {
        return intervalo;
    }

    public void setIntervalo(String intervalo) {
        this.intervalo = intervalo;
    }

    @Override
    public String toString() {
        return "Prescricao{" +
                "id=" + id +
                ", qtdDias=" + qtdDias +
                ", medicamento='" + medicamento + '\'' +
                ", _id='" + _id + '\'' +
                ", _rev='" + _rev + '\'' +
                ", devCode='" + devCode + '\'' +
                ", dosagem='" + dosagem + '\'' +
                ", horaInicio='" + horaInicio + '\'' +
                ", intervalo='" + intervalo + '\'' +
                '}';
    }

}
