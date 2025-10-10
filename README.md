# 🔐 API de Autenticación - Guía para Frontend

## 📋 Información General

**Microservicio:** Authentication Service  
**URL Base:** `http://localhost:5002`  
**Versión API:** v1  
**Tecnología:** Spring Boot 3.5.6 + WebFlux (Reactivo)  
**Base de Datos:** PostgreSQL (Neon Cloud)

---

## 🚀 Inicio Rápido

### Credenciales de Prueba

```
Usuario: superadmin
Contraseña: Admin@123
```

### Flujo Básico de Autenticación

1. **Login** → Obtener tokens
2. **Usar Access Token** en todas las peticiones
3. **Refresh Token** cuando expire el access token
4. **Logout** al cerrar sesión

---

## 🔑 Autenticación

### 1. Login

Autentica un usuario y obtiene los tokens JWT.

**Endpoint:** `POST /api/v1/auth/login`

**Request:**
```json
{
  "username": "superadmin",
  "password": "Admin@123"
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "userId": "00000000-0000-0000-0000-000000000001",
  "username": "superadmin",
  "status": "ACTIVE",
  "roles": ["SUPER_ADMIN"],
  "loginTime": "2025-01-08T10:00:00"
}
```

**Ejemplo con Fetch:**
```javascript
const login = async (username, password) => {
  const response = await fetch('http://localhost:5002/api/v1/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ username, password })
  });
  
  if (!response.ok) {
    throw new Error('Credenciales inválidas');
  }
  
  const data = await response.json();
  
  // Guardar tokens en localStorage
  localStorage.setItem('accessToken', data.accessToken);
  localStorage.setItem('refreshToken', data.refreshToken);
  localStorage.setItem('user', JSON.stringify({
    userId: data.userId,
    username: data.username,
    roles: data.roles
  }));
  
  return data;
};
```

**Ejemplo con Axios:**
```javascript
import axios from 'axios';

const login = async (username, password) => {
  try {
    const response = await axios.post('http://localhost:5002/api/v1/auth/login', {
      username,
      password
    });
    
    const { accessToken, refreshToken, userId, username, roles } = response.data;
    
    // Guardar tokens
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
    localStorage.setItem('user', JSON.stringify({ userId, username, roles }));
    
    return response.data;
  } catch (error) {
    console.error('Error en login:', error.response?.data);
    throw error;
  }
};
```

---

### 2. Logout

Invalida el token actual del usuario.

**Endpoint:** `POST /api/v1/auth/logout`

**Headers:**
```
Authorization: Bearer {accessToken}
```

**Response (200 OK):**
```
"Logout exitoso"
```

**Ejemplo:**
```javascript
const logout = async () => {
  const token = localStorage.getItem('accessToken');
  
  await fetch('http://localhost:5002/api/v1/auth/logout', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  // Limpiar localStorage
  localStorage.removeItem('accessToken');
  localStorage.removeItem('refreshToken');
  localStorage.removeItem('user');
};
```

---

### 3. Refresh Token

Renueva el access token usando el refresh token.

**Endpoint:** `POST /api/v1/auth/refresh`

**Request:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9..."
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

**Ejemplo:**
```javascript
const refreshAccessToken = async () => {
  const refreshToken = localStorage.getItem('refreshToken');
  
  const response = await fetch('http://localhost:5002/api/v1/auth/refresh', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ refreshToken })
  });
  
  if (!response.ok) {
    // Refresh token expirado, redirigir a login
    window.location.href = '/login';
    return;
  }
  
  const data = await response.json();
  localStorage.setItem('accessToken', data.accessToken);
  
  return data.accessToken;
};
```

---

### 4. Validar Token

Verifica si un token es válido.

**Endpoint:** `POST /api/v1/auth/validate`

**Headers:**
```
Authorization: Bearer {accessToken}
```

**Response (200 OK):**
```json
true
```

---

## 👤 Gestión de Usuarios

### 1. Crear Usuario

**Endpoint:** `POST /api/v1/users`

**Headers:**
```
Authorization: Bearer {accessToken}
Content-Type: application/json
```

**Request:**
```json
{
  "username": "jperez",
  "password": "Password@123",
  "personId": "uuid-de-persona",
  "areaId": "uuid-de-area",
  "positionId": "uuid-de-posicion",
  "directManagerId": "uuid-de-manager",
  "status": "ACTIVE",
  "createdBy": "uuid-usuario-creador"
}
```

