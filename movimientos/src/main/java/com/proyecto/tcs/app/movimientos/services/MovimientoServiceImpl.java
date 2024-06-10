package com.proyecto.tcs.app.movimientos.services;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.tcs.app.movimientos.entities.Cuenta;
import com.proyecto.tcs.app.movimientos.entities.Movimiento;
import com.proyecto.tcs.app.movimientos.exceptions.SaldoInsuficienteException;
import com.proyecto.tcs.app.movimientos.repositories.CuentaRepository;
import com.proyecto.tcs.app.movimientos.repositories.MovimientoRepository;

@Service
public class MovimientoServiceImpl implements MovimientoService {
    @Autowired
    private MovimientoRepository repository;

    @Autowired
    private CuentaRepository cuentaRepository;

    @Transactional(readOnly = true)
    @Override
    public List<Movimiento> findAll() {
        return (List<Movimiento>) repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Movimiento> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public Movimiento registrarMovimiento(Long cuentaId, String tipoMovimiento, BigDecimal valor) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        BigDecimal nuevoSaldo = cuenta.getSaldoInicial().add(valor);
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new SaldoInsuficienteException("Saldo no disponible");
        }
        cuenta.setSaldoInicial(nuevoSaldo);
        cuentaRepository.save(cuenta);

        Movimiento movimiento = new Movimiento();
        movimiento.setFecha(new Date());
        movimiento.setTipoMovimiento(tipoMovimiento);
        movimiento.setValor(valor);
        movimiento.setSaldo(nuevoSaldo);
        movimiento.setCuenta(cuenta);

        return repository.save(movimiento);
    }

    @Override
    @Transactional
    public Movimiento save(Movimiento movimiento) {
        return repository.save(movimiento);
    }

    @Override
    @Transactional
    public Optional<Movimiento> update(Long id, Movimiento movimiento) {
        Optional<Movimiento> movimientoOptional = repository.findById(id);
        if (movimientoOptional.isPresent()) {
            Movimiento movimientoDb = movimientoOptional.orElseThrow();

            movimientoDb.setFecha(movimiento.getFecha());
            movimientoDb.setTipoMovimiento(movimiento.getTipoMovimiento());
            movimientoDb.setValor(movimiento.getValor());
            movimientoDb.setSaldo(movimiento.getSaldo());

            return Optional.of(repository.save(movimientoDb));

        }
        return movimientoOptional;
    }

    @Transactional
    @Override
    public Optional<Movimiento> delete(Long id) {
        Optional<Movimiento> movimientoOptional = repository.findById(id);
        movimientoOptional.ifPresent(movimientoDb -> {
            repository.delete(movimientoDb);
        });
        return movimientoOptional;
    }

}
