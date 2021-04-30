package com.imildo.represencas_api_fingerprint_.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.Date;

/**
 * @author Imildo Sitoe
 *
 * */
@Entity
public class Estudantes {

    @Id
    private Integer id_estudante;
    private String apelido;
    private String nome;
    private String nr_estudante;
    private String url_foto;
    private String email;
    private String senha;
    private String genero;
    private Date data_nascimento;
    private int id_curso;
    private byte[] finger_template;
    private String is_finger_registered;


    public Estudantes() {
        super();
    }

    public Integer getId_estudante() {
        return id_estudante;
    }

    public void setId_estudante(Integer id_estudante) {
        this.id_estudante = id_estudante;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNr_estudante() {
        return nr_estudante;
    }

    public void setNr_estudante(String nr_estudante) {
        this.nr_estudante = nr_estudante;
    }

    public String getUrl_foto() {
        return url_foto;
    }

    public void setUrl_foto(String url_foto) {
        this.url_foto = url_foto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Date getData_nascimento() {
        return data_nascimento;
    }

    public void setData_nascimento(Date data_nascimento) {
        this.data_nascimento = data_nascimento;
    }

    public int getId_curso() {
        return id_curso;
    }

    public void setId_curso(int id_curso) {
        this.id_curso = id_curso;
    }

    public byte[] getFinger_template() {
        return finger_template;
    }

    public void setFinger_template(byte[] finger_template) {
        this.finger_template = finger_template;
    }

    public String isIs_finger_registered() {
        return is_finger_registered;
    }

    public void setIs_finger_registered(String is_finger_registered) {
        this.is_finger_registered = is_finger_registered;
    }

    @Override
    public String toString() {
        return "Estudantes{" +
                "id_estudante=" + id_estudante +
                ", apelido='" + apelido + '\'' +
                ", nome='" + nome + '\'' +
                ", nr_estudante='" + nr_estudante + '\'' +
                ", url_foto='" + url_foto + '\'' +
                ", email='" + email + '\'' +
                ", senha='" + senha + '\'' +
                ", genero='" + genero + '\'' +
                ", data_nascimento=" + data_nascimento +
                ", id_curso=" + id_curso +
                ", finger_template=" + Arrays.toString(finger_template) +
                ", is_finger_registered=" + is_finger_registered +
                '}';
    }
}
