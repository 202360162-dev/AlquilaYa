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

import com.soa.alquilaya.models.ClienteModel;
import com.soa.alquilaya.services.ClienteService;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public List<ClienteModel> listar() {
        return clienteService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> buscar(@PathVariable Long id) {
        ClienteModel cliente = clienteService.buscarPorId(id);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe el cliente");
        }
        return ResponseEntity.ok(cliente);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Object> buscarPorEmail(@PathVariable String email) {
        ClienteModel cliente = clienteService.buscarPorEmail(email);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay cliente con ese email");
        }
        return ResponseEntity.ok(cliente);
    }

    @GetMapping("/licencia/{licencia}")
    public ResponseEntity<Object> buscarPorLicencia(@PathVariable String licencia) {
        ClienteModel cliente = clienteService.buscarPorLicencia(licencia);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay cliente con esa licencia");
        }
        return ResponseEntity.ok(cliente);
    }

    @PostMapping
    public ResponseEntity<Object> crear(@RequestBody ClienteModel cliente) {
        String error = validar(cliente);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        if (clienteService.buscarPorEmail(cliente.getEmail()) != null) {
            return ResponseEntity.badRequest().body("Ya hay un cliente con ese email");
        }
        if (clienteService.buscarPorLicencia(cliente.getLicenciaConducir()) != null) {
            return ResponseEntity.badRequest().body("Ya hay un cliente con esa licencia");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.guardar(cliente));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> editar(@PathVariable Long id, @RequestBody ClienteModel cliente) {
        if (clienteService.buscarPorId(id) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe el cliente");
        }
        String error = validar(cliente);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        ClienteModel conEmail = clienteService.buscarPorEmail(cliente.getEmail());
        if (conEmail != null && !conEmail.getIdCliente().equals(id)) {
            return ResponseEntity.badRequest().body("Ya hay un cliente con ese email");
        }
        ClienteModel conLicencia = clienteService.buscarPorLicencia(cliente.getLicenciaConducir());
        if (conLicencia != null && !conLicencia.getIdCliente().equals(id)) {
            return ResponseEntity.badRequest().body("Ya hay un cliente con esa licencia");
        }
        cliente.setIdCliente(id);
        return ResponseEntity.ok(clienteService.guardar(cliente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> borrar(@PathVariable Long id) {
        if (clienteService.buscarPorId(id) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe el cliente");
        }
        if (clienteService.tieneAlquileres(id)) {
            return ResponseEntity.badRequest().body("No se puede borrar, el cliente tiene alquileres");
        }
        clienteService.borrar(id);
        return ResponseEntity.ok("Cliente eliminado");
    }

    private String validar(ClienteModel cliente) {
        if (cliente.getNombre() == null || cliente.getApellido() == null
                || cliente.getEmail() == null || cliente.getLicenciaConducir() == null) {
            return "Faltan datos del cliente";
        }
        if (!cliente.getEmail().contains("@")) {
            return "El email no es valido";
        }
        return null;
    }
}