**Response (201 Created):**
```json
{
  "id": "uuid-generado",
  "username": "jperez",
  "personId": "uuid-de-persona",
  "areaId": "uuid-de-area",
  "positionId": "uuid-de-posicion",
  "directManagerId": "uuid-de-manager",
  "status": "ACTIVE",
  "loginAttempts": 0,
  "createdAt": "2025-01-08T10:00:00",
  "version": 1
}
```

**Ejemplo:**
```javascript
const createUser = async (userData) => {
  const token = localStorage.getItem('accessToken');
  
  const response = await fetch('http://localhost:5002/api/v1/users', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(userData)
  });
  
  return await response.json();
};
```

---

### 2. Obtener Todos los Usuarios

**Endpoint:** `GET /api/v1/users`

**Headers:**
```
Authorization: Bearer {accessToken}
```

**Response (200 OK):**
```json
[
  {
    "id": "uuid",
    "username": "jperez",
    "personId": "uuid",
    "status": "ACTIVE",
    "lastLogin": "2025-01-08T10:00:00",
    "createdAt": "2025-01-08T09:00:00"
  }
]
```

**Ejemplo:**
```javascript
const getAllUsers = async () => {
  const token = localStorage.getItem('accessToken');
  
  const response = await fetch('http://localhost:5002/api/v1/users', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  return await response.json();
};
```

---

### 3. Obtener Usuario por ID

**Endpoint:** `GET /api/v1/users/{id}`

