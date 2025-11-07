package com.example.technicalassistance;

public class Dispositivo {
    public int id;
    public String tipo;
    public String modelo;
    public String proprietario;
    public String dataEntrega;
    public String status;

    public Dispositivo(int id, String tipo, String modelo, String proprietario, String dataEntrega, String status) {
        this.id = id;
        this.tipo = tipo;
        this.modelo = modelo;
        this.proprietario = proprietario;
        this.dataEntrega = dataEntrega;
        this.status = status;
    }
}
