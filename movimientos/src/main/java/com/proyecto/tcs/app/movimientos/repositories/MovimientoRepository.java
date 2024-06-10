package com.proyecto.tcs.app.movimientos.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.proyecto.tcs.app.movimientos.entities.Movimiento;

public interface MovimientoRepository extends CrudRepository<Movimiento,Long>{
    List<Movimiento> findByCuentaIdAndFechaBetween(Long cuentaId, Date fechaInicio, Date fechaFin);
}
