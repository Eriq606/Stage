DELETE FROM annulation_remises;
DELETE FROM annulation_actions;
DELETE FROM annulation_paiements;
DELETE FROM remises;
DELETE FROM action_superviseurs;
DELETE FROM paiements;
DELETE FROM accompagnement_commandes;
DELETE FROM clotures;
DELETE FROM commande_filles_terminees;
DELETE FROM commande_filles;
DELETE FROM commandes;
DELETE FROM accompagnement_produits;
DELETE FROM accompagnements;
DELETE FROM stock_produits;
DELETE FROM historique_prix_produits;
DELETE FROM produits;
DELETE FROM role_categorie_produits_checkings;
DELETE FROM categories;
DELETE FROM historique_role_utilisateurs;
DELETE FROM mode_paiements;
DELETE FROM rangee_utilisateurs;
DELETE FROM rangee_places;
DELETE FROM rangees;
DELETE FROM places;
DELETE FROM type_places;
DELETE FROM session_utilisateurs;
DELETE FROM utilisateurs;
DELETE FROM roles;
ALTER SEQUENCE annulation_remises_id_seq RESTART WITH 1;
ALTER SEQUENCE annulation_actions_id_seq RESTART WITH 1;
ALTER SEQUENCE annulation_paiements_id_seq RESTART WITH 1;
ALTER SEQUENCE remises_id_seq RESTART WITH 1;
ALTER SEQUENCE action_superviseurs_id_seq RESTART WITH 1;
ALTER SEQUENCE paiements_id_seq RESTART WITH 1;
ALTER SEQUENCE accompagnement_commandes_id_seq RESTART WITH 1;
ALTER SEQUENCE clotures_id_seq RESTART WITH 1;
ALTER SEQUENCE commande_filles_terminees_id_seq RESTART WITH 1;
ALTER SEQUENCE commande_filles_id_seq RESTART WITH 1;
ALTER SEQUENCE commandes_id_seq RESTART WITH 1;
ALTER SEQUENCE accompagnement_produits_id_seq RESTART WITH 1;
ALTER SEQUENCE accompagnements_id_seq RESTART WITH 1;
ALTER SEQUENCE stock_produits_id_seq RESTART WITH 1;
ALTER SEQUENCE historique_prix_produits_id_seq RESTART WITH 1;
ALTER SEQUENCE produits_id_seq RESTART WITH 1;
ALTER SEQUENCE role_categorie_produits_checkings_id_seq RESTART WITH 1;
ALTER SEQUENCE categories_id_seq RESTART WITH 1;
ALTER SEQUENCE historique_role_utilisateurs_id_seq RESTART WITH 1;
ALTER SEQUENCE mode_paiements_id_seq RESTART WITH 1;
ALTER SEQUENCE rangee_utilisateurs_id_seq RESTART WITH 1;
ALTER SEQUENCE rangee_places_id_seq RESTART WITH 1;
ALTER SEQUENCE rangees_id_seq RESTART WITH 1;
ALTER SEQUENCE places_id_seq RESTART WITH 1;
ALTER SEQUENCE type_places_id_seq RESTART WITH 1;
ALTER SEQUENCE session_utilisateurs_id_seq RESTART WITH 1;
ALTER SEQUENCE utilisateurs_id_seq RESTART WITH 1;
ALTER SEQUENCE roles_id_seq RESTART WITH 1;

insert into roles values(default, 'Serveur', 0, '1'),
                        (default, 'Barman', 0, '2'),
                        (default, 'Cuisinier', 0, '3'),
                        (default, 'Caisse', 0, '4'),
                        (default, 'Analyste', 0, '5'),
                        (default, 'Superviseur', 0, '6'),
                        (default, 'Off', 0, '7'),
                        (default, 'Admin', 0, '8');

alter domain email drop constraint email_check;
insert into utilisateurs values(default, 8, 'ADMIN', 'ADMIN', '', 'root', 0, 0),
                               (default, 6, 'Responsable_1', 'Resp_1', '', 'root', 0, 0);

insert into rangees values(0, 'Inutilisée', 0),
                          (-1, 'Off', 0);

insert into mode_paiements values(-1, 'V.A.T cadre', 0),
                                 (-2, 'V.A.T client', 0);