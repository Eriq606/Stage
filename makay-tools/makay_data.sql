insert into roles values(default, 'superviseur', 30, 0);

insert into utilisateurs values(default, 1, 'Jean', 'jean@mail.io', '032 63 156 28', 'root', 0);

update roles set nom='serveur', numero='1';

-- 28/08/2024

insert into categories values(default, 'boissons');
insert into produits values(default, 'Jägerbomb', 35000, 0, 1);
insert into historique_prix_produits values(default, 1, 1, 35000, current_timestamp(), 0);

insert into categories values(default, 'plats');
insert into produits values(default, 'Souris d`agneau', 25000, 0, 1);
insert into historique_prix_produits values(default, 1, 2, 25000, current_timestamp, 0);

update produits set idcategorie=2 where id=2;