**Ejemplo:**
```javascript
const getUserById = async (userId) => {
  const token = localStorage.getItem('accessToken');
  
  const response = await fetch(`http://localhost:5002/api/v1/users/${userId}`, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  return await response.json();
};
```

---

### 4. Obtener Usuario por Username

**Endpoint:** `GET /api/v1/users/username/{username}`

---

### 5. Obtener Usuarios Activos

**Endpoint:** `GET /api/v1/users/active`

---

### 6. Obtener Usuarios Inactivos

**Endpoint:** `GET /api/v1/users/inactive`

---

### 7. Obtener Usuarios Suspendidos

**Endpoint:** `GET /api/v1/users/suspended`

---

### 8. Actualizar Usuario

**Endpoint:** `PUT /api/v1/users/{id}`

**Request:**
```json
{
  "username": "jperez",
  "personId": "uuid",
  "areaId": "uuid",
  "positionId": "uuid",
  "status": "ACTIVE",
  "updatedBy": "uuid-usuario-actualizador"
}
```

---

### 9. Cambiar Estado de Usuario

**Endpoint:** `PATCH /api/v1/users/{id}/status/{status}?updatedBy={uuid}`

**Parámetros:**
- `status`: ACTIVE, INACTIVE, SUSPENDED

**Ejemplo:**
```javascript
const changeUserStatus = async (userId, status, updatedBy) => {
  const token = localStorage.getItem('accessToken');
  
  const response = await fetch(
    `http://localhost:5002/api/v1/users/${userId}/status/${status}?updatedBy=${updatedBy}`,
    {
      method: 'PATCH',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    }
  );
  
  return await response.json();
};
```

---

### 10. Suspender Usuario

**Endpoint:** `PATCH /api/v1/users/{id}/suspend?updatedBy={uuid}`

---

### 11. Bloquear Usuario

**Endpoint:** `PATCH /api/v1/users/{id}/block`

---

### 12. Desbloquear Usuario

**Endpoint:** `PATCH /api/v1/users/{id}/unblock`

---

### 13. Eliminar Usuario (Lógico)

**Endpoint:** `DELETE /api/v1/users/{id}?updatedBy={uuid}`

---

### 14. Restaurar Usuario

**Endpoint:** `PATCH /api/v1/users/{id}/restore?updatedBy={uuid}`

---

## 👥 Gestión de Personas

### 1. Crear Persona

**Endpoint:** `POST /api/v1/persons`

**Request:**
```json
{
  "documentTypeId": 1,
  "documentNumber": "12345678",
  "firstName": "Juan",
  "lastName": "Pérez",
  "middleName": "Carlos",
  "birthDate": "1990-05-15",
  "gender": "M",
  "personalPhone": "+51987654321",
  "workPhone": "+5112345678",
  "personalEmail": "juan.perez@email.com",
  "address": "Av. Principal 123, Lima"
}
```

**Response (201 Created):**
```json
{
  "id": "uuid-generado",
  "documentTypeId": 1,
  "documentNumber": "12345678",
  "firstName": "Juan",
  "lastName": "Pérez",
  "middleName": "Carlos",
  "fullName": "Juan Carlos Pérez",
  "birthDate": "1990-05-15",
  "age": 34,
  "gender": "M",
  "personalPhone": "+51987654321",
  "workPhone": "+5112345678",
  "personalEmail": "juan.perez@email.com",
  "address": "Av. Principal 123, Lima",
  "createdAt": "2025-01-08T10:00:00"
}
```

---

### 2. Obtener Todas las Personas

**Endpoint:** `GET /api/v1/persons`

---

### 3. Obtener Persona por ID

**Endpoint:** `GET /api/v1/persons/{id}`

---

### 4. Obtener Persona por Documento

**Endpoint:** `GET /api/v1/persons/document/{documentTypeId}/{documentNumber}`

**Ejemplo:**
```javascript
const getPersonByDocument = async (documentTypeId, documentNumber) => {
  const token = localStorage.getItem('accessToken');
  
  const response = await fetch(
    `http://localhost:5002/api/v1/persons/document/${documentTypeId}/${documentNumber}`,
    {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    }
  );
  
  return await response.json();
};
```

---

### 5. Obtener Persona por Email

**Endpoint:** `GET /api/v1/persons/email/{email}`

---

### 6. Buscar Personas por Nombre

**Endpoint:** `GET /api/v1/persons/search/name/{name}`

---

### 7. Actualizar Persona

**Endpoint:** `PUT /api/v1/persons/{id}`

---

### 8. Eliminar Persona

**Endpoint:** `DELETE /api/v1/persons/{id}`

---

## 🎭 Gestión de Roles

### 1. Crear Rol

**Endpoint:** `POST /api/v1/roles`

**Request:**
```json
{
  "name": "MANAGER",
  "description": "Rol de gerente con permisos de gestión",
  "isSystem": false,
  "active": true
}
```

---

### 2. Obtener Todos los Roles

**Endpoint:** `GET /api/v1/roles`

---

### 3. Obtener Roles Activos

**Endpoint:** `GET /api/v1/roles/active`

---

### 4. Obtener Rol por ID

**Endpoint:** `GET /api/v1/roles/{id}`

---

### 5. Obtener Rol por Nombre

**Endpoint:** `GET /api/v1/roles/name/{name}`

---

### 6. Actualizar Rol

**Endpoint:** `PUT /api/v1/roles/{id}`

---

### 7. Eliminar Rol (Lógico)

**Endpoint:** `DELETE /api/v1/roles/{id}`

---

### 8. Restaurar Rol

**Endpoint:** `PATCH /api/v1/roles/{id}/restore`

---

## 🔑 Gestión de Permisos

### 1. Crear Permiso

**Endpoint:** `POST /api/v1/permissions`

**Request:**
```json
{
  "module": "users",
  "action": "write",
  "resource": "profile",
  "description": "Permiso para editar perfiles de usuario"
}
```

---

### 2. Obtener Todos los Permisos

**Endpoint:** `GET /api/v1/permissions`

---

### 3. Obtener Permiso por ID

**Endpoint:** `GET /api/v1/permissions/{id}`

---

### 4. Obtener Permisos por Módulo

**Endpoint:** `GET /api/v1/permissions/module/{module}`

**Ejemplo:**
```javascript
const getPermissionsByModule = async (module) => {
  const token = localStorage.getItem('accessToken');
  
  const response = await fetch(
    `http://localhost:5002/api/v1/permissions/module/${module}`,
    {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    }
  );
  
  return await response.json();
};
```

---

### 5. Buscar Permiso por Detalles

**Endpoint:** `GET /api/v1/permissions/search?module={module}&action={action}&resource={resource}`

---

### 6. Verificar Existencia de Permiso

**Endpoint:** `GET /api/v1/permissions/exists?module={module}&action={action}&resource={resource}`

---

## 🔗 Asignaciones (Usuario-Rol y Rol-Permiso)

### 1. Obtener Roles de un Usuario

**Endpoint:** `GET /api/v1/users/{userId}/roles`

**Response:**
```json
[
  {
    "userId": "uuid",
    "roleId": "uuid",
    "roleName": "ADMIN",
    "assignedBy": "uuid",
    "assignedAt": "2025-01-08T10:00:00",
    "active": true
  }
]
```

---

### 2. Asignar Rol a Usuario

**Endpoint:** `POST /api/v1/users/{userId}/roles/{roleId}`

**Request:**
```json
{
  "assignedBy": "uuid-usuario-asignador",
  "expirationDate": "2025-12-31",
  "active": true
}
```

**Ejemplo:**
```javascript
const assignRoleToUser = async (userId, roleId, assignedBy) => {
  const token = localStorage.getItem('accessToken');
  
  const response = await fetch(
    `http://localhost:5002/api/v1/users/${userId}/roles/${roleId}`,
    {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        assignedBy,
        active: true
      })
    }
  );
  
  return await response.json();
};
```

---

### 3. Quitar Rol de Usuario

**Endpoint:** `DELETE /api/v1/users/{userId}/roles/{roleId}`

---

### 4. Obtener Usuarios con un Rol

**Endpoint:** `GET /api/v1/roles/{roleId}/users`

---

### 5. Obtener Permisos de un Rol

**Endpoint:** `GET /api/v1/roles/{roleId}/permissions`

---

### 6. Asignar Permiso a Rol

**Endpoint:** `POST /api/v1/roles/{roleId}/permissions/{permissionId}`

---

### 7. Quitar Permiso de Rol

**Endpoint:** `DELETE /api/v1/roles/{roleId}/permissions/{permissionId}`

---

### 8. Obtener Permisos Efectivos de un Usuario

**Endpoint:** `GET /api/v1/users/{userId}/effective-permissions`

**Response:**
```json
[
  {
    "roleId": "uuid",
    "roleName": "ADMIN",
    "permissionId": "uuid",
    "module": "users",
    "action": "write",
    "resource": "*",
    "description": "Permiso para gestionar usuarios"
  }
]
```

---

### 9. Verificar si Usuario Tiene Permiso

**Endpoint:** `GET /api/v1/users/{userId}/has-permission?module={module}&action={action}&resource={resource}`

**Response:**
```json
true
```

---

### 10. Verificar si Usuario Tiene Rol

**Endpoint:** `GET /api/v1/users/{userId}/has-role/{roleId}`

---

## 🛠️ Utilidades para Frontend

### Interceptor de Axios con Refresh Token

```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:5002/api/v1'
});

