CREATE TABLE todo (
    identifier BIGSERIAL PRIMARY KEY NOT NULL,
    content VARCHAR(4096) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    status VARCHAR NOT NULL
);

CREATE INDEX todo_status_idx ON todo(status);
