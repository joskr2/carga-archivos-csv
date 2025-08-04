# 📦 Pedidos Microservicio (Java + Spring Boot)

Microservicio encargado de **cargar pedidos de envío desde un archivo CSV**, aplicando validaciones de negocio y persistiendo los registros válidos.  
Cumple con la arquitectura **hexagonal** y los requisitos definidos en la prueba técnica.

> ## 🚨 **ANTES DE EMPEZAR - CONFIGURACIÓN OBLIGATORIA**
>
> **⚠️ PASO CRÍTICO:** Para ejecutar con Docker, debes copiar y configurar las variables de entorno:
>
> ```bash
> cp .env.dev.example .env.dev
> cp .env.prod.example .env.prod
> # ¡Edita los archivos y cambia "your_password_here" por contraseñas reales!
> ```
>
> 📝 **Nota:** Los tests (`make test`) no requieren configuración y siempre funcionan con H2.

---

## 🛠️ Tecnologías utilizadas

- Java 17
- Spring Boot 3.x
- Spring Data JPA
- PostgreSQL
- Docker & Docker Compose
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

## ⚡ Quick Start

```bash
# 1. Clonar repositorio
git clone <tu-repo>
cd pedidos-ms

# 2. ⚠️ OBLIGATORIO: Configurar variables de entorno
cp .env.dev.example .env.dev
nano .env.dev  # Cambiar "your_dev_password_here" por contraseña real

# 3. Ejecutar (Docker compilará automáticamente)
make up-dev

# O solo tests (no requiere configuración)
make test
```

**🎯 ¡Ya funciona!** Endpoint disponible en: `http://localhost:8080/pedidos/cargar`

---

## 🧪 Cómo ejecutar el proyecto localmente

### Opción 1: Con Docker (Recomendado)

#### Requisitos

- Docker y Docker Compose
- Make (opcional, para usar comandos simplificados)

#### ⚠️ **CONFIGURACIÓN OBLIGATORIA: Variables de entorno**

🔴 **PASO CRÍTICO:** Antes de ejecutar el proyecto, **DEBES** copiar y configurar los archivos de variables de entorno:

```bash
# 1. Copiar plantillas de configuración
cp .env.dev.example .env.dev
cp .env.prod.example .env.prod

# 2. Editar las contraseñas en los archivos copiados
# ⚠️ CAMBIAR "your_dev_password_here" por tu contraseña real
```

**Para desarrollo (`.env.dev`):**

```bash
# Database Configuration
POSTGRES_DB=pedidosdb
POSTGRES_USER=postgres
POSTGRES_PASSWORD=tu_contraseña_dev_aqui   # ⚠️ CAMBIAR ESTO

# Spring Application Configuration
SPRING_PROFILES_ACTIVE=dev
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/pedidosdb
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=tu_contraseña_dev_aqui   # ⚠️ CAMBIAR ESTO
```

**Para producción (`.env.prod`):**

```bash
# Database Configuration
POSTGRES_DB=pedidosdb
POSTGRES_USER=tu_usuario_prod    # ⚠️ CAMBIAR ESTO
POSTGRES_PASSWORD=tu_contraseña_segura_prod   # ⚠️ CAMBIAR ESTO

# Spring Application Configuration
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/pedidosdb
SPRING_DATASOURCE_USERNAME=tu_usuario_prod    # ⚠️ CAMBIAR ESTO
SPRING_DATASOURCE_PASSWORD=tu_contraseña_segura_prod   # ⚠️ CAMBIAR ESTO
```

#### Ejecutar con Makefile

```bash
# ⚠️ IMPORTANTE: Configurar variables de entorno PRIMERO (ver arriba)

# Ejecutar tests (no requiere DB externa)
make test

# Para desarrollo (compila automáticamente en Docker)
make up-dev

# Para producción (compila automáticamente en Docker)
make up-prod

# Compilar JAR localmente (opcional)
make build-local

# Para limpiar contenedores
make clean
```

#### Ejecutar manualmente

```bash
# ⚠️ PASO 1: Configurar variables de entorno (OBLIGATORIO)
cp .env.dev.example .env.dev
# Editar .env.dev y cambiar las contraseñas

# PASO 2: Desarrollo (Docker compilará automáticamente)
docker-compose --env-file .env.dev up --build

# O crear imagen standalone
docker build -t pedidos-ms .
```

