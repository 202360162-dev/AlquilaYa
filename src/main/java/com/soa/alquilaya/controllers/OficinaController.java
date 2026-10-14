package com.soa.alquilaya.controllers;

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
import com.soa.alquilaya.services.OficinaService;

@RestController
@RequestMapping("/api/oficinas")
public class OficinaController {

    @Autowired
    private OficinaService oficinaService;

    @GetMapping
    public List<OficinaModel> listar() {
        return oficinaService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> buscar(@PathVariable Long id) {
        OficinaModel oficina = oficinaService.buscarPorId(id);
        if (oficina == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe la oficina");
        }
        return ResponseEntity.ok(oficina);
    }

    @PostMapping
    public ResponseEntity<Object> crear(@RequestBody OficinaModel oficina) {
        if (faltanDatos(oficina)) {
            return ResponseEntity.badRequest().body("Faltan datos de la oficina");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(oficinaService.guardar(oficina));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> editar(@PathVariable Long id, @RequestBody OficinaModel oficina) {
        if (oficinaService.buscarPorId(id) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe la oficina");
        }
        if (faltanDatos(oficina)) {
            return ResponseEntity.badRequest().body("Faltan datos de la oficina");
        }
        oficina.setIdOficina(id);
        return ResponseEntity.ok(oficinaService.guardar(oficina));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> borrar(@PathVariable Long id) {
        if (oficinaService.buscarPorId(id) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe la oficina");
        }
        if (oficinaService.tieneVehiculos(id)) {
            return ResponseEntity.badRequest().body("No se puede borrar, la oficina tiene vehiculos");
        }
        oficinaService.borrar(id);
        return ResponseEntity.ok("Oficina eliminada");
    }

    private boolean faltanDatos(OficinaModel oficina) {
        return oficina.getNombre() == null || oficina.getDireccion() == null
                || oficina.getCiudad() == null || oficina.getRegion() == null;
    }
}
