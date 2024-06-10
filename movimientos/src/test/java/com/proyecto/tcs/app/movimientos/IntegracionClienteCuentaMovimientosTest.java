package com.proyecto.tcs.app.movimientos;

import com.proyecto.tcs.app.movimientos.entities.Cliente;
import com.proyecto.tcs.app.movimientos.entities.Cuenta;
import com.proyecto.tcs.app.movimientos.entities.Movimiento;
import com.proyecto.tcs.app.movimientos.repositories.CuentaRepository;
import com.proyecto.tcs.app.movimientos.repositories.MovimientoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;


import java.util.Date;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegracionClienteCuentaMovimientosTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private RestTemplate restTemplate;

    private Cliente cliente;
    private Cuenta cuenta;
    private Movimiento movimiento;

    @BeforeEach
    public void setup() {
        // Crea cliente
        cliente = new Cliente();
        cliente.setNombre("Cliente Prueba");
        cliente.setGenero("Alvaro");
        cliente.setEdad(32L);
        cliente.setIdentificacion("1718892746");
        cliente.setDireccion("Calle 13");
        cliente.setTelefono("0983101346");
        cliente.setContrasenia("1234");
        cliente.setEstado(Boolean.TRUE);

        ResponseEntity<Cliente> response = restTemplate.postForEntity("http://localhost:8080/api/clientes", cliente, Cliente.class);
        cliente = response.getBody();

        // Crea cuenta asociada al cliente
        cuenta = new Cuenta();
        cuenta.setCliente(cliente);
        cuenta.setSaldoInicial(new BigDecimal("1000"));
        cuenta = cuentaRepository.save(cuenta);

        // Crea movimiento asociado a la cuenta
        movimiento = new Movimiento();
        movimiento.setCuenta(cuenta);
        movimiento.setFecha(new Date());
        movimiento.setTipoMovimiento("Depósito");
        movimiento.setValor(new BigDecimal("500"));
        movimiento.setSaldo(new BigDecimal("1500"));
        movimientoRepository.save(movimiento);
    }

    @Test
    public void testObtenerClienteConCuentaYMovimiento() throws Exception {
        // Realiza solicitud GET para obtener el cliente
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/api/clientes/{id}", cliente.getId())
                .contentType(MediaType.APPLICATION_JSON));

        // Verifica que la solicitud fue exitosa y que se recupera el cliente con sus cuentas y movimientos asociados
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(cliente.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nombre").value(cliente.getNombre()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cuentas[0].id").value(cuenta.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cuentas[0].saldoInicial").value(cuenta.getSaldoInicial()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cuentas[0].movimientos[0].id").value(movimiento.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cuentas[0].movimientos[0].tipoMovimiento").value(movimiento.getTipoMovimiento()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cuentas[0].movimientos[0].valor").value(movimiento.getValor()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cuentas[0].movimientos[0].saldo").value(movimiento.getSaldo()));
    }
}