// Interceptor para agregar token a todas las peticiones
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Interceptor para manejar errores 401 y renovar token
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      
      try {
        const refreshToken = localStorage.getItem('refreshToken');
        const response = await axios.post(
          'http://localhost:5002/api/v1/auth/refresh',
          { refreshToken }
        );
        
        const { accessToken } = response.data;
        localStorage.setItem('accessToken', accessToken);
        
        originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        return api(originalRequest);
      } catch (refreshError) {
        // Refresh token expirado, redirigir a login
        localStorage.clear();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }
    
    return Promise.reject(error);
  }
);

export default api;
```

---

### Hook de React para Autenticación

```javascript
import { useState, useEffect, createContext, useContext } from 'react';
import api from './api';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      setUser(JSON.parse(storedUser));
    }
    setLoading(false);
  }, []);

  const login = async (username, password) => {
    const response = await api.post('/auth/login', { username, password });
    const { accessToken, refreshToken, userId, username: user, roles } = response.data;
    
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
    localStorage.setItem('user', JSON.stringify({ userId, username: user, roles }));
    
    setUser({ userId, username: user, roles });
    return response.data;
  };

  const logout = async () => {
    try {
      await api.post('/auth/logout');
    } finally {
      localStorage.clear();
      setUser(null);
    }
  };

  const hasRole = (role) => {
    return user?.roles?.includes(role) || false;
  };

  const hasAnyRole = (roles) => {
    return roles.some(role => user?.roles?.includes(role)) || false;
  };

  return (
    <AuthContext.Provider value={{ user, login, logout, hasRole, hasAnyRole, loading }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth debe usarse dentro de AuthProvider');
  }
  return context;
};
```

---

### Componente de Ruta Protegida (React)

```javascript
import { Navigate } from 'react-router-dom';
import { useAuth } from './AuthContext';

