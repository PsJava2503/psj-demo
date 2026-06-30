INSERT INTO permissions (id, code, resource, action, name, description)
VALUES
    (13, 'agent:use', 'agent', 'use', 'Use AI agent', 'Use commerce AI agent chat and analysis APIs')
ON CONFLICT (id) DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
VALUES
    (1, 13),
    (2, 13),
    (3, 13)
ON CONFLICT (role_id, permission_id) DO NOTHING;
