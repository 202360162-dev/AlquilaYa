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

import com.soa.alquilaya.models.OficinaModel;
import com.soa.alquilaya.models.VehiculoModel;
import com.soa.alquilaya.services.OficinaService;
import com.soa.alquilaya.services.VehiculoService;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    @Autowired
    private VehiculoService vehiculoService;

    @Autowired
    private OficinaService oficinaService;

    @GetMapping
    public List<VehiculoModel> listar() {
        return vehiculoService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> buscar(@PathVariable Long id) {
        VehiculoModel vehiculo = vehiculoService.buscarPorId(id);
        if (vehiculo == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe el vehiculo");
        }
        return ResponseEntity.ok(vehiculo);
    }

    @GetMapping("/estado/{estado}")
    public List<VehiculoModel> buscarPorEstado(@PathVariable String estado) {
        return vehiculoService.buscarPorEstado(estado);
    }

    @PostMapping
    public ResponseEntity<Object> crear(@RequestBody VehiculoModel vehiculo) {
        String error = validar(vehiculo);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        if (vehiculoService.buscarPorMatricula(vehiculo.getMatricula()) != null) {
            return ResponseEntity.badRequest().body("Ya hay un vehiculo con esa matricula");
        }
        if (vehiculo.getOficina() == null) {
            return ResponseEntity.badRequest().body("Falta la oficina del vehiculo");
        }
        OficinaModel oficina = oficinaService.buscarPorId(vehiculo.getOficina().getIdOficina());
        if (oficina == null) {
            return ResponseEntity.badRequest().body("La oficina no existe");
        }
        vehiculo.setOficina(oficina);
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculoService.guardar(vehiculo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> editar(@PathVariable Long id, @RequestBody VehiculoModel datos) {
        VehiculoModel vehiculo = vehiculoService.buscarPorId(id);
        if (vehiculo == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe el vehiculo");
        }
        String error = validar(datos);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        VehiculoModel mismaMatricula = vehiculoService.buscarPorMatricula(datos.getMatricula());
        if (mismaMatricula != null && !mismaMatricula.getIdVehiculo().equals(id)) {
            return ResponseEntity.badRequest().body("Ya hay un vehiculo con esa matricula");
        }
        datos.setIdVehiculo(id);
        datos.setEstado(vehiculo.getEstado());
        datos.setOficina(vehiculo.getOficina());
        return ResponseEntity.ok(vehiculoService.guardar(datos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> borrar(@PathVariable Long id) {
        if (vehiculoService.buscarPorId(id) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe el vehiculo");
        }
        if (vehiculoService.tieneAlquileres(id)) {
            return ResponseEntity.badRequest().body("No se puede borrar, el vehiculo tiene alquileres");
        }
        vehiculoService.borrar(id);
        return ResponseEntity.ok("Vehiculo eliminado");
    }

    private String validar(VehiculoModel vehiculo) {
        if (vehiculo.getMatricula() == null || vehiculo.getMarca() == null
                || vehiculo.getModelo() == null || vehiculo.getAnio() == null || vehiculo.getTarifaDiaria() == null) {
            return "Faltan datos del vehiculo";
        }
        if (vehiculo.getTarifaDiaria().compareTo(BigDecimal.ZERO) <= 0) {
            return "La tarifa diaria debe ser mayor a 0";
        }
        return null;
    }
}
