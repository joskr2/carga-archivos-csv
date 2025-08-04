# 📦 Pedidos Microservicio

Microservicio para **cargar pedidos desde archivos CSV** con validaciones de negocio y arquitectura hexagonal.

##  Quick Start

```bash
# 1. Configurar variables de entorno (OBLIGATORIO)
cp .env.dev.example .env.dev
# Editar .env.dev y cambiar "your_dev_password_here" por contraseña real

# 2. Ejecutar con Docker
make up-dev

# 3. Probar endpoint
curl -X POST http://localhost:8080/pedidos/cargar -F "file=@pedidos_validos.csv"
```

**🎯 Endpoint disponible:** `http://localhost:8080/pedidos/cargar`

---

## 🛠️ Tecnologías

- **Java 17** + **Spring Boot 3.x**
- **PostgreSQL** (producción) / **H2** (tests)
- **Docker** + **Maven**
- **Arquitectura hexagonal** con caché y optimización de consultas

---

## ⚙️ Ejecución

### Con Docker (Recomendado)

```bash
# ⚠️ PASO 1: Configurar variables de entorno
cp .env.dev.example .env.dev
nano .env.dev  # Cambiar contraseñas

# PASO 2: Ejecutar
make up-dev      # Desarrollo
make up-prod     # Producción
make test        # Tests (no requiere configuración)
```

### Sin Docker

```bash
# Ejecuta con H2 automáticamente
./mvnw spring-boot:run
```

---

## 📄 Formato CSV

```csv
numeroPedido,clienteId,fechaEntrega,estado,zonaEntrega,requiereRefrigeracion
P001,CLI-123,2025-08-10,PENDIENTE,ZONA1,true
P002,CLI-999,2025-08-12,ENTREGADO,ZONA5,false
```

### Validaciones implementadas

- ✅ `numeroPedido`: único
- ✅ `clienteId`: debe existir
- ✅ `fechaEntrega`: no puede ser pasada
- ✅ `estado`: PENDIENTE | CONFIRMADO | ENTREGADO
- ✅ `zonaEntrega`: debe existir
- ✅ `requiereRefrigeracion`: zona debe soportar refrigeración

---

## 📊 Ejemplo de respuesta

```json
{
  "totalRegistros": 5,
  "registrosGuardados": 3,
  "errores": [
    {
      "linea": 2,
      "motivo": "Cliente CLI-999 no existe",
      "tipo": "CLIENTE_NO_ENCONTRADO"
    }
  ]
}
```

---

## 🗂️ Datos de prueba incluidos

**Clientes:** CLI-123, CLI-999  
**Zonas:** ZONA1 (con refrigeración), ZONA5 (sin refrigeración)

**Archivos CSV de ejemplo:**

- `pedidos_validos.csv` - Todos los registros válidos
- `pedidos_con_errores.csv` - Incluye errores para testing

---

## � Troubleshooting

### Error: "Failed to configure a DataSource"

```bash
# Verificar variables de entorno
cp .env.dev.example .env.dev
nano .env.dev  # Cambiar "your_password_here"
```

### Error: "Connection refused"

```bash
# Verificar que las contraseñas sean reales
cat .env.dev  # No debe contener "your_password_here"
```

### ✅ Tests siempre funcionan

```bash
make test  # Usa H2, no requiere configuración
```

---

## 🏗️ Arquitectura

```text
src/main/java/com/josue/pedidos_ms/
├── domain/          # Entidades y servicios de negocio
├── application/     # Casos de uso
├── infrastructure/  # Controladores y repositorios
└── shared/          # DTOs y errores compartidos
```

**Características avanzadas:**

- 🚀 **Spring Cache** con estrategias optimizadas
- 📊 **Índices de base de datos** para consultas rápidas
- 🔍 **Logging estructurado** con trazabilidad
- ⚡ **Consultas JPQL explícitas** para mejor rendimiento

---

## 👨‍💻 Comandos disponibles

```bash
make up-dev          # Desarrollo con PostgreSQL
make up-prod         # Producción
make test            # Ejecutar tests
make clean           # Limpiar contenedores
make build-local     # Compilar JAR localmente
```

---

**Autor:** Josué Patricio  
**Licencia:** MIT
