# API AlquilaYa

API REST para rentar vehiculos. Maneja oficinas, vehiculos, clientes y alquileres.

Reglas principales:

- Cada vehiculo pertenece a una oficina.
- No se pueden repetir matriculas de vehiculos, ni emails o licencias de clientes.
- Al crear un alquiler el vehiculo pasa a `ALQUILADO` y no se puede volver a rentar. Cuando el alquiler se elimina el vehiculo regresa a `DISPONIBLE`.
- La fecha de devolucion tiene que ser despues de la fecha de alquiler.
- No se puede eliminar una oficina con vehiculos, ni un vehiculo o cliente que tenga alquileres.

Ademas de la API tiene paginas web para usarla desde el navegador.

## Tecnologias

- Java 21
- Spring Boot 4.1.1 (Spring Web y Spring Data JPA)
- Thymeleaf para las paginas web
- MySQL 8
- Maven (el proyecto trae `mvnw`, no hace falta instalarlo)

## Instalacion y ejecucion

1. Tener instalado Java 21 y MySQL 8 corriendo en el puerto 3306.
2. Clonar el repositorio y entrar a la carpeta del proyecto.
3. Configurar las variables de entorno de la base de datos (ver la siguiente seccion).
4. Ejecutar:

```
mvnw.cmd spring-boot:run
```

Tambien se puede correr desde el IDE ejecutando la clase `AlquilayaApplication`.

La API queda en `http://localhost:8083/api` y las paginas web en `http://localhost:8083/`. La base de datos `renta_autos` y sus tablas se crean solas al arrancar.

## Variables de entorno

| Variable | Descripcion | Valor por defecto |
|---|---|---|
| `DB_URL` | URL de conexion a MySQL | `jdbc:mysql://localhost:3306/renta_autos?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false` |
| `DB_USER` | Usuario de MySQL | `root` |
| `DB_PASSWORD` | Contrasena de MySQL | vacia |
| `PORT` | Puerto de la API | `8083` |

Ejemplo en la terminal antes de ejecutar:

```
set DB_PASSWORD=tu_contrasena
```

## Paginas web

| Ruta | Que hace |
|---|---|
| `/oficinas` | Lista, agrega y elimina oficinas |
| `/vehiculos` | Lista, agrega y elimina vehiculos |
| `/clientes` | Lista, agrega y elimina clientes |
| `/alquileres` | Lista y agrega alquileres. El boton "Devolver" elimina el alquiler y deja el vehiculo disponible |

Al entrar a `http://localhost:8083/` abre la pagina de oficinas.

## Endpoints

Oficinas, vehiculos, clientes y alquileres tienen las mismas operaciones:

| Metodo | Ruta | Descripcion |
|---|---|---|
| GET | `/api/{recurso}` | Lista todos |
| GET | `/api/{recurso}/{id}` | Busca uno por id |
| POST | `/api/{recurso}` | Crea un registro |
| PUT | `/api/{recurso}/{id}` | Edita un registro |
| DELETE | `/api/{recurso}/{id}` | Elimina un registro |

Recursos: `oficinas`, `vehiculos`, `clientes`, `alquileres`.

Endpoints extra:

| Metodo | Ruta | Descripcion |
|---|---|---|
| GET | `/api/vehiculos/estado/{estado}` | Vehiculos `DISPONIBLE` o `ALQUILADO` |
| GET | `/api/clientes/email/{email}` | Busca un cliente por email |
| GET | `/api/clientes/licencia/{licencia}` | Busca un cliente por licencia |

## Ejemplos

### Registrar una oficina

`POST http://localhost:8083/api/oficinas`

```json
{
  "nombre": "Sucursal Angelopolis",
  "direccion": "Blvd. del Nino Poblano 2510",
  "ciudad": "Puebla",
  "region": "Centro"
}
```

Respuesta `201 Created`

```json
{
  "ciudad": "Puebla",
  "direccion": "Blvd. del Nino Poblano 2510",
  "idOficina": 1,
  "nombre": "Sucursal Angelopolis",
  "region": "Centro"
}
```

### Registrar un vehiculo

`POST http://localhost:8083/api/vehiculos`

