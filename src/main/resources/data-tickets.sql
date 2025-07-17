-- Ticket critique soumis par l'utilisateur 'user'
INSERT INTO ticket (title, description, resolved, priority_id, submitting_user_id)
VALUES ('Serveur principal en panne',
        'Le serveur principal ne répond plus depuis ce matin. Tous les services sont indisponibles.', false, 1, 2);

-- Ticket résolu par l'admin
INSERT INTO ticket (title, description, resolved, priority_id, submitting_user_id, resolving_user_id)
VALUES ('Problème de connexion WiFi', 'Impossible de se connecter au réseau WiFi de l''entreprise.', true, 3, 3, 1);

-- Ticket en cours
INSERT INTO ticket (title, description, resolved, priority_id, submitting_user_id)
VALUES ('Installation nouveau logiciel',
        'Demande d''installation du nouveau logiciel de comptabilité sur tous les postes.', false, 2, 4);

-- Ticket de sécurité
INSERT INTO ticket (title, description, resolved, priority_id, submitting_user_id)
VALUES ('Mise à jour sécuritaire urgente', 'Application des derniers correctifs de sécurité sur tous les serveurs.',
        false, 1, 2);

-- Ticket simple
INSERT INTO ticket (title, description, resolved, priority_id, submitting_user_id)
VALUES ('Changement mot de passe', 'Demande de réinitialisation du mot de passe pour accès à l''application.', false, 4,
        3);
