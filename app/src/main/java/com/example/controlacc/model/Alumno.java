package com.example.controlacc.model;

/**
 * @author Alfonso Hernández Xochipa
 */
public class Alumno {

    private String nombre,apellidos,matricula;

    public Alumno(){;}

    public Alumno(String nombre,String apellidos, String matricula){
        this.nombre=nombre;
        this.apellidos=apellidos;
        this.matricula=matricula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }
}

