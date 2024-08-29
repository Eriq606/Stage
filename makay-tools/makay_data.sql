insert into roles values(default, 'superviseur', 30, 0);

insert into utilisateurs values(default, 1, 'Jean', 'jean@mail.io', '032 63 156 28', 'root', 0);

update roles set nom='serveur', numero='1';

-- 26/08/2024

insert into categories values(default, 'boissons');
insert into produits values(default, 'Jägerbomb', 35000, 0, 1);
insert into historique_prix_produits values(default, 1, 1, 35000, current_timestamp(), 0);

insert into categories values(default, 'plats');
insert into produits values(default, 'Souris d`agneau', 25000, 0, 1);
insert into historique_prix_produits values(default, 1, 2, 25000, current_timestamp, 0);

update produits set idcategorie=2 where id=2;

-- 27/08/2024
insert into accompagnements values(default, 'riz', 0);
insert into accompagnements values(default, 'frites', 0);
insert into accompagnements values(default, 'glaçons', 0);

insert into accompagnement_produits values(default, 2, 1, 0);
insert into accompagnement_produits values(default, 2, 2, 0);
insert into accompagnement_produits values(default, 1, 3, 0);

insert into places values(default, '16', 0);
insert into places values(default, '16 bis', 0);

-- 28/08/2024
insert into roles values(default, 'superviseur', 0, '6');
insert into utilisateurs values(default, 2, 'Jules', 'jules@mail.io', '038 27 160 50', 'root', 0, 30);

insert into rangees values(default, 'bar', 0);
insert into rangees values(default, 'rangee 1', 0);
insert into rangees values(default, 'rangee 2', 0);
insert into rangees values(default, 'rangee 3', 0);
insert into rangees values(default, 'terrasse', 0);

insert into type_places values(default, 'bar', 0);
insert into type_places values(default, 'salle', 0);
insert into type_places values(default, 'terrasse', 0);

update places set idtypeplace=2;