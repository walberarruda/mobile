package com.example.technicalassistance;

public class Usuario {
    public int id;
    public String nome, usuario, tipo;
    public Usuario(int id, String nome, String usuario, String tipo){
        this.id = id;
        this.nome = nome;
        this.usuario = usuario;
        this.tipo = tipo;
    }
    @Override
    public String toString() {
        return nome + " (" + usuario + ") - " + tipo;
    }
}