```json
{
  "marca": "Kia",
  "modelo": "Rio",
  "anio": 2021,
  "matricula": "PUE-123-A",
  "tarifaDiaria": 700,
  "oficina": { "idOficina": 1 }
}
```

Respuesta `201 Created`

```json
{
  "anio": 2021,
  "estado": "DISPONIBLE",
  "idVehiculo": 1,
  "marca": "Kia",
  "matricula": "PUE-123-A",
  "modelo": "Rio",
  "oficina": {
    "ciudad": "Puebla",
    "direccion": "Blvd. del Nino Poblano 2510",
    "idOficina": 1,
    "nombre": "Sucursal Angelopolis",
    "region": "Centro"
  },
  "tarifaDiaria": 700
}
```

### Registrar un vehiculo con matricula repetida

`POST http://localhost:8083/api/vehiculos`

```json
{
  "marca": "Chevrolet",
  "modelo": "Aveo",
  "anio": 2020,
  "matricula": "PUE-123-A",
  "tarifaDiaria": 600,
  "oficina": { "idOficina": 1 }
}
```

Respuesta `400 Bad Request`

```
Ya hay un vehiculo con esa matricula
```

### Registrar un cliente

`POST http://localhost:8083/api/clientes`

```json
{
  "nombre": "Sofia",
  "apellido": "Ruiz",
  "email": "sofia.ruiz@mail.com",
  "telefono": "2223334455",
  "licenciaConducir": "PUE-LIC-4821"
}
```

Respuesta `201 Created`

```json
{
  "apellido": "Ruiz",
  "email": "sofia.ruiz@mail.com",
  "idCliente": 1,
  "licenciaConducir": "PUE-LIC-4821",
  "nombre": "Sofia",
  "telefono": "2223334455"
}
```

### Rentar un vehiculo

`POST http://localhost:8083/api/alquileres`

```json
{
  "cliente": { "idCliente": 1 },
  "vehiculo": { "idVehiculo": 1 },
  "fechaAlquiler": "2026-10-20",
  "fechaDevolucion": "2026-10-22",
  "precioTotal": 1400
}
```

Respuesta `201 Created` (el vehiculo queda `ALQUILADO`)

```json
{
  "cliente": {
    "apellido": "Ruiz",
    "email": "sofia.ruiz@mail.com",
    "idCliente": 1,
    "licenciaConducir": "PUE-LIC-4821",
    "nombre": "Sofia",
    "telefono": "2223334455"
  },
  "fechaAlquiler": "2026-10-20",
  "fechaDevolucion": "2026-10-22",
  "idAlquiler": 1,
  "precioTotal": 1400,
  "vehiculo": {
    "anio": 2021,
    "estado": "ALQUILADO",
    "idVehiculo": 1,
    "marca": "Kia",
    "matricula": "PUE-123-A",
    "modelo": "Rio",
    "oficina": {
      "ciudad": "Puebla",
      "direccion": "Blvd. del Nino Poblano 2510",
      "idOficina": 1,
      "nombre": "Sucursal Angelopolis",
      "region": "Centro"
    },
    "tarifaDiaria": 700.0
  }
}
```

### Rentar un vehiculo que ya esta alquilado

`POST http://localhost:8083/api/alquileres`

```json
{
  "cliente": { "idCliente": 1 },
  "vehiculo": { "idVehiculo": 1 },
  "fechaAlquiler": "2026-10-25",
  "fechaDevolucion": "2026-10-27",
  "precioTotal": 1400
}
```

Respuesta `400 Bad Request`

```
El vehiculo ya esta alquilado
```

### Eliminar una oficina con vehiculos

`DELETE http://localhost:8083/api/oficinas/1`

Respuesta `400 Bad Request`

```
No se puede borrar, la oficina tiene vehiculos
```

### Devolver el vehiculo (eliminar el alquiler)

`DELETE http://localhost:8083/api/alquileres/1`

Respuesta `200 OK`

```
Alquiler eliminado, el vehiculo quedo disponible
```

### Buscar un cliente por email que no existe

`GET http://localhost:8083/api/clientes/email/nadie@mail.com`

Respuesta `404 Not Found`

```
No hay cliente con ese email
```
