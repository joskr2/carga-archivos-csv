# 📦 Pedidos Microservicio (Java + Spring Boot)

Microservicio encargado de **cargar pedidos de envío desde un archivo CSV**, aplicando validaciones de negocio y persistiendo los registros válidos.  
Cumple con la arquitectura **hexagonal** y los requisitos definidos en la prueba técnica.

---

## 🛠️ Tecnologías utilizadas

- Java 17
- Spring Boot 3.x
- Spring Data JPA
- H2 Database (modo desarrollo)
- Maven
- OpenCSV
- Arquitectura hexagonal (puertos y adaptadores)

---

## 🚀 Requisitos funcionales implementados

- [x] Endpoint `POST /pedidos/cargar` que recibe un archivo `.csv`.
- [x] Validaciones por fila:
  - `numeroPedido`: único (no repetido)
  - `clienteId`: debe existir
  - `fechaEntrega`: no puede ser una fecha pasada
  - `estado`: debe ser `PENDIENTE`, `CONFIRMADO`, `ENTREGADO`
  - `zonaEntrega`: debe existir
  - Si `requiereRefrigeracion` = `true`, la zona debe tener soporte para refrigeración
- [x] Solo se persisten pedidos válidos
- [x] Se devuelve:
  - Total de registros procesados
  - Registros guardados
  - Errores agrupados por tipo, incluyendo línea y motivo

---

## 🧪 Cómo ejecutar el proyecto localmente

### 1. Clona el repositorio

```bash
git clone https://github.com/tuusuario/pedidos-ms.git
cd pedidos-ms
```

### 2. Compila y ejecuta

```bash

./mvnw spring-boot:run
```

O usa tu IDE (IntelliJ, VS Code) para correr `PedidosMsApplication`.

### 3. Accede a la consola H2 (opcional)

URL: <http://localhost:8080/h2-console>

- JDBC URL: `jdbc:h2:mem:pedidosdb`
- User: `sa`
- Password: (vacío)

---

## 📤 Cómo probar el endpoint

### POST /pedidos/cargar

- Tipo de contenido: `multipart/form-data`
- Campo: `file` (archivo .csv)

### Ejemplo con curl

```bash
curl -X POST http://localhost:8080/pedidos/cargar \
  -F "file=@pedidos.csv"
```

### Ejemplo con Postman

- Método: POST
- URL: `http://localhost:8080/pedidos/cargar`
- Body > form-data > Key: `file` → Type: File → Selecciona tu archivo .csv

---

## 📄 Formato del archivo CSV

El archivo debe tener las siguientes columnas en este orden:

```csv
numeroPedido,clienteId,fechaEntrega,estado,zonaEntrega,requiereRefrigeracion
```

### Ejemplo

```csv
P001,CLI-123,2025-08-10,PENDIENTE,ZONA1,true
P002,CLI-999,2025-08-12,ENTREGADO,ZONA5,false
```

---

## 💾 Base de datos H2 (datos precargados)

El sistema carga datos iniciales desde `src/main/resources/data.sql` para:

**Clientes:**

- CLI-123, CLI-999

**Zonas:**

- ZONA1 (soporteRefrigeracion: true)
- ZONA5 (soporteRefrigeracion: false)

---

## ⚙️ Eficiencia del procesamiento

El archivo CSV se procesa línea por línea usando `BufferedReader` y `OpenCSV`,
evitando la carga total en memoria. Esto asegura eficiencia incluso con archivos grandes.

Las validaciones son realizadas por servicios de dominio desacoplados, cumpliendo con el principio de responsabilidad única.

---

## 📁 Arquitectura del proyecto

```text
src/
└── main/
    ├── domain/          ← Entidades y servicios de dominio
    ├── application/     ← Casos de uso (usecases)
    ├── infrastructure/  ← Repositorios y controladores (adaptadores)
    └── shared/          ← DTOs y errores
```

## 🧑 Autor

Josué Patricio

---

## 📝 Licencia

MIT
