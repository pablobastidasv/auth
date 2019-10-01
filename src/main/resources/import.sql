INSERT INTO tenants(id, name) VALUES ('avalane', 'Avalane Inc.');

INSERT INTO users(id, username, enc_key, salt, state)
  VALUES(1, 'username', '1/MWIdhLRtvrg4hO7shX/g==', 'hXlt4UvdAuCxXxbf7k13mw==', 'ACTIVE');
INSERT INTO users(id, username, enc_key, salt, state)
  VALUES(2, 'user', '1/MWIdhLRtvrg4hO7shX/g==', 'hXlt4UvdAuCxXxbf7k13mw==', 'ACTIVE');

INSERT INTO user_x_tenant(user_id, tenant_id) VALUES (1, 'avalane');
INSERT INTO user_x_tenant(user_id, tenant_id) VALUES (2, 'avalane');

INSERT INTO roles(id, name) VALUES('ADMIN', 'administrator');
INSERT INTO user_x_role(user_id, role_id) VALUES (1, 'ADMIN');