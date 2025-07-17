-- Utilisateur administrateur (mot de passe: admin123)
INSERT INTO users (pseudo, password, admin)
VALUES ('admin', '$2a$10$tAhk5SU2Q1NqLdh.d.kc3eVyXe/I/n29sfX1lS3vmhv/tPQemqo0u', true);

-- Utilisateur normal (mot de passe: user123)
INSERT INTO users (pseudo, password, admin)
VALUES ('user', '$2a$10$83ofE.IqWlaxggRClj.jse3xPllHTSqbJzMtS1yZ/HCuCFCViJLxG', false);

-- Utilisateur de test (mot de passe: test123)
INSERT INTO users (pseudo, password, admin)
VALUES ('test', '$2a$10$Wx4uz1dR/zGGXGgar9VTweKf36/VaUfwSWcopeHTlLA4COrR/3G.S', false);

-- Utilisateur développeur (mot de passe: dev123)
INSERT INTO users (pseudo, password, admin)
VALUES ('dev', '$2a$10$6bKc8eSAHUcHbzGPIIe0.ulOfyQv8YhKSr5ABC9fH1ggf/joBbD8O', false);

-- Second administrateur (mot de passe: admin456)
INSERT INTO users (pseudo, password, admin)
VALUES ('admin2', '$2a$10$8VGlDviMxFy271wbpTVQG.YRbT59NLLeXl5ots2ZWi/ULdivuIMje', true);