const ProtectedRoute = ({ children, roles = [] }) => {
  const { user, loading, hasAnyRole } = useAuth();

  if (loading) {
    return <div>Cargando...</div>;
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (roles.length > 0 && !hasAnyRole(roles)) {
    return <Navigate to="/unauthorized" replace />;
  }

  return children;
};

export default ProtectedRoute;
```

**Uso:**
```javascript
<Route 
  path="/admin" 
  element={
    <ProtectedRoute roles={['ADMIN', 'SUPER_ADMIN']}>
      <AdminPanel />
    </ProtectedRoute>
  } 
/>
```

---

## 📊 Códigos de Estado HTTP

| Código | Descripción |
|--------|-------------|
| 200 | OK - Petición exitosa |
| 201 | Created - Recurso creado exitosamente |
| 204 | No Content - Operación exitosa sin contenido |
| 400 | Bad Request - Datos inválidos |
| 401 | Unauthorized - Token inválido o expirado |
| 403 | Forbidden - Sin permisos para el recurso |
| 404 | Not Found - Recurso no encontrado |
| 409 | Conflict - Recurso duplicado |
| 423 | Locked - Usuario bloqueado |
| 500 | Internal Server Error - Error del servidor |

---

## ⚠️ Manejo de Errores

### Estructura de Error

```json
{
  "timestamp": "2025-01-08T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Descripción del error",
  "path": "/api/v1/users"
}
```

### Ejemplo de Manejo de Errores

```javascript
const handleApiError = (error) => {
  if (error.response) {
    // Error de respuesta del servidor
    const { status, data } = error.response;
    
    switch (status) {
      case 400:
        console.error('Datos inválidos:', data.message);
        break;
      case 401:
        console.error('No autorizado, redirigiendo a login...');
        window.location.href = '/login';
        break;
      case 403:
        console.error('Sin permisos:', data.message);
        break;
      case 404:
        console.error('Recurso no encontrado:', data.message);
        break;
      case 409:
        console.error('Conflicto:', data.message);
        break;
      case 423:
        console.error('Usuario bloqueado:', data.message);
        break;
      default:
        console.error('Error del servidor:', data.message);
    }
  } else if (error.request) {
    // Error de red
    console.error('Error de conexión con el servidor');
  } else {
    console.error('Error:', error.message);
  }
};
```

---

## 🔒 Seguridad y Mejores Prácticas

### 1. Almacenamiento de Tokens

```javascript
// ✅ CORRECTO - Usar localStorage para SPAs
localStorage.setItem('accessToken', token);

// ❌ EVITAR - No almacenar en cookies sin httpOnly
document.cookie = `token=${token}`;
```

### 2. Renovación Automática de Tokens

```javascript
// Renovar token 5 minutos antes de que expire
const scheduleTokenRefresh = (expiresIn) => {
  const refreshTime = (expiresIn - 300) * 1000; // 5 min antes
  
  setTimeout(async () => {
    try {
      await refreshAccessToken();
    } catch (error) {
      console.error('Error al renovar token:', error);
      window.location.href = '/login';
    }
  }, refreshTime);
};
```

### 3. Validación de Permisos en Frontend

```javascript
const canEditUser = (user, targetUserId) => {
  // Validar en frontend, pero SIEMPRE validar en backend
  if (user.roles.includes('SUPER_ADMIN')) return true;
  if (user.roles.includes('ADMIN')) return true;
  if (user.userId === targetUserId) return true; // Editar propio perfil
  return false;
};
```

### 4. Timeout de Sesión por Inactividad

```javascript
let inactivityTimer;

const resetInactivityTimer = () => {
  clearTimeout(inactivityTimer);
  
  inactivityTimer = setTimeout(() => {
    alert('Sesión expirada por inactividad');
    logout();
  }, 30 * 60 * 1000); // 30 minutos
};

// Escuchar eventos de actividad
document.addEventListener('mousemove', resetInactivityTimer);
document.addEventListener('keypress', resetInactivityTimer);
```

---

## 📚 Documentación Adicional

### Swagger UI

Accede a la documentación interactiva en:

```
http://localhost:5002/webjars/swagger-ui/index.html
```

### OpenAPI Specification

```
http://localhost:5002/api-docs
```

---

## 🐛 Troubleshooting

### Error: CORS

Si recibes errores de CORS, verifica que el backend tenga configurado:

```yaml
# application.yml
spring:
  webflux:
    cors:
      allowed-origins: "http://localhost:3000"
      allowed-methods: "*"
      allowed-headers: "*"
```

### Error: Token Expirado

```javascript
// Verificar si el token está expirado antes de hacer peticiones
const isTokenExpired = () => {
  const token = localStorage.getItem('accessToken');
  if (!token) return true;
  
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.exp * 1000 < Date.now();
  } catch {
    return true;
  }
};
```

### Error: Usuario Bloqueado (423)

```javascript
if (error.response?.status === 423) {
  alert('Tu cuenta ha sido bloqueada. Contacta al administrador.');
  logout();
}
```

---

## 📞 Soporte

Para más información, consulta la documentación completa en `DOCUMENTACION.md`.

**Equipo de Desarrollo:**
- Luis Mencia
- Henry Lunazco
- Kimberling Lipa

---

## 📝 Changelog

### Versión 1.0.0 (2025-01-08)
- ✅ API REST completa con 40+ endpoints
- ✅ Autenticación JWT con refresh tokens
- ✅ Gestión de usuarios, personas, roles y permisos
- ✅ Sistema de asignaciones usuario-rol y rol-permiso
- ✅ Documentación Swagger integrada
- ✅ CORS configurado para desarrollo

---

**🎉 ¡Listo para integrar con tu frontend!**
