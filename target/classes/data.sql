INSERT INTO cliente (nome, cpf, email, created_at) VALUES
    ('Ana Souza',     '111.111.111-11', 'ana.souza@email.com',     CURRENT_TIMESTAMP),
    ('Bruno Lima',    '222.222.222-22', 'bruno.lima@email.com',    CURRENT_TIMESTAMP),
    ('Carla Mendes',  '333.333.333-33', 'carla.mendes@email.com',  CURRENT_TIMESTAMP),
    ('Diego Ferreira','444.444.444-44', 'diego.ferreira@email.com',CURRENT_TIMESTAMP);

INSERT INTO conta (numero_conta, saldo, ativa, cliente_id, created_at) VALUES
    ('0001-1', 5000.00, TRUE, 1, CURRENT_TIMESTAMP),
    ('0002-1', 3000.00, TRUE, 2, CURRENT_TIMESTAMP),
    ('0003-1', 1500.00, TRUE, 3, CURRENT_TIMESTAMP),
    ('0004-1',  750.00, TRUE, 4, CURRENT_TIMESTAMP);
