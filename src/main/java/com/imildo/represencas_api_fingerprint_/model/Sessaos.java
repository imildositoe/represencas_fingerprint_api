package com.imildo.represencas_api_fingerprint_.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Time;

/**
 * @author Imildo Sitoe
 *
 * */
@Entity
public class Sessaos {

    @Id
    private Integer id_sessao;
    private Time hora_inicio;
    private Time hora_fim;
    private boolean is_selada;
    private boolean is_aberta;
    private int id_aula;

    public Sessaos() {
        super();
    }

    public Integer getId_sessao() {
        return id_sessao;
    }

    public void setId_sessao(Integer id_sessao) {
        this.id_sessao = id_sessao;
    }

    public Time getHora_inicio() {
        return hora_inicio;
    }

    public void setHora_inicio(Time hora_inicio) {
        this.hora_inicio = hora_inicio;
    }

    public Time getHora_fim() {
        return hora_fim;
    }

    public void setHora_fim(Time hora_fim) {
        this.hora_fim = hora_fim;
    }

    public boolean isIs_selada() {
        return is_selada;
    }

    public void setIs_selada(boolean is_selada) {
        this.is_selada = is_selada;
    }

    public boolean isIs_aberta() {
        return is_aberta;
    }

    public void setIs_aberta(boolean is_aberta) {
        this.is_aberta = is_aberta;
    }

    public int getId_aula() {
        return id_aula;
    }

    public void setId_aula(int id_aula) {
        this.id_aula = id_aula;
    }

    @Override
    public String toString() {
        return "Sessaos{" +
                "id_sessao=" + id_sessao +
                ", hora_inicio=" + hora_inicio +
                ", hora_fim=" + hora_fim +
                ", is_selada=" + is_selada +
                ", is_aberta=" + is_aberta +
                ", id_aula=" + id_aula +
                '}';
    }
}
