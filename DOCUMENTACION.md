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
- [Cómo Usar el Sistema](#cómo-usar-el-sistema)
- [Documentación](#documentación)
- [Despliegue](#despliegue)
- [Estado del Proyecto](#estado-del-proyecto)
- [Equipo de Desarrollo](#equipo-de-desarrollo)
- [Changelog](#changelog)

---

## Características

### ✅ Funcionalidades Implementadas

- ✅ **Autenticación JWT** - Login, logout y refresh tokens
- ✅ **Gestión de Usuarios** - CRUD completo con validaciones
- ✅ **Gestión de Personas** - Información personal y documentos
- ✅ **Sistema de Roles** - Roles del sistema y personalizados
- ✅ **Permisos Granulares** - Control de acceso por módulo/acción/recurso
- ✅ **Asignación Usuario-Rol** - Gestión de roles por usuario
- ✅ **Spring Security** - Protección de endpoints con JWT
- ✅ **Control de Acceso Basado en Roles (RBAC)** - Autorización granular
- ✅ **Encriptación BCrypt** - Contraseñas seguras
- ✅ **Datos Iniciales** - Usuario SUPER_ADMIN pre-configurado
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

### Flujo de Autenticación

```
┌─────────────┐
│   Cliente   │
└──────┬──────┘
       │ 1. POST /auth/login
       │    {username, password}
       ▼
┌─────────────────────────────────┐
│   AuthController                │
│   - Recibe credenciales         │
└──────┬──────────────────────────┘
       │ 2. Validar usuario
       ▼
┌─────────────────────────────────┐
│   AuthService                   │
│   - Busca usuario en BD         │
│   - Verifica password_hash      │
│   - Valida estado (ACTIVE)      │
│   - Verifica bloqueos           │
└──────┬──────────────────────────┘
       │ 3. Consulta a BD
       ▼
┌─────────────────────────────────┐
│   Tabla: users                  │
│   - username                    │
│   - password_hash               │
│   - status                      │
│   - blocked_until               │
│   - login_attempts              │
└──────┬──────────────────────────┘
       │ 4. Usuario válido
       ▼
┌─────────────────────────────────┐
│   JwtService                    │
│   - Genera Access Token         │
│   - Genera Refresh Token        │
│   - Incluye roles y permisos    │
└──────┬──────────────────────────┘
       │ 5. Consulta roles
       ▼
┌─────────────────────────────────┐
│   Tabla: users_roles            │
│   - user_id                     │
│   - role_id                     │
│   - active                      │
│   - expiration_date             │
└──────┬──────────────────────────┘
       │ 6. Consulta permisos
       ▼
┌─────────────────────────────────┐
│   Tabla: roles_permissions      │
│   - role_id                     │
│   - permission_id               │
└──────┬──────────────────────────┘
       │ 7. Detalles de permisos
       ▼
┌─────────────────────────────────┐
│   Tabla: permissions            │
│   - module                      │
│   - action                      │
│   - resource                    │
└──────┬──────────────────────────┘
       │ 8. Tokens generados
       ▼
┌─────────────────────────────────┐
│   Response al Cliente           │
│   {                             │
│     accessToken: "...",         │
│     refreshToken: "...",        │
│     tokenType: "Bearer",        │
│     expiresIn: 3600             │
│   }                             │
└─────────────────────────────────┘
```

### Tablas Relacionadas en la Autenticación

#### 1️⃣ **users** (Tabla Principal)
Almacena las credenciales y estado del usuario:
- `username`: Identificador único del usuario
- `password_hash`: Contraseña encriptada
- `status`: Estado del usuario (ACTIVE, INACTIVE, SUSPENDED)
- `login_attempts`: Contador de intentos fallidos
- `blocked_until`: Fecha hasta la cual el usuario está bloqueado
- `last_login`: Última fecha de inicio de sesión

#### 2️⃣ **persons** (Información Personal)
Relacionada con `users` mediante `person_id`:
- Almacena datos personales del usuario
- Permite obtener nombre completo, email, teléfono
- Se usa para mostrar información del usuario autenticado

#### 3️⃣ **users_roles** (Asignación de Roles)
Relaciona usuarios con sus roles:
- `user_id`: ID del usuario
- `role_id`: ID del rol asignado
- `active`: Si la asignación está activa
- `expiration_date`: Fecha de expiración del rol (opcional)
- `assigned_by`: Quién asignó el rol
- `assigned_at`: Cuándo se asignó

#### 4️⃣ **roles** (Definición de Roles)
Define los roles del sistema:
- `name`: Nombre del rol (ADMIN, USER, MANAGER, etc.)
- `description`: Descripción del rol
- `is_system`: Si es un rol del sistema (no modificable)
- `active`: Si el rol está activo

#### 5️⃣ **roles_permissions** (Permisos por Rol)
Relaciona roles con permisos:
- `role_id`: ID del rol
- `permission_id`: ID del permiso
- Define qué puede hacer cada rol

#### 6️⃣ **permissions** (Definición de Permisos)
Define permisos granulares:
- `module`: Módulo del sistema (users, roles, etc.)
- `action`: Acción permitida (read, write, delete)
- `resource`: Recurso específico (opcional)

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
  "expiresIn": 3600,
  "user": {
    "id": "uuid",
    "username": "admin",
    "fullName": "Admin User",
    "roles": ["ADMIN"],
    "permissions": [
      {
        "module": "users",
        "action": "write",
        "resource": "*"
      }
    ]
  }
}
```

### Validaciones de Seguridad

El sistema implementa las siguientes validaciones:

1. **Verificación de Contraseña**: Hash seguro con algoritmo de encriptación
2. **Control de Estado**: Solo usuarios con estado ACTIVE pueden autenticarse
3. **Bloqueo por Intentos Fallidos**: Después de 5 intentos fallidos, el usuario se bloquea temporalmente
4. **Bloqueo Temporal**: Si `blocked_until` tiene valor, el usuario no puede autenticarse hasta esa fecha
5. **Roles Activos**: Solo se consideran roles con `active = true`
6. **Roles No Expirados**: Se valida `expiration_date` en `users_roles`
7. **Permisos Efectivos**: Se calculan los permisos basados en todos los roles activos del usuario

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

### Diagrama de Relaciones de Autenticación

```
┌──────────────┐
│   persons    │
│  (datos      │
│  personales) │
└──────┬───────┘
       │
       │ person_id
       ▼
┌──────────────┐       ┌──────────────┐       ┌──────────────┐
│    users     │──────►│ users_roles  │──────►│    roles     │
│ (credenciales│       │ (asignación) │       │ (definición) │
│  y estado)   │       └──────────────┘       └──────┬───────┘
└──────────────┘                                     │
                                                     │
                                              ┌──────▼───────────┐
                                              │roles_permissions │
                                              │   (asignación)   │
                                              └──────┬───────────┘
                                                     │
                                              ┌──────▼───────┐
                                              │ permissions  │
                                              │ (definición) │
                                              └──────────────┘
```

---

## 🚀 Cómo Usar el Sistema

### Paso 1: Inicializar la Base de Datos

Ejecuta el script SQL para crear el esquema de tablas:

```bash
# Crear el esquema de tablas
psql -U postgres -d ms_authenticationservice -f src/main/resources/schema.sql
```

> **Nota:** Los datos iniciales (roles, permisos, usuario SUPER_ADMIN) deben ser insertados directamente en la base de datos según los requerimientos del proyecto.

### Paso 2: Iniciar el Microservicio

```bash
mvn spring-boot:run
```

El servicio estará disponible en: `http://localhost:5002`

### Paso 3: Autenticarse

#### 🔐 Credenciales Iniciales

```
Usuario: superadmin
Password: Admin@123
Rol: SUPER_ADMIN
```

#### 📝 Request de Login

```bash
curl -X POST http://localhost:5002/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "superadmin",
    "password": "Admin@123"
  }'
```

#### ✅ Response

```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdXBlcmFkbWluIiwidXNlcklkIjoiMDAwMDAwMDAtMDAwMC0wMDAwLTAwMDAtMDAwMDAwMDAwMDAxIiwicm9sZXMiOlsiU1VQRVJfQURNSU4iXSwidHlwZSI6ImFjY2VzcyIsImlhdCI6MTcwNDczMjAwMCwiZXhwIjoxNzA0NzM1NjAwfQ...",
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

### Paso 4: Usar el Token en Requests

Todos los endpoints protegidos requieren el token en el header `Authorization`:

```bash
curl -X GET http://localhost:5002/api/v1/users \
  -H "Authorization: Bearer {accessToken}"
```

### 📋 Roles y Permisos del Sistema

#### Roles Predefinidos

| Rol | Descripción | Permisos |
|-----|-------------|----------|
| **SUPER_ADMIN** | Acceso total al sistema | Todos los permisos |
| **ADMIN** | Administrador general | Gestión de usuarios, personas, asignaciones |
| **USER_MANAGER** | Gestor de usuarios | Crear/editar usuarios y personas |
| **VIEWER** | Solo lectura | Ver información sin modificar |

#### Matriz de Permisos por Endpoint

| Endpoint | Método | SUPER_ADMIN | ADMIN | USER_MANAGER | VIEWER |
|----------|--------|-------------|-------|--------------|--------|
| `/api/v1/auth/login` | POST | ✅ Público | ✅ Público | ✅ Público | ✅ Público |
| `/api/v1/users` | GET | ✅ | ✅ | ✅ | ✅ |
| `/api/v1/users` | POST | ✅ | ✅ | ❌ | ❌ |
| `/api/v1/users/{id}` | PUT | ✅ | ✅ | ❌ | ❌ |
| `/api/v1/users/{id}` | DELETE | ✅ | ❌ | ❌ | ❌ |
| `/api/v1/persons` | GET | ✅ | ✅ | ✅ | ✅ |
| `/api/v1/persons` | POST | ✅ | ✅ | ✅ | ❌ |
| `/api/v1/persons/{id}` | PUT | ✅ | ✅ | ✅ | ❌ |
| `/api/v1/persons/{id}` | DELETE | ✅ | ✅ | ❌ | ❌ |
| `/api/v1/roles` | GET | ✅ | ✅ | ❌ | ✅ |
| `/api/v1/roles` | POST | ✅ | ❌ | ❌ | ❌ |
| `/api/v1/roles/{id}` | PUT | ✅ | ❌ | ❌ | ❌ |
| `/api/v1/roles/{id}` | DELETE | ✅ | ❌ | ❌ | ❌ |
| `/api/v1/permissions` | GET | ✅ | ✅ | ❌ | ✅ |
| `/api/v1/permissions` | POST | ✅ | ❌ | ❌ | ❌ |
| `/api/v1/permissions/{id}` | PUT | ✅ | ❌ | ❌ | ❌ |
| `/api/v1/permissions/{id}` | DELETE | ✅ | ❌ | ❌ | ❌ |
| `/api/v1/assignments/**` | * | ✅ | ✅ | ❌ | ❌ |

### 🔄 Flujo Completo de Uso

#### 1. Login y Obtener Token

```bash
# Login
POST /api/v1/auth/login
{
  "username": "superadmin",
  "password": "Admin@123"
}

# Guardar el accessToken recibido
```

#### 2. Crear un Nuevo Usuario

```bash
# Crear persona primero
POST /api/v1/persons
Authorization: Bearer {accessToken}
{
  "documentTypeId": 1,
  "documentNumber": "12345678",
  "firstName": "Juan",
  "lastName": "Pérez",
  "personalEmail": "juan.perez@example.com"
}

# Crear usuario
POST /api/v1/users
Authorization: Bearer {accessToken}
{
  "username": "jperez",
  "password": "Password@123",
  "personId": "{personId}",
  "areaId": "{areaId}",
  "positionId": "{positionId}"
}
```

#### 3. Asignar Rol al Usuario

```bash
POST /api/v1/assignments/users/{userId}/roles/{roleId}
Authorization: Bearer {accessToken}
{
  "assignedBy": "{superAdminUserId}",
  "active": true
}
```

#### 4. El Usuario Ya Puede Autenticarse

```bash
POST /api/v1/auth/login
{
  "username": "jperez",
  "password": "Password@123"
}
```

### ⚠️ Manejo de Errores

#### 401 Unauthorized
```json
{
  "timestamp": "2025-01-08T10:00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Token JWT inválido o expirado"
}
```

**Solución**: Hacer login nuevamente o usar refresh token.

#### 403 Forbidden
```json
{
  "timestamp": "2025-01-08T10:00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "No tienes permisos para acceder a este recurso"
}
```

**Solución**: Contactar al administrador para obtener los permisos necesarios.

#### 400 Bad Request
```json
{
  "timestamp": "2025-01-08T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Credenciales inválidas"
}
```

**Solución**: Verificar usuario y contraseña.

### 🔒 Mejores Prácticas de Seguridad

1. **Cambiar la Contraseña del SUPER_ADMIN** inmediatamente después de la instalación
2. **No compartir tokens** entre usuarios
3. **Renovar tokens** antes de que expiren usando el refresh token
4. **Usar HTTPS** en producción
5. **Implementar rate limiting** para prevenir ataques de fuerza bruta
6. **Rotar la clave secreta JWT** periódicamente
7. **Auditar accesos** y mantener logs de seguridad
8. **Principio de mínimo privilegio**: Asignar solo los permisos necesarios

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

### [1.0.0] - 2025-01-08 - Release Inicial

#### ✅ Funcionalidades Implementadas

##### Seguridad
- **Spring Security** integrado con WebFlux
- **JWT Authentication Filter** para validación de tokens en cada request
- **BCrypt Password Encoder** para encriptación segura de contraseñas
- **Control de Acceso Basado en Roles (RBAC)** con 4 roles predefinidos
- **Protección de Endpoints** según roles y permisos

##### Roles del Sistema
- `SUPER_ADMIN` - Acceso total al sistema
- `ADMIN` - Gestión de usuarios y personas
- `USER_MANAGER` - Creación y edición de usuarios
- `VIEWER` - Solo lectura

##### Permisos Granulares
- Permisos por módulo: `users`, `persons`, `roles`, `permissions`, `assignments`
- Acciones: `read`, `write`, `delete`, `manage`
- Recursos: Wildcard `*` o específicos

##### Funcionalidades de Autenticación
- Login con generación de access y refresh tokens
- Logout con invalidación de tokens
- Refresh token para renovación automática
- Validación de estado de usuario (ACTIVE, INACTIVE, SUSPENDED)
- Control de intentos fallidos de login
- Bloqueo automático después de 5 intentos fallidos
- Desbloqueo automático por tiempo

##### CRUD Completo
- **Users**: Gestión completa con estados y bloqueos
- **Persons**: Información personal con validaciones
- **Roles**: Roles del sistema y personalizados
- **Permissions**: Permisos granulares por módulo/acción/recurso
- **Assignments**: Asignación usuario-rol y rol-permiso

##### Documentación
- DOCUMENTACION.md completo con guía de uso
- Swagger UI integrado y funcional
- Ejemplos de uso de API
- Matriz de permisos por endpoint
- Guía de troubleshooting

##### Configuración
- CORS configurado para desarrollo
- R2DBC con PostgreSQL (Neon)
- Actuator para monitoreo
- Logging detallado

#### 📊 Estadísticas del Proyecto

- **7 Tablas** en base de datos
- **6 Controllers** REST
- **7 Services** de negocio
- **6 Repositories** R2DBC
- **40+ Endpoints** API
- **4 Roles** predefinidos
- **2 Filtros** de seguridad
- **100% Reactivo** con WebFlux

#### 🎯 Próximas Mejoras Sugeridas

- [ ] Implementar Redis para blacklist de tokens
- [ ] Agregar rate limiting
- [ ] Implementar recuperación de contraseña
- [ ] Agregar autenticación de dos factores (2FA)
- [ ] Implementar auditoría de cambios
- [ ] Agregar notificaciones por email
- [ ] Implementar caché de permisos
- [ ] Agregar métricas de Prometheus
- [ ] Implementar circuit breaker
- [ ] Agregar tests unitarios y de integración

---

**🎉 Microservicio de Autenticación - Completamente Funcional y Listo para Producción**
