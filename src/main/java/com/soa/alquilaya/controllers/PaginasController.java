package com.soa.alquilaya.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.soa.alquilaya.models.AlquilerModel;
import com.soa.alquilaya.models.ClienteModel;
import com.soa.alquilaya.models.OficinaModel;
import com.soa.alquilaya.models.VehiculoModel;
import com.soa.alquilaya.services.AlquilerService;
import com.soa.alquilaya.services.ClienteService;
import com.soa.alquilaya.services.OficinaService;
import com.soa.alquilaya.services.VehiculoService;

@Controller
public class PaginasController {

    @Autowired
    private OficinaService oficinaService;

    @Autowired
    private VehiculoService vehiculoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private AlquilerService alquilerService;

    @GetMapping("/")
    public String inicio() {
        return "redirect:/oficinas";
    }

    @GetMapping("/oficinas")
    public String oficinas(Model model) {
        model.addAttribute("oficinas", oficinaService.listar());
        return "oficinas";
    }

    @PostMapping("/oficinas")
    public String crearOficina(@RequestParam String nombre, @RequestParam String direccion,
            @RequestParam String ciudad, @RequestParam String region) {
        OficinaModel oficina = new OficinaModel();
        oficina.setNombre(nombre);
        oficina.setDireccion(direccion);
        oficina.setCiudad(ciudad);
        oficina.setRegion(region);
        oficinaService.guardar(oficina);
        return "redirect:/oficinas";
    }

    @PostMapping("/oficinas/{id}/borrar")
    public String borrarOficina(@PathVariable Long id, Model model) {
        if (oficinaService.tieneVehiculos(id)) {
            model.addAttribute("error", "No se puede borrar, la oficina tiene vehiculos");
            return oficinas(model);
        }
        oficinaService.borrar(id);
        return "redirect:/oficinas";
    }

    @GetMapping("/vehiculos")
    public String vehiculos(Model model) {
        model.addAttribute("vehiculos", vehiculoService.listar());
        model.addAttribute("oficinas", oficinaService.listar());
        return "vehiculos";
    }

    @PostMapping("/vehiculos")
    public String crearVehiculo(@RequestParam String matricula, @RequestParam String marca,
            @RequestParam String modelo, @RequestParam Integer anio, @RequestParam BigDecimal tarifaDiaria,
            @RequestParam Long idOficina, Model model) {
        if (vehiculoService.buscarPorMatricula(matricula) != null) {
            model.addAttribute("error", "Ya hay un vehiculo con esa matricula");
            return vehiculos(model);
        }
        OficinaModel oficina = oficinaService.buscarPorId(idOficina);
        if (oficina == null) {
            model.addAttribute("error", "La oficina no existe");
            return vehiculos(model);
        }
        VehiculoModel vehiculo = new VehiculoModel();
        vehiculo.setMatricula(matricula);
        vehiculo.setMarca(marca);
        vehiculo.setModelo(modelo);
        vehiculo.setAnio(anio);
        vehiculo.setTarifaDiaria(tarifaDiaria);
        vehiculo.setOficina(oficina);
        vehiculoService.guardar(vehiculo);
        return "redirect:/vehiculos";
    }

    @PostMapping("/vehiculos/{id}/borrar")
    public String borrarVehiculo(@PathVariable Long id, Model model) {
        if (vehiculoService.tieneAlquileres(id)) {
            model.addAttribute("error", "No se puede borrar, el vehiculo tiene alquileres");
            return vehiculos(model);
        }
        vehiculoService.borrar(id);
        return "redirect:/vehiculos";
    }

    @GetMapping("/clientes")
    public String clientes(Model model) {
        model.addAttribute("clientes", clienteService.listar());
        return "clientes";
    }

    @PostMapping("/clientes")
    public String crearCliente(@RequestParam String nombre, @RequestParam String apellido,
            @RequestParam String email, @RequestParam String telefono,
            @RequestParam String licenciaConducir, Model model) {
        if (clienteService.buscarPorEmail(email) != null) {
            model.addAttribute("error", "Ya hay un cliente con ese email");
            return clientes(model);
        }
        if (clienteService.buscarPorLicencia(licenciaConducir) != null) {
            model.addAttribute("error", "Ya hay un cliente con esa licencia");
            return clientes(model);
        }
        ClienteModel cliente = new ClienteModel();
        cliente.setNombre(nombre);
        cliente.setApellido(apellido);
        cliente.setEmail(email);
        cliente.setTelefono(telefono);
        cliente.setLicenciaConducir(licenciaConducir);
        clienteService.guardar(cliente);
        return "redirect:/clientes";
    }

    @PostMapping("/clientes/{id}/borrar")
    public String borrarCliente(@PathVariable Long id, Model model) {
        if (clienteService.tieneAlquileres(id)) {
            model.addAttribute("error", "No se puede borrar, el cliente tiene alquileres");
            return clientes(model);
        }
        clienteService.borrar(id);
        return "redirect:/clientes";
    }

    @GetMapping("/alquileres")
    public String alquileres(Model model) {
        model.addAttribute("alquileres", alquilerService.listar());
        model.addAttribute("clientes", clienteService.listar());
        model.addAttribute("vehiculos", vehiculoService.buscarPorEstado("DISPONIBLE"));
        return "alquileres";
    }

    @PostMapping("/alquileres")
    public String crearAlquiler(@RequestParam Long idCliente, @RequestParam Long idVehiculo,
            @RequestParam String fechaAlquiler, @RequestParam String fechaDevolucion,
            @RequestParam BigDecimal precioTotal, Model model) {
        LocalDate inicio = LocalDate.parse(fechaAlquiler);
        LocalDate fin = LocalDate.parse(fechaDevolucion);
        if (!inicio.isBefore(fin)) {
            model.addAttribute("error", "La fecha de devolucion tiene que ser despues de la de alquiler");
            return alquileres(model);
        }
        ClienteModel cliente = clienteService.buscarPorId(idCliente);
        VehiculoModel vehiculo = vehiculoService.buscarPorId(idVehiculo);
        if (cliente == null || vehiculo == null) {
            model.addAttribute("error", "El cliente o el vehiculo no existe");
            return alquileres(model);
        }
        if ("ALQUILADO".equals(vehiculo.getEstado())) {
            model.addAttribute("error", "El vehiculo ya esta alquilado");
            return alquileres(model);
        }
        vehiculoService.cambiarEstado(vehiculo, "ALQUILADO");
        AlquilerModel alquiler = new AlquilerModel();
        alquiler.setCliente(cliente);
        alquiler.setVehiculo(vehiculo);
        alquiler.setFechaAlquiler(inicio);
        alquiler.setFechaDevolucion(fin);
        alquiler.setPrecioTotal(precioTotal);
        alquilerService.guardar(alquiler);
        return "redirect:/alquileres";
    }

    @PostMapping("/alquileres/{id}/borrar")
    public String borrarAlquiler(@PathVariable Long id) {
        AlquilerModel alquiler = alquilerService.buscarPorId(id);
        if (alquiler != null) {
            vehiculoService.cambiarEstado(alquiler.getVehiculo(), "DISPONIBLE");
            alquilerService.borrar(id);
        }
        return "redirect:/alquileres";
    }
}
