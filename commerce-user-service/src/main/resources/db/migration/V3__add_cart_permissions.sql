INSERT INTO permissions (id, code, resource, action, name, description)
VALUES
    (12, 'cart:view', 'cart', 'view', 'View cart', 'View and manage shopping cart items')
ON CONFLICT (id) DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
VALUES
    (1, 12),
    (2, 12),
    (3, 12)
ON CONFLICT (role_id, permission_id) DO NOTHING;
