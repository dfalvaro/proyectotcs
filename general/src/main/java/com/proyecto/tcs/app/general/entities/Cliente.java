package com.proyecto.tcs.app.general.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;


@Entity
@Table(name = "cliente", uniqueConstraints = {@UniqueConstraint(columnNames = {"clienteid"})})
@Data
public class Cliente extends Persona{

    private Long clienteid;
    private String contrasenia;
    private Boolean estado;



}
