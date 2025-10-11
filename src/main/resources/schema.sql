CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
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
    preferences     JSONB DEFAULT '{}'::jsonb,
    created_by      UUID,
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_by      UUID,
    updated_at      TIMESTAMP DEFAULT NOW(),
    version         INTEGER DEFAULT 1,
    CONSTRAINT chk_users_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED'))
);

-- Foreign keys
ALTER TABLE users ADD CONSTRAINT fk_users_person 
    FOREIGN KEY (person_id) REFERENCES persons(id);

ALTER TABLE users ADD CONSTRAINT fk_users_area 
    FOREIGN KEY (area_id) REFERENCES areas(id);

ALTER TABLE users ADD CONSTRAINT fk_users_position 
    FOREIGN KEY (position_id) REFERENCES positions(id);

ALTER TABLE users ADD CONSTRAINT fk_users_manager 
    FOREIGN KEY (direct_manager_id) REFERENCES users(id);


CREATE TABLE persons (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_type_id INTEGER NOT NULL,
    document_number VARCHAR(20) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    birth_date DATE,
    gender CHAR(1),
    personal_phone VARCHAR(20),
    work_phone VARCHAR(20),
    personal_email VARCHAR(200),
    address TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    status BOOLEAN DEFAULT true NOT NULL,
    CONSTRAINT chk_gender CHECK (gender IN ('M', 'F')),
    CONSTRAINT uk_person_document UNIQUE (document_type_id, document_number)
);

CREATE TABLE document_types (
    id INTEGER PRIMARY KEY,
    code VARCHAR(5) UNIQUE NOT NULL,
    description VARCHAR(50) NOT NULL,
    length INTEGER NOT NULL,
    active BOOLEAN DEFAULT true
);

CREATE TABLE roles (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(50) UNIQUE NOT NULL,
    description     TEXT,
    is_system       BOOLEAN DEFAULT false,
    active          BOOLEAN DEFAULT true,
    created_at      TIMESTAMP DEFAULT NOW()
);

CREATE TABLE permissions (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    module          VARCHAR(50) NOT NULL,
    action          VARCHAR(50) NOT NULL,
    resource        VARCHAR(100),
    description     TEXT,
    created_at      TIMESTAMP DEFAULT NOW(),
    CONSTRAINT uk_permission UNIQUE (module, action, resource)
);

CREATE TABLE roles_permissions (
    role_id         UUID NOT NULL,
    permission_id   UUID NOT NULL,
    created_at      TIMESTAMP DEFAULT NOW(),
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

CREATE TABLE users_roles (
    user_id         UUID NOT NULL,
    role_id         UUID NOT NULL,
    assigned_by     UUID NOT NULL,
    assigned_at     TIMESTAMP DEFAULT NOW(),
    expiration_date DATE,
    active          BOOLEAN DEFAULT true,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Relacionar persons con document_types
ALTER TABLE persons ADD CONSTRAINT fk_persons_document_type
    FOREIGN KEY (document_type_id) REFERENCES document_types(id);

-- Relacionar users con users (creador y actualizador)
ALTER TABLE users ADD CONSTRAINT fk_users_created_by
    FOREIGN KEY (created_by) REFERENCES users(id);


ALTER TABLE users ADD CONSTRAINT fk_users_updated_by
    FOREIGN KEY (updated_by) REFERENCES users(id);

-- Relacionar users_roles con users (quien asignó el rol)
ALTER TABLE users_roles ADD CONSTRAINT fk_users_roles_assigned_by
    FOREIGN KEY (assigned_by) REFERENCES users(id);

-- Relacionar roles y permissions con users (quien creó el rol o permiso)
ALTER TABLE roles ADD COLUMN created_by UUID;
ALTER TABLE roles ADD CONSTRAINT fk_roles_created_by
    FOREIGN KEY (created_by) REFERENCES users(id);


ALTER TABLE permissions ADD COLUMN created_by UUID;
ALTER TABLE permissions ADD CONSTRAINT fk_permissions_created_by
    FOREIGN KEY (created_by) REFERENCES users(id);