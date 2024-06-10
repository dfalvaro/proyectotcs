package com.proyecto.tcs.app.movimientos.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import java.util.List;



@Entity
@Table(name = "clientes", uniqueConstraints = {@UniqueConstraint(columnNames = {"clienteid"})})
@Data
public class Cliente extends Persona{

    private Long clienteid;
    private String contrasenia;
    private Boolean estado;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Cuenta> cuentas;

}
