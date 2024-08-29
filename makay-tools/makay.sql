create domain email as varchar check (value ~ '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$');
create domain phone as varchar check (value ~ '^\d{3}\s\d{2}\s\d{3}\s\d{2}$');
create domain price as numeric(16,2) check (value>=0);

create table roles(
    id serial primary key,
    nom varchar not null,
    autorisation int default 0,
    etat int default 0
);
create table utilisateurs(
    id serial primary key,
    idrole int not null references roles(id),
    nom varchar not null,
    "email" email unique,
    contact phone unique,
    motdepasse text not null,
    etat int default 0,
    CONSTRAINT unique_nom_motdepasse UNIQUE (nom, motdepasse)
);
create table historique_role_utilisateurs(
    id serial primary key,
    idutilisateur int not null references utilisateurs(id),
    idrole int not null references roles(id),
    dateheure timestamp not null,
    etat int default 0
);

alter table roles add numero varchar unique;

insert into roles values(default, 'superviseur', 30, 0);

insert into utilisateurs values(default, 1, 'Jean', 'jean@mail.io', '032 63 156 28', 'root', 0);

update roles set nom='serveur', numero='1';

-- 26/08/2024

create table categories(
    id serial primary key,
    nom varchar unique not null,
    etat int default 0
);
create table accompagnements(
    id serial primary key,
    nom varchar unique not null,
    etat int default 0
);
create table produits(
    id serial primary key,
    nom varchar unique not null,
    prix price not null,
    etat int default 0
);
create table historique_prix_produits(
    id serial primary key,
    idutilisateur int not null references utilisateurs(id),
    idproduit int not null references produits(id),
    prix price not null,
    dateheure timestamp not null,
    etat int default 0
);

alter table accompagnements add idproduit int not null references produits(id);
alter table produits add idcategorie int not null references categories(id);

alter table accompagnements drop idproduit;

create table accompagnement_produits(
    id serial primary key,
    idproduit int not null references produits(id),
    idaccompagnement int not null references accompagnements(id),
    etat int default 0
);

insert into categories values(default, 'boissons');
insert into produits values(default, 'Jägerbomb', 35000, 0, 1);
insert into historique_prix_produits values(default, 1, 1, 35000, current_timestamp, 0);

insert into categories values(default, 'plats');
insert into produits values(default, 'Souris d`agneau', 25000, 0, 1);
insert into historique_prix_produits values(default, 1, 2, 25000, current_timestamp, 0);

update produits set idcategorie=2 where id=2;

-- 27/08/2024
create domain quantity as numeric(16,2) check (value>=0);

create table places(
    id serial primary key,
    nom varchar unique not null,
    etat int default 0
);
create table commandes(
    id serial primary key,
    idutilisateur int not null references utilisateurs(id),
    idplace int not null references places(id),
    dateheure_ouverture timestamp not null,
    dateheure_cloture timestamp,
    montant price not null,
    etat int default 0
);
create table commande_filles(
    id serial primary key,
    idcommande int not null references commandes(id),
    idproduit int not null references produits(id),
    prix_unitaire price not null,
    quantite quantity not null,
    montant price not null,
    notes text,
    etat int default 0
);
create table accompagnement_commandes(
    id serial primary key,
    idcommandefille int not null references commande_filles(id),
    idaccompagnement int not null references accompagnements(id),
    etat int default 0
);

insert into accompagnements values(default, 'riz', 0);
insert into accompagnements values(default, 'frites', 0);
insert into accompagnements values(default, 'glaçons', 0);

insert into accompagnement_produits values(default, 2, 1, 0);
insert into accompagnement_produits values(default, 2, 2, 0);
insert into accompagnement_produits values(default, 1, 3, 0);

insert into places values(default, '16', 0);
insert into places values(default, '16 bis', 0);

alter table roles drop autorisation;
alter table utilisateurs add autorisation int default 0;

create table rangees(
    id serial primary key,
    nom varchar unique not null,
    etat int default 0
);
create table type_places(
    id serial primary key,
    nom varchar unique not null,
    etat int default 0
);
create table rangee_places(
    id serial primary key,
    idrangee int not null references rangees(id),
    idplace int not null references places(id),
    dateheure timestamp not null,
    etat int default 0
);

alter table places add idtypeplace int references type_places(id);

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
alter table places alter column idtypeplace set not null;

alter table type_places add numero varchar unique;
update type_places set numero='1' where id=1;
update type_places set numero='2' where id=2;
update type_places set numero='3' where id=3;
alter table type_places alter column numero set not null;

insert into places values(default, '11', 0, 2);
insert into places values(default, '12', 0, 2);
insert into places values(default, '14', 0, 2);
insert into places values(default, '15', 0, 2);
insert into places values(default, '17', 0, 2);
insert into places values(default, '18', 0, 2);
insert into places values(default, '19', 0, 2);
insert into places values(default, '19 bis', 0, 2);

alter table rangee_places add idutilisateur int not null references utilisateurs(id);

create or replace view v_places as
select *
from places where places.etat=0;

create or replace view v_rangees as
select *
from rangees where rangees.etat=0;

create or replace view v_dateheure_max_rangee_places as
select max(dateheure) as max_dateheure
from rangee_places;

create or replace view v_arrangement_place_part as
select rp.*, v_rangees.nom as nom_rangee, v_places.nom as nom_place, dm.max_dateheure
from rangee_places rp
join v_rangees on rp.idrangee=v_rangees.id
join v_places on rp.idplace=v_places.id
left join v_dateheure_max_rangee_places dm on rp.dateheure=dm.max_dateheure
where rp.etat=0;

create or replace view v_arrangement_place as
select *
from v_arrangement_place_part where max_dateheure is not null;