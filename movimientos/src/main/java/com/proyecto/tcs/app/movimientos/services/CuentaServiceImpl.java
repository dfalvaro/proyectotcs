package com.proyecto.tcs.app.movimientos.services;


import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.tcs.app.movimientos.entities.Cuenta;
import com.proyecto.tcs.app.movimientos.repositories.CuentaRepository;

@Service
public class CuentaServiceImpl implements CuentaService{
    
    @Autowired
    private CuentaRepository repository;

    @Transactional(readOnly = true)
    @Override
    public List<Cuenta> findAll() {
        return (List<Cuenta>) repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cuenta> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public Cuenta save(Cuenta cuenta) {
        return repository.save(cuenta);
    }

    @Override
    @Transactional
    public Optional<Cuenta> update(Long id, Cuenta cuenta) {
        Optional<Cuenta> cuentaOptional = repository.findById(id);
        if (cuentaOptional.isPresent()) {
            Cuenta cuentaDb = cuentaOptional.orElseThrow();
            
            cuentaDb.setNumeroCuenta(cuenta.getNumeroCuenta());
            cuentaDb.setTipoCuenta(cuenta.getTipoCuenta());
            cuentaDb.setSaldoInicial(cuenta.getSaldoInicial());
            cuentaDb.setEstado(cuenta.getEstado());
           

            return Optional.of(repository.save(cuentaDb));
            
        }
        return cuentaOptional;
    }

    @Transactional
    @Override
    public Optional<Cuenta> delete(Long id) {
        Optional<Cuenta> cuentaOptional = repository.findById(id);
        cuentaOptional.ifPresent(cuentaDb -> {
            repository.delete(cuentaDb);
        });
        return cuentaOptional;
    }
}
