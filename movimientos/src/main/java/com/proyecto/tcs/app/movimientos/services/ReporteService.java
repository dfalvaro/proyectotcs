package com.proyecto.tcs.app.movimientos.services;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.proyecto.tcs.app.movimientos.dto.EstadoCuentaDto;
import com.proyecto.tcs.app.movimientos.entities.Cliente;
import com.proyecto.tcs.app.movimientos.entities.Cuenta;
import com.proyecto.tcs.app.movimientos.entities.Movimiento;
import com.proyecto.tcs.app.movimientos.repositories.CuentaRepository;
import com.proyecto.tcs.app.movimientos.repositories.MovimientoRepository;

@Service
public class ReporteService {

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private MovimientoRepository movimientoRepository;

     @Autowired
    private RestTemplate restTemplate;

    public EstadoCuentaDto generarReporte(Long clienteId, Date fechaInicio, Date fechaFin) {
        // Obtener el cliente utilizando el endpoint /api/clientes/{id}
        ResponseEntity<Cliente> response = restTemplate.getForEntity("http://localhost:8080/api/clientes/{id}", Cliente.class, clienteId);
        Cliente cliente = response.getBody();

         // Verificar si se encontró el cliente
         if (cliente == null) {
            throw new RuntimeException("Cliente no encontrado");
        }

        // Obtener las cuentas del cliente
        List<Cuenta> cuentas = cuentaRepository.findByClienteId(clienteId);

        EstadoCuentaDto reporte = new EstadoCuentaDto();
        reporte.setClienteId(clienteId);
        reporte.setClienteNombre(cliente.getNombre());

        List<EstadoCuentaDto.CuentaDto> cuentasDTO = cuentas.stream().map(cuenta -> {
            EstadoCuentaDto.CuentaDto cuentaDTO = new EstadoCuentaDto.CuentaDto();
            cuentaDTO.setCuentaId(cuenta.getId());
            cuentaDTO.setNumeroCuenta(cuenta.getNumeroCuenta());
            cuentaDTO.setTipoCuenta(cuenta.getTipoCuenta());
            cuentaDTO.setSaldoInicial(cuenta.getSaldoInicial());

            List<Movimiento> movimientos = movimientoRepository.findByCuentaIdAndFechaBetween(cuenta.getId(), fechaInicio, fechaFin);

            List<EstadoCuentaDto.MovimientoDto> movimientosDTO = movimientos.stream().map(movimiento -> {
                EstadoCuentaDto.MovimientoDto movimientoDTO = new EstadoCuentaDto.MovimientoDto();
                movimientoDTO.setFecha(movimiento.getFecha());
                movimientoDTO.setTipoMovimiento(movimiento.getTipoMovimiento());
                movimientoDTO.setValor(movimiento.getValor());
                movimientoDTO.setSaldo(movimiento.getSaldo());
                return movimientoDTO;
            }).collect(Collectors.toList());

            cuentaDTO.setMovimientos(movimientosDTO);
            return cuentaDTO;
        }).collect(Collectors.toList());

        reporte.setCuentas(cuentasDTO);
        return reporte;
    }
}
