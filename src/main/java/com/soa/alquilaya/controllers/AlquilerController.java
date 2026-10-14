package com.soa.alquilaya.controllers;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soa.alquilaya.models.AlquilerModel;
import com.soa.alquilaya.models.ClienteModel;
import com.soa.alquilaya.models.VehiculoModel;
import com.soa.alquilaya.services.AlquilerService;
import com.soa.alquilaya.services.ClienteService;
import com.soa.alquilaya.services.VehiculoService;

@RestController
@RequestMapping("/api/alquileres")
public class AlquilerController {

    @Autowired
    private AlquilerService alquilerService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private VehiculoService vehiculoService;

    @GetMapping
    public List<AlquilerModel> listar() {
        return alquilerService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> buscar(@PathVariable Long id) {
        AlquilerModel alquiler = alquilerService.buscarPorId(id);
        if (alquiler == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe el alquiler");
        }
        return ResponseEntity.ok(alquiler);
    }

    @PostMapping
    public ResponseEntity<Object> crear(@RequestBody AlquilerModel alquiler) {
        if (alquiler.getCliente() == null || alquiler.getVehiculo() == null) {
            return ResponseEntity.badRequest().body("Faltan el cliente o el vehiculo");
        }
        String error = validar(alquiler);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        ClienteModel cliente = clienteService.buscarPorId(alquiler.getCliente().getIdCliente());
        if (cliente == null) {
            return ResponseEntity.badRequest().body("El cliente no existe");
        }
        VehiculoModel vehiculo = vehiculoService.buscarPorId(alquiler.getVehiculo().getIdVehiculo());
        if (vehiculo == null) {
            return ResponseEntity.badRequest().body("El vehiculo no existe");
        }
        if ("ALQUILADO".equals(vehiculo.getEstado())) {
            return ResponseEntity.badRequest().body("El vehiculo ya esta alquilado");
        }
        vehiculoService.cambiarEstado(vehiculo, "ALQUILADO");
        alquiler.setCliente(cliente);
        alquiler.setVehiculo(vehiculo);
        return ResponseEntity.status(HttpStatus.CREATED).body(alquilerService.guardar(alquiler));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> editar(@PathVariable Long id, @RequestBody AlquilerModel datos) {
        AlquilerModel alquiler = alquilerService.buscarPorId(id);
        if (alquiler == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe el alquiler");
        }
        String error = validar(datos);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        datos.setIdAlquiler(id);
        datos.setCliente(alquiler.getCliente());
        datos.setVehiculo(alquiler.getVehiculo());
        return ResponseEntity.ok(alquilerService.guardar(datos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> borrar(@PathVariable Long id) {
        AlquilerModel alquiler = alquilerService.buscarPorId(id);
        if (alquiler == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe el alquiler");
        }
        vehiculoService.cambiarEstado(alquiler.getVehiculo(), "DISPONIBLE");
        alquilerService.borrar(id);
        return ResponseEntity.ok("Alquiler eliminado, el vehiculo quedo disponible");
    }

    private String validar(AlquilerModel alquiler) {
        if (alquiler.getFechaAlquiler() == null || alquiler.getFechaDevolucion() == null) {
            return "Faltan las fechas del alquiler";
        }
        if (!alquiler.getFechaAlquiler().isBefore(alquiler.getFechaDevolucion())) {
            return "La fecha de devolucion tiene que ser despues de la de alquiler";
        }
        if (alquiler.getPrecioTotal() == null || alquiler.getPrecioTotal().compareTo(BigDecimal.ZERO) <= 0) {
            return "El precio total debe ser mayor a 0";
        }
        return null;
    }
}
