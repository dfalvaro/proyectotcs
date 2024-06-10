package com.proyecto.tcs.app.movimientos.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.proyecto.tcs.app.movimientos.entities.Cuenta;

public interface CuentaRepository extends CrudRepository<Cuenta,Long>{

     List<Cuenta> findByClienteId(Long clienteId);

}
