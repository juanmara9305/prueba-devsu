CREATE TABLE person (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    gender VARCHAR(50) NOT NULL,
    birth_date DATE NOT NULL,
    identification VARCHAR(100) NOT NULL,
    address VARCHAR(500) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_person_identification ON person(identification);
CREATE INDEX idx_person_name ON person(name);
CREATE INDEX idx_person_deleted ON person(deleted);

CREATE TABLE client (
    id BIGSERIAL PRIMARY KEY,
    person_id BIGINT UNIQUE NOT NULL,
    password VARCHAR(128) NOT NULL,
    status BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at TIMESTAMP,
    CONSTRAINT fk_client_person FOREIGN KEY (person_id) REFERENCES person(id) ON DELETE CASCADE
);

CREATE INDEX idx_clients_id ON client(id);
CREATE INDEX idx_clients_status ON client(status);
CREATE INDEX idx_clients_deleted ON client(deleted);
