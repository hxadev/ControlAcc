package com.example.controlacc.model;

public class Escuela {

    private String nombre;
    private int nivelEducativo;
    private int estado;
    private int municipio;
    private String direccion;
    private String ipServidorEscuela;
    private int escuelaConfigurada;

    public Escuela(){
        ;
    }

    public Escuela(String nombre, int nivelEducativo, int estado, int municipio, String direccion, String ipServidorEscuela, int escuelaConfigurada) {
        this.nombre = nombre;
        this.nivelEducativo = nivelEducativo;
        this.estado = estado;
        this.municipio = municipio;
        this.direccion = direccion;
        this.ipServidorEscuela = ipServidorEscuela;
        this.escuelaConfigurada = escuelaConfigurada;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getNivelEducativo() {
        return nivelEducativo;
    }

    public void setNivelEducativo(int nivelEducativo) {
        this.nivelEducativo = nivelEducativo;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public int getMunicipio() {
        return municipio;
    }

    public void setMunicipio(int municipio) {
        this.municipio = municipio;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getIpServidorEscuela() {
        return ipServidorEscuela;
    }

    public void setIpServidorEscuela(String ipServidorEscuela) {
        this.ipServidorEscuela = ipServidorEscuela;
    }

    public int getEscuelaConfigurada() {
        return escuelaConfigurada;
    }

    public void setEscuelaConfigurada(int escuelaConfigurada) {
        this.escuelaConfigurada = escuelaConfigurada;
    }


}