> **📝 Nota importante:**
>
> El Dockerfile ahora utiliza **multi-stage build** que:
>
> - ✅ Compila el código fuente automáticamente
> - ✅ No requiere tener el JAR pre-compilado
> - ✅ Optimiza el tamaño de la imagen final
> - ✅ Utiliza cache de dependencias Maven

### Opción 2: Ejecución local con H2 (Sin Docker)

#### 1. Clona el repositorio

```bash
git clone https://github.com/tuusuario/pedidos-ms.git
cd pedidos-ms
```

#### 2. Compila y ejecuta

```bash
# Ejecutar con perfil dev (H2 automática)
./mvnw spring-boot:run

# O ejecutar tests
./mvnw test
```

#### 3. Accede a la consola H2 (opcional)

URL: <http://localhost:8080/h2-console>

- JDBC URL: `jdbc:h2:mem:pedidosdb`
- User: `sa`
- Password: (vacío)

---

## ⚙️ Configuración de entornos

### Variables de entorno requeridas

El proyecto requiere las siguientes variables de entorno para funcionar correctamente:

| Variable                     | Descripción                        | Ejemplo                               |
| ---------------------------- | ---------------------------------- | ------------------------------------- |
| `POSTGRES_DB`                | Nombre de la base de datos         | `pedidosdb`                           |
| `POSTGRES_USER`              | Usuario de PostgreSQL              | `your_db_user`                        |
| `POSTGRES_PASSWORD`          | Contraseña de PostgreSQL           | `your_secure_password`                |
| `SPRING_PROFILES_ACTIVE`     | Perfil activo de Spring            | `dev` o `prod`                        |
| `SPRING_DATASOURCE_URL`      | URL de conexión a la base de datos | `jdbc:postgresql://db:5432/pedidosdb` |
| `SPRING_DATASOURCE_USERNAME` | Usuario para Spring DataSource     | `your_db_user`                        |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña para Spring DataSource  | `your_secure_password`                |

### Archivos de configuración

- **`.env.dev`**: Configuración para desarrollo (PostgreSQL local)
- **`.env.prod`**: Configuración para producción (PostgreSQL con credenciales seguras)
- **`.env`**: Se genera automáticamente al ejecutar `make up-dev` o `make up-prod`

> ⚠️ **Importante**: Los archivos `.env.dev` y `.env.prod` contienen credenciales sensibles y NO deben subirse al repositorio. Asegúrate de que estén en tu `.gitignore`.

### Configuración inicial

1. Copia los archivos de ejemplo:

```bash
cp .env.dev.example .env.dev
cp .env.prod.example .env.prod
```

1. Edita cada archivo con tus credenciales reales
1. Nunca commits estos archivos al repositorio

### Perfiles de Spring

- **`dev`**: Usa PostgreSQL con datos de prueba
- **`prod`**: Usa PostgreSQL con configuración de producción
- **Por defecto (sin Docker)**: Usa H2 en memoria para desarrollo rápido

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

---

## 🐛 Troubleshooting

### ❌ Error: "Failed to configure a DataSource"

**Problema:** No tienes configuradas las variables de entorno.

**Solución:**

```bash
# 1. Verifica que existan los archivos
ls -la .env.*

# 2. Si no existen, cópialos:
cp .env.dev.example .env.dev
cp .env.prod.example .env.prod

# 3. Edita las contraseñas en los archivos:
nano .env.dev  # o tu editor preferido
```

### ❌ Error: "Connection refused" o problemas de DB

**Problema:** Las contraseñas en `.env.dev` o `.env.prod` no están configuradas.

**Solución:**

```bash
# Verificar contenido del archivo
cat .env.dev

# Debe mostrar contraseñas reales, NO "your_password_here"
```

### ❌ Error: "No such file or directory: .env"

**Problema:** El Makefile busca archivos `.env` sin el sufijo.

**Solución:**

```bash
# El Makefile automáticamente copia el correcto:
make up-dev  # copia .env.dev a .env
make up-prod # copia .env.prod a .env
```

### ✅ Tests siempre funcionan

Los tests usan H2 en memoria y no requieren configuración externa.

```bash
make test  # Siempre debe funcionar
```

---

## 🧑 Autor

Josué Patricio

---

## 📝 Licencia

MIT
