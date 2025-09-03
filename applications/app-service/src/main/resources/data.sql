INSERT INTO status (id, name, description) VALUES
 (1, 'Aceptado', 'Solicitud aprobada'),
 (2, 'Rechazado', 'Solicitud rechazada'),
 (3, 'En revisión', 'El sistema valida la información para decidir si se acepta o no el préstamo.'),
 (4, 'Pendiente de revisión', 'Se registra la solicitud en el sistema.')
 ON CONFLICT (id) DO NOTHING;

INSERT INTO loan_type (id, name, min_amount, max_amount, interest_rate) VALUES
 (1, 'Libre inversión', 300000, 20000000, 12.2),
 (2, 'Hipotecario', 15000000, 95000000, 15.0)
 ON CONFLICT (id) DO NOTHING;