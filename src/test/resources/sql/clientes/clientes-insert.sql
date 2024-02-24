INSERT INTO usuarios (id_usuario, username, password, role) VALUES (100, 'teo@email.com', '$2a$10$7VTRDkdfKbw21USOD/wkw.ce8nbAvha8D3TCaq/frm9C96S.a.jDC', 'ROLE_ADMIN');
INSERT INTO usuarios (id_usuario, username, password, role) VALUES (101, 'bia@email.com', '$2a$10$7VTRDkdfKbw21USOD/wkw.ce8nbAvha8D3TCaq/frm9C96S.a.jDC', 'ROLE_CLIENTE');
INSERT INTO usuarios (id_usuario, username, password, role) VALUES (102, 'arnold@email.com', '$2a$10$7VTRDkdfKbw21USOD/wkw.ce8nbAvha8D3TCaq/frm9C96S.a.jDC', 'ROLE_CLIENTE');
INSERT INTO usuarios (id_usuario, username, password, role) VALUES (103, 'marilia@email.com', '$2a$10$7VTRDkdfKbw21USOD/wkw.ce8nbAvha8D3TCaq/frm9C96S.a.jDC', 'ROLE_CLIENTE');

INSERT INTO clientes (id_cliente, nome, cpf, id_usuario) VALUES (10, 'Arnold Pro', '49344061823', 102);
INSERT INTO clientes (id_cliente, nome, cpf, id_usuario) VALUES (20, 'Marilia Santos', '81309409064', 103);