# 🔐 Authentication Service - Microservicio de Autenticación

Microservicio de autenticación y autorización desarrollado con Spring Boot 3.5.6, WebFlux (Reactive), R2DBC y PostgreSQL.

## 📋 Tabla de Contenidos

- [Características](#características)
- [Tecnologías](#tecnologías)
- [Arquitectura](#arquitectura)
- [Configuración](#configuración)
- [Endpoints API](#endpoints-api)
- [Modelo de Datos](#modelo-de-datos)
- [Seguridad](#seguridad)
- [Documentación](#documentación)
- [Despliegue](#despliegue)
- [Estado del Proyecto](#estado-del-proyecto)
- [Equipo de Desarrollo](#equipo-de-desarrollo)

---

## Características

### ✅ Funcionalidades Implementadas

- ✅ **Autenticación JWT** - Login, logout y refresh tokens
- ✅ **Gestión de Usuarios** - CRUD completo con validaciones
- ✅ **Gestión de Personas** - Información personal y documentos
- ✅ **Sistema de Roles** - Roles del sistema y personalizados
- ✅ **Permisos Granulares** - Control de acceso por módulo/acción/recurso
- ✅ **Asignación Usuario-Rol** - Gestión de roles por usuario
- ✅ **Asignación Rol-Permiso** - Gestión de permisos por rol
- ✅ **Validaciones de Negocio** - Duplicados, estados, bloqueos
- ✅ **Documentación Swagger** - API interactiva y documentada
- ✅ **CORS Configurado** - Listo para integración con frontend
- ✅ **Manejo de Excepciones** - Respuestas consistentes y claras
- ✅ **Logging Detallado** - Trazabilidad completa de operaciones

---

## Tecnologías

### Backend
- **Java 17**
- **Spring Boot 3.5.6**
- **Spring WebFlux** (Programación Reactiva)
- **Spring Data R2DBC** (Acceso reactivo a base de datos)
- **PostgreSQL** (Base de datos)
- **JJWT** (JSON Web Tokens)
- **Lombok** (Reducción de código boilerplate)
- **SpringDoc OpenAPI** (Documentación Swagger)

### Base de Datos
- **PostgreSQL 15+** (Neon Database)
- **R2DBC PostgreSQL Driver**

---

## Arquitectura

### Arquitectura de Capas

```
┌─────────────────────────────────────────┐
│         Controllers (REST API)          │
│  - UserController                       │
│  - PersonController                     │
│  - RoleController                       │
│  - PermissionController                 │
│  - AuthController                       │
│  - AssignmentController                 │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│            Services (Lógica)            │
│  - UserService                          │
│  - PersonService                        │
│  - RoleService                          │
│  - PermissionService                    │
│  - AuthService                          │
│  - AssignmentService                    │
│  - JwtService                           │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│       Repositories (Acceso Datos)       │
│  - UserRepository                       │
│  - PersonRepository                     │
│  - RoleRepository                       │
│  - PermissionRepository                 │
│  - UserRoleRepository                   │
│  - RolePermissionRepository             │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│         PostgreSQL Database             │
└─────────────────────────────────────────┘
```

---

## Configuración

### application.yml

```yaml
server:
  port: 8081

spring:
  application:
    name: authentication-service
  
  r2dbc:
    url: r2dbc:postgresql://ep-quiet-sea-a5iqvqxe.us-east-2.aws.neon.tech:5432/ms-authenticationService
    username: ms-authenticationService_owner
    password: your-password
    pool:
      initial-size: 10
      max-size: 20

jwt:
  secret: your-secret-key-here-min-256-bits
  expiration: 3600000  # 1 hora
  refresh-expiration: 86400000  # 24 horas

springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
```

---

## Endpoints API

### 🔐 Authentication

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/auth/login` | Iniciar sesión |
| POST | `/api/v1/auth/logout` | Cerrar sesión |
| POST | `/api/v1/auth/refresh` | Renovar token |

### 👤 Users

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/users` | Crear usuario |
| GET | `/api/v1/users/{id}` | Obtener usuario por ID |
| GET | `/api/v1/users` | Listar todos los usuarios |
| GET | `/api/v1/users/username/{username}` | Buscar por username |
| GET | `/api/v1/users/active` | Listar usuarios activos |
| PUT | `/api/v1/users/{id}` | Actualizar usuario |
| DELETE | `/api/v1/users/{id}` | Eliminar usuario (lógico) |
| PATCH | `/api/v1/users/{id}/activate` | Activar usuario |
| PATCH | `/api/v1/users/{id}/deactivate` | Desactivar usuario |
| PATCH | `/api/v1/users/{id}/block` | Bloquear usuario |
| PATCH | `/api/v1/users/{id}/unblock` | Desbloquear usuario |

### 👥 Persons

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/persons` | Crear persona |
| GET | `/api/v1/persons/{id}` | Obtener persona por ID |
| GET | `/api/v1/persons` | Listar todas las personas |
| GET | `/api/v1/persons/document` | Buscar por documento |
| GET | `/api/v1/persons/email/{email}` | Buscar por email |
| GET | `/api/v1/persons/search` | Búsqueda avanzada |
| PUT | `/api/v1/persons/{id}` | Actualizar persona |
| DELETE | `/api/v1/persons/{id}` | Eliminar persona |

### 🎭 Roles

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/roles` | Crear rol |
| GET | `/api/v1/roles/{id}` | Obtener rol por ID |
| GET | `/api/v1/roles` | Listar todos los roles |
| GET | `/api/v1/roles/active` | Listar roles activos |
| GET | `/api/v1/roles/system` | Listar roles del sistema |
| GET | `/api/v1/roles/name/{name}` | Buscar por nombre |
| PUT | `/api/v1/roles/{id}` | Actualizar rol |
| DELETE | `/api/v1/roles/{id}` | Eliminar rol (lógico) |
| PATCH | `/api/v1/roles/{id}/activate` | Activar rol |
| PATCH | `/api/v1/roles/{id}/deactivate` | Desactivar rol |

### 🔑 Permissions

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/permissions` | Crear permiso |
| GET | `/api/v1/permissions/{id}` | Obtener permiso por ID |
| GET | `/api/v1/permissions` | Listar todos los permisos |
| GET | `/api/v1/permissions/module/{module}` | Permisos por módulo |
| GET | `/api/v1/permissions/search` | Buscar por detalles |
| GET | `/api/v1/permissions/exists` | Verificar existencia |
| PUT | `/api/v1/permissions/{id}` | Actualizar permiso |
| DELETE | `/api/v1/permissions/{id}` | Eliminar permiso |

### 🔗 Assignments

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/assignments/users/{userId}/roles` | Roles de un usuario |
| POST | `/api/v1/assignments/users/{userId}/roles/{roleId}` | Asignar rol a usuario |
| DELETE | `/api/v1/assignments/users/{userId}/roles/{roleId}` | Quitar rol a usuario |
| GET | `/api/v1/assignments/roles/{roleId}/users` | Usuarios con un rol |
| GET | `/api/v1/assignments/roles/{roleId}/permissions` | Permisos de un rol |
| POST | `/api/v1/assignments/roles/{roleId}/permissions/{permissionId}` | Asignar permiso a rol |
| DELETE | `/api/v1/assignments/roles/{roleId}/permissions/{permissionId}` | Quitar permiso a rol |
| GET | `/api/v1/assignments/users/{userId}/permissions` | Permisos efectivos de usuario |

---

## Modelo de Datos

### Tablas Principales

#### 1️⃣ **users** - Usuarios del Sistema
```sql
CREATE TABLE users (
    id              UUID PRIMARY KEY,
    username        VARCHAR(50) UNIQUE NOT NULL,
    password_hash   VARCHAR(500) NOT NULL,
    person_id       UUID NOT NULL,
    area_id         UUID NOT NULL,
    position_id     UUID NOT NULL,
    direct_manager_id UUID,
    status          VARCHAR(20) DEFAULT 'ACTIVE',
    last_login      TIMESTAMP,
    login_attempts  INTEGER DEFAULT 0,
    blocked_until   TIMESTAMP,
    preferences     JSONB,
    created_by      UUID,
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_by      UUID,
    updated_at      TIMESTAMP DEFAULT NOW(),
    version         INTEGER DEFAULT 1
);
```

#### 2️⃣ **persons** - Información Personal
```sql
CREATE TABLE persons (
    id              UUID PRIMARY KEY,
    document_type_id INTEGER NOT NULL,
    document_number VARCHAR(20) NOT NULL,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    middle_name     VARCHAR(100),
    birth_date      DATE,
    gender          CHAR(1),
    personal_phone  VARCHAR(20),
    work_phone      VARCHAR(20),
    personal_email  VARCHAR(200),
    address         TEXT,
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP DEFAULT NOW()
);
```

#### 3️⃣ **roles** - Roles del Sistema
```sql
CREATE TABLE roles (
    id              UUID PRIMARY KEY,
    name            VARCHAR(50) UNIQUE NOT NULL,
    description     TEXT,
    is_system       BOOLEAN DEFAULT false,
    active          BOOLEAN DEFAULT true,
    created_by      UUID,
    created_at      TIMESTAMP DEFAULT NOW()
);
```

#### 4️⃣ **permissions** - Permisos Granulares
```sql
CREATE TABLE permissions (
    id              UUID PRIMARY KEY,
    module          VARCHAR(50) NOT NULL,
    action          VARCHAR(50) NOT NULL,
    resource        VARCHAR(100),
    description     TEXT,
    created_by      UUID,
    created_at      TIMESTAMP DEFAULT NOW(),
    CONSTRAINT uk_permission UNIQUE (module, action, resource)
);
```

#### 5️⃣ **users_roles** - Asignación Usuario-Rol
```sql
CREATE TABLE users_roles (
    user_id         UUID NOT NULL,
    role_id         UUID NOT NULL,
    assigned_by     UUID NOT NULL,
    assigned_at     TIMESTAMP DEFAULT NOW(),
    expiration_date DATE,
    active          BOOLEAN DEFAULT true,
    PRIMARY KEY (user_id, role_id)
);
```

#### 6️⃣ **roles_permissions** - Asignación Rol-Permiso
```sql
CREATE TABLE roles_permissions (
    role_id         UUID NOT NULL,
    permission_id   UUID NOT NULL,
    created_at      TIMESTAMP DEFAULT NOW(),
    PRIMARY KEY (role_id, permission_id)
);
```

#### 7️⃣ **document_types** - Tipos de Documentos
```sql
CREATE TABLE document_types (
    id              INTEGER PRIMARY KEY,
    code            VARCHAR(5) UNIQUE NOT NULL,
    description     VARCHAR(50) NOT NULL,
    length          INTEGER NOT NULL,
    active          BOOLEAN DEFAULT true
);
```

### Diagrama de Relaciones

```
┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│   persons   │◄──────│    users    │──────►│ users_roles │
└─────────────┘       └─────────────┘       └─────────────┘
                             │                      │
                             │                      ▼
                             │              ┌─────────────┐
                             │              │    roles    │
                             │              └─────────────┘
                             │                      │
                             ▼                      ▼
                      ┌─────────────┐       ┌──────────────────┐
                      │document_types│       │roles_permissions │
                      └─────────────┘       └──────────────────┘
                                                     │
                                                     ▼
                                             ┌─────────────┐
                                             │ permissions │
                                             └─────────────┘
```

---

## Seguridad

### Autenticación JWT

El sistema utiliza JSON Web Tokens (JWT) para la autenticación:

1. **Login**: El usuario envía credenciales y recibe un access token y refresh token
2. **Access Token**: Token de corta duración (1 hora) para acceder a recursos protegidos
3. **Refresh Token**: Token de larga duración (24 horas) para renovar el access token
4. **Logout**: Invalida los tokens del usuario

### Ejemplo de Login

**Request:**
```json
POST /api/v1/auth/login
{
  "username": "admin",
  "password": "password123"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

### Sistema de Permisos

Los permisos se definen con tres componentes:

- **Module**: Módulo del sistema (users, roles, permissions, etc.)
- **Action**: Acción a realizar (read, write, delete, etc.)
- **Resource**: Recurso específico (opcional)

**Ejemplo:**
```
module: "users"
action: "write"
resource: "profile"
```

---

## Documentación

### Swagger UI

La documentación interactiva de la API está disponible en:

```
http://localhost:5002/webjars/swagger-ui/index.html#/
```

### OpenAPI Specification

El archivo de especificación OpenAPI está disponible en:

```
http://localhost:5002/api-docs
```

---

## Despliegue

### Docker

```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Construir imagen:**
```bash
docker build -t authentication-service .
```

**Ejecutar contenedor:**
```bash
docker run -p 8081:8081 authentication-service
```

---

## Estado del Proyecto

### ✅ Microservicios Completados

| Microservicio | Estado | Descripción |
|---------------|--------|-------------|
| ✅ **Authentication Service** | **COMPLETADO** | Autenticación, usuarios, roles y permisos |
| ⏳ Areas Service | En desarrollo | Gestión de áreas organizacionales |
| ⏳ Positions Service | En desarrollo | Gestión de cargos y posiciones |

### 🎯 Funcionalidades por Módulo

#### ✅ Users (100%)
- ✅ CRUD completo
- ✅ Gestión de estados (activo/inactivo/suspendido)
- ✅ Bloqueo/desbloqueo de usuarios
- ✅ Control de intentos de login
- ✅ Búsquedas avanzadas

#### ✅ Persons (100%)
- ✅ CRUD completo
- ✅ Validación de documentos
- ✅ Búsqueda por documento/email
- ✅ Campos calculados (nombre completo, edad)

#### ✅ Roles (100%)
- ✅ CRUD completo
- ✅ Roles del sistema vs personalizados
- ✅ Gestión de estado activo/inactivo
- ✅ Búsquedas y filtros

#### ✅ Permissions (100%)
- ✅ CRUD completo
- ✅ Permisos granulares por módulo/acción/recurso
- ✅ Búsquedas por módulo
- ✅ Validación de duplicados

#### ✅ Authentication (100%)
- ✅ Login con JWT
- ✅ Logout
- ✅ Refresh token
- ✅ Validación de tokens

#### ✅ Assignments (100%)
- ✅ Asignación usuario-rol
- ✅ Asignación rol-permiso
- ✅ Consultas de permisos efectivos
- ✅ Validaciones de asignaciones

---

## Equipo de Desarrollo

- **Backend Developers**: Luis Mencia, Henry Lunazco, Kimberling Lipa
- **Database Design**: Henry Lunazco
- **API Documentation**: Luis Mencia

---

## 📝 Licencia

Este proyecto es privado y confidencial.

---

## 🔄 Changelog

### Version 1.0.0 (2025-01-08)
- ✅ Implementación completa del microservicio de autenticación
- ✅ CRUD de usuarios, personas, roles y permisos
- ✅ Sistema de autenticación JWT
- ✅ Sistema de asignaciones usuario-rol y rol-permiso
- ✅ Documentación Swagger completa
- ✅ Configuración CORS
- ✅ Manejo de excepciones centralizado

---

**🎉 Microservicio de Autenticación - Completamente Funcional y Listo para Producción**
