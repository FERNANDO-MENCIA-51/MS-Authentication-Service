# 🔐 Authentication Service - Microservicio Reactivo

## 📋 Descripción
Microservicio de autenticación desarrollado con **Spring WebFlux** (programación reactiva) y **PostgreSQL** como base de datos. Este servicio maneja la autenticación, autorización y gestión de usuarios de forma asíncrona y no bloqueante.

## 🏗️ Arquitectura del Proyecto

```
AuthenticationService/
├── 📁 src/main/java/edu/pe/vallegrande/AuthenticationService/
│   ├── 📁 config/          # Configuraciones (R2DBC, Security, etc.)
│   ├── 📁 controller/      # Controladores REST reactivos
│   ├── 📁 service/         # Lógica de negocio
│   ├── 📁 repository/      # Repositorios R2DBC
│   ├── 📁 model/          # Entidades y DTOs
│   ├── 📁 security/       # Configuración de seguridad
│   └── 📁 exception/      # Manejo de excepciones
├── 📁 src/main/resources/
│   ├── application.yml    # Configuración de la aplicación
│   └── schema.sql        # Scripts de base de datos
└── 📄 pom.xml           # Dependencias Maven
```

## 🛠️ Tecnologías y Dependencias

### 🔧 Framework Principal
- **Spring Boot 3.5.6** - Framework base
- **Spring WebFlux** - Programación reactiva para APIs REST
- **Spring Data R2DBC** - Acceso reactivo a base de datos

### 🗄️ Base de Datos
- **PostgreSQL 17** - Base de datos principal
- **Neon PostgreSQL** - Servicio de base de datos en la nube
- **R2DBC PostgreSQL Driver** - Driver reactivo para PostgreSQL

### 📚 Utilidades
- **Lombok** - Reducción de código boilerplate
- **SpringDoc OpenAPI** - Documentación automática de APIs (Swagger)

### 🧪 Testing
- **Spring Boot Test** - Framework de testing
- **Reactor Test** - Testing para código reactivo

## 🧠 Orden de Creación del Microservicio

### 1️⃣ **Roles** 
```sql
CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    is_system BOOLEAN DEFAULT false,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT NOW()
);
```
- 🎭 Definir los roles del sistema (ADMIN, USER, MANAGER, etc.)
- 🔒 Roles de sistema vs roles personalizados

### 2️⃣ **Permisos**
```sql
CREATE TABLE permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    module VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    resource VARCHAR(100),
    description TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);
```
- 🛡️ Definir permisos granulares (READ, WRITE, DELETE)
- 📦 Organizar por módulos y recursos

### 3️⃣ **Personas**
```sql
CREATE TABLE persons (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_type_id INTEGER NOT NULL,
    document_number VARCHAR(20) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    -- ... más campos
);
```
- 👤 Información personal básica
- 📄 Gestión de tipos de documento

### 4️⃣ **Usuarios**
```sql
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(500) NOT NULL,
    person_id UUID NOT NULL,
    -- ... más campos
);
```
- 🔑 Credenciales de acceso
- 🔗 Vinculación con información personal

### 5️⃣ **Usuarios Globales**
```sql
CREATE TABLE users_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    assigned_by UUID NOT NULL,
    -- ... más campos
);
```
- 🌐 Asignación de roles a usuarios
- ⏰ Control de expiración y auditoría

## 🚀 Configuración y Ejecución

### 📋 Prerrequisitos
- ☕ Java 17+
- 🔧 Maven 3.6+
- 🐘 PostgreSQL 17 (Neon)

### ⚙️ Configuración
El archivo `application.yml` está configurado para:
- 🌐 **Puerto**: 5002
- 🗄️ **Base de datos**: Neon PostgreSQL
- 📖 **Swagger UI**: http://localhost:5002/swagger-ui.html
- 🔍 **API Docs**: http://localhost:5002/api-docs

### 🏃‍♂️ Ejecutar la aplicación
```bash
# Compilar el proyecto
mvn clean compile

# Ejecutar la aplicación
mvn spring-boot:run

# O ejecutar el JAR
mvn clean package
java -jar target/AuthenticationService-0.0.1-SNAPSHOT.jar
```

## 📡 Endpoints Principales

### 🔐 Autenticación
- `POST /api/v1/auth/login` - Iniciar sesión
- `POST /api/v1/auth/logout` - Cerrar sesión
- `POST /api/v1/auth/refresh` - Renovar token

### 👥 Gestión de Usuarios
- `GET /api/v1/users` - Listar usuarios
- `POST /api/v1/users` - Crear usuario
- `PUT /api/v1/users/{id}` - Actualizar usuario
- `DELETE /api/v1/users/{id}` - Eliminar usuario

### 🎭 Gestión de Roles
- `GET /api/v1/roles` - Listar roles
- `POST /api/v1/roles` - Crear rol
- `PUT /api/v1/roles/{id}` - Actualizar rol

### 🛡️ Gestión de Permisos
- `GET /api/v1/permissions` - Listar permisos
- `POST /api/v1/permissions` - Crear permiso

## 🔒 Modelo de Seguridad

### 🎯 Características
- 🔐 **Autenticación JWT** - Tokens seguros
- 🛡️ **Autorización basada en roles** - Control granular
- 🔄 **Programación reactiva** - Alto rendimiento
- 📊 **Auditoría completa** - Trazabilidad de cambios

### 🗂️ Estructura de Permisos
```
module:action:resource
Ejemplo: USER:READ:PROFILE
         USER:WRITE:PASSWORD
         ADMIN:DELETE:USER
```

## 📊 Base de Datos

### 🔗 Relaciones Principales
- `users` ↔ `persons` (1:1)
- `users` ↔ `roles` (N:M) via `users_roles`
- `roles` ↔ `permissions` (N:M) via `roles_permissions`

### 🔑 Características
- 🆔 **UUIDs** como claves primarias
- ⏰ **Timestamps** automáticos
- 🔒 **Constraints** de integridad
- 📝 **Auditoría** integrada

## 🧪 Testing

```bash
# Ejecutar tests
mvn test

# Tests con cobertura
mvn test jacoco:report
```

## 📖 Documentación API

Una vez ejecutada la aplicación, accede a:
- **Swagger UI**: http://localhost:5002/swagger-ui.html
- **OpenAPI JSON**: http://localhost:5002/api-docs

## 🤝 Contribución

1. 🍴 Fork el proyecto
2. 🌿 Crear rama feature (`git checkout -b feature/nueva-funcionalidad`)
3. 💾 Commit cambios (`git commit -am 'Agregar nueva funcionalidad'`)
4. 📤 Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. 🔄 Crear Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para detalles.

---

🚀 **¡Listo para desarrollar tu microservicio de autenticación reactivo!**