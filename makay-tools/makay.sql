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

create or replace view v_type_places as
select *
from type_places where type_places.etat=0;

create or replace view v_rangee_places as
select *
from rangee_places where etat=0;

create or replace view v_dateheure_max_rangee_places as
select date_trunc('minute', max(dateheure)) as max_dateheure
from v_rangee_places;

create or replace view v_arrangement_place_part as
select rp.*, v_rangees.nom as nom_rangee, v_places.nom as nom_place, v_type_places.nom as nom_type_place, v_type_places.numero as numero_type_place, dm.max_dateheure
from v_rangee_places rp
join v_rangees on rp.idrangee=v_rangees.id
join v_places on rp.idplace=v_places.id
join v_type_places on v_places.idtypeplace=v_type_places.id
left join v_dateheure_max_rangee_places dm on date_trunc('minute', rp.dateheure)=dm.max_dateheure;

create or replace view v_arrangement_place as
select *
from v_arrangement_place_part where max_dateheure is not null;

insert into rangee_places values(default, 2, 3, current_timestamp, 0, 2);
insert into rangee_places values(default, 2, 4, current_timestamp, 0, 2);
insert into rangee_places values(default, 2, 6, current_timestamp, 0, 2);
insert into rangee_places values(default, 2, 7, current_timestamp, 0, 2);

insert into rangee_places values(default, 3, 1, current_timestamp, 0, 2);
insert into rangee_places values(default, 3, 2, current_timestamp, 0, 2);
insert into rangee_places values(default, 3, 8, current_timestamp, 0, 2);
insert into rangee_places values(default, 3, 10, current_timestamp, 0, 2);

create table utilisateur_roles(
    id serial primary key,
    idutilisateur int not null references utilisateurs(id),
    idrole int not null references roles(id),
    dateheure timestamp not null,
    etat int default 0
);

insert into utilisateur_roles values(default, 1, 1, current_timestamp, 0);
insert into utilisateur_roles values(default, 2, 2, current_timestamp, 0);

drop table utilisateur_roles;

create or replace view v_utilisateurs as
select id, idrole, nom, email, contact
from utilisateurs where etat=0;

create or replace view v_roles as
select *
from roles where etat=0;

create or replace view v_historique_utilisateur_roles as
select *
from historique_role_utilisateurs where etat=0;

create or replace view v_dateheure_max_roles as
select date_trunc('minute', max(dateheure)) as max_dateheure
from v_historique_utilisateur_roles;

create or replace view v_attribution_roles_part as
select vh.*, v_utilisateurs.nom as nom_utilisateur, v_roles.nom as nom_role, vd.max_dateheure, v_roles.numero as numero_role
from v_historique_utilisateur_roles as vh
join v_utilisateurs on vh.idutilisateur=v_utilisateurs.id
join v_roles on vh.idrole=v_roles.id
left join v_dateheure_max_roles as vd on date_trunc('minute', vh.dateheure)=vd.max_dateheure
order by vh.dateheure desc
limit (select count(*) from v_utilisateurs);

create or replace view v_attribution_roles as
select *
from v_attribution_roles_part where max_dateheure is not null
order by idutilisateur;

insert into historique_role_utilisateurs values(default, 1, 1, current_timestamp, 0),
                                               (default, 2, 2, current_timestamp, 0);

insert into roles values(default, 'off', 0, '7');

create table session_utilisateurs(
    id serial primary key,
    sessionid varchar unique not null,
    expiration timestamp not null
);

alter table session_utilisateurs add idutilisateur int not null references utilisateurs(id);
alter table session_utilisateurs add estvalide int default 0;
alter table historique_role_utilisateurs alter column dateheure drop not null;
alter table historique_role_utilisateurs alter column dateheure set default now();

alter table rangee_places alter column dateheure drop not null;
alter table rangee_places alter column dateheure set default now();

create or replace view v_dateheure_max_rangee_places as
select max(dateheure) as max_dateheure
from v_rangee_places;

create or replace view v_arrangement_place_part as
select rp.*, v_rangees.nom as nom_rangee, v_places.nom as nom_place, v_type_places.nom as nom_type_place, v_type_places.numero as numero_type_place, dm.max_dateheure
from v_rangee_places rp
join v_rangees on rp.idrangee=v_rangees.id
join v_places on rp.idplace=v_places.id
join v_type_places on v_places.idtypeplace=v_type_places.id
left join v_dateheure_max_rangee_places dm on rp.dateheure=dm.max_dateheure;

create or replace view v_arrangement_place as
select *
from v_arrangement_place_part where max_dateheure is not null;

create or replace view v_dateheure_max_roles as
select max(dateheure) as max_dateheure
from v_historique_utilisateur_roles;

create or replace view v_attribution_roles_part as
select vh.*, v_utilisateurs.nom as nom_utilisateur, v_roles.nom as nom_role, vd.max_dateheure, v_roles.numero as numero_role
from v_historique_utilisateur_roles as vh
join v_utilisateurs on vh.idutilisateur=v_utilisateurs.id
join v_roles on vh.idrole=v_roles.id
left join v_dateheure_max_roles as vd on vh.dateheure=vd.max_dateheure;

create or replace view v_attribution_roles as
select *
from v_attribution_roles_part where max_dateheure is not null;

create table rangee_utilisateurs(
    id serial primary key,
    idrangee int not null references rangees(id),
    idutilisateur int not null references utilisateurs(id),
    dateheure timestamp default now(),
    etat int default 0
);

create or replace view v_rangee_utilisateurs as
select *
from rangee_utilisateurs where etat=0;

create or replace view v_dateheure_max_rangee_users as
select max(dateheure) as max_dateheure
from v_rangee_utilisateurs;

create or replace view v_dispatch_staff_part as
select vu.*, v_rangees.nom as nom_rangee, v_utilisateurs.nom as nom_utilisateur, v_utilisateurs.email as email_utilisateur, v_utilisateurs.contact as contact_utilisateur, vdm.max_dateheure
from v_rangee_utilisateurs vu
join v_rangees on vu.idrangee=v_rangees.id
join v_utilisateurs on vu.idutilisateur=v_utilisateurs.id
left join v_dateheure_max_rangee_users vdm on vu.dateheure=vdm.max_dateheure;

create or replace view v_dispatch_staff as
select *
from v_dispatch_staff_part where max_dateheure is not null;

alter table rangee_utilisateurs add idutilisateur_responsable int not null references utilisateurs(id);

create or replace view v_places_utilisateurs as
select vds.*, vap.idplace, vap.nom_place, vap.nom_type_place, vap.numero_type_place
from v_dispatch_staff vds
join v_arrangement_place vap on vds.idrangee=vap.idrangee;

-- ETATS COMMANDE:
-- 0: CRÉÉE
-- 10: ADDITION
-- 20: PAYÉE
-- 30: ANNULÉE
-- 40: SUPPRIMÉE
create or replace view v_commandes as
select *
from commandes where etat<20;

create or replace view v_commande_filles as
select *
from commande_filles where etat=0;

create or replace view v_produits as
select *
from produits where etat=0;

create or replace view v_categories as
select *
from categories where etat=0;

create or replace view v_commande_avec_details_en_cours as
select vc.*,
       vcf.id as idcmdfille, vcf.prix_unitaire, vcf.quantite, vcf.montant as montant_cmd_fille, vcf.notes,
       vu.nom as nom_utilisateur, vu.email, vu.contact,
       v_places.nom as nom_place,
       vp.id as idproduit, vp.nom as nom_produit,
       v_categories.id as idcategorie, v_categories.nom as nom_categorie
from v_commandes vc
join v_commande_filles vcf on vc.id=vcf.idcommande
join v_utilisateurs vu on vc.idutilisateur=vu.id
join v_places on vc.idplace=v_places.id
join v_produits vp on vcf.idproduit=vp.id
join v_categories on vp.idcategorie=v_categories.id;

create or replace view v_commandes as 
select commandes.*, v_places.nom as nom_place, v_places.idtypeplace, v_type_places.numero as numero_type_place
from commandes
join v_places on commandes.idplace=v_places.id
join v_type_places on v_places.idtypeplace=v_type_places.id
where commandes.etat<20;

insert into rangees values(default, 'inutilisee', 0),
                          (default, 'off', 0);

delete from rangees where id>5;

alter sequence rangees_id_seq restart with 5;

insert into rangees values(0, 'inutilisee', 0),
                         (-1, 'off', 0);

create or replace view v_commandes as 
select commandes.*, v_places.nom as nom_place, v_places.idtypeplace, v_type_places.numero as numero_type_place
from commandes
join v_places on commandes.idplace=v_places.id
join v_type_places on v_places.idtypeplace=v_type_places.id
where commandes.etat<40;

create or replace view v_serveurs_encours_1 as
select idutilisateur, nom_utilisateur, email_utilisateur, contact_utilisateur
from v_dispatch_staff
where idrangee=-1;

create or replace view v_serveurs_encours as
select *
from v_utilisateurs vu
left join v_serveurs_encours_1 v1 on vu.id=v1.idutilisateur
where v1.idutilisateur is not null;

drop view v_serveurs_encours;

create or replace view v_serveurs_encours as
select vu.*, v_roles.nom as nom_role, v_roles.numero as numero_role
from v_utilisateurs vu
join v_roles on vu.idrole=v_roles.id
left join v_serveurs_encours_1 v1 on vu.id=v1.idutilisateur
where v1.idutilisateur is null and v_roles.numero in ('1','2','6');

insert into roles values(default, 'barman', 0, '2');
alter domain phone drop CONSTRAINT phone_check;
select nextval('utilisateurs_id_seq');
insert into utilisateurs values(default, 4, 'Marc', 'marc@mail.io', '261323536740', 'root', 0, 0);

create table role_categorie_produits(
    id serial primary key,
    idrole int not null references roles(id),
    idcategorie int not null references categories(id),
    etat int default 0
);

create or replace view v_role_categorie_produits as
select *
from role_categorie_produits where etat=0;

create or replace view v_commandefille_produits as
select vcf.*, vp.nom, vp.prix, vp.idcategorie
from v_commande_filles vcf
join v_produits vp on vcf.idproduit=vp.id;

insert into role_categorie_produits values(default, 1, 1, 0),
                                          (default, 1, 2, 0);

insert into role_categorie_produits values(default, 2, 1, 0),
                                          (default, 2, 2, 0);                                          

insert into role_categorie_produits values(default, 4, 1, 0);

create table commande_filles_terminees(
    id serial primary key,
    idutilisateur int not null references utilisateurs(id),
    idcommandefille int not null references commande_filles(id),
    dateheure timestamp not null,
    est_termine int default 1
);
alter table commande_filles_terminees add etat int default 0;
create or replace view v_commande_filles_terminees as
select *
from commande_filles_terminees where etat=0;

create or replace view v_commandefille_produits as
select vcf.*, vp.nom, vp.prix, vp.idcategorie, coalesce(vcft.est_termine, -1) as est_termine
from v_commande_filles vcf
join v_produits vp on vcf.idproduit=vp.id
left join v_commande_filles_terminees vcft on vcf.id=vcft.idcommandefille;

create table role_categorie_produits_checkings(
    id serial primary key,
    idrole int not null references roles(id),
    idcategorie int not null references categories(id),
    etat int default 0
);
create or replace view v_role_categorie_produits_checkings as
select *
from role_categorie_produits_checkings where etat=0;

create or replace view v_commandes as 
select commandes.*, v_places.nom as nom_place, v_places.idtypeplace, v_type_places.numero as numero_type_place
from commandes
join v_places on commandes.idplace=v_places.id
join v_type_places on v_places.idtypeplace=v_type_places.id
where commandes.etat<40;

insert into role_categorie_produits values(default, 4, 2, 0);

insert into role_categorie_produits_checkings values(default, 1, 1, 0),
                                                    (default, 1, 2, 0),

                                                    (default, 2, 1, 0),
                                                    (default, 2, 2, 0),

                                                    (default, 4, 1, 0),
                                                    (default, 4, 2, 0);

delete from role_categorie_produits_checkings where idrole=4 and idcategorie=2;

create or replace view v_commandes as 
select commandes.*, v_places.nom as nom_place, v_places.idtypeplace, v_type_places.numero as numero_type_place
from commandes
join v_places on commandes.idplace=v_places.id
join v_type_places on v_places.idtypeplace=v_type_places.id
join v_utilisateurs vu on commandes.idutilisateur=vu.id
where commandes.etat<40;

create or replace view v_utilisateurs as
select utilisateurs.id, utilisateurs.idrole, utilisateurs.nom, utilisateurs.email, utilisateurs.contact, v_roles.nom as nom_role, v_roles.numero as numero_role
from utilisateurs
join v_roles on utilisateurs.idrole=v_roles.id
where utilisateurs.etat=0;

create table mode_paiements(
    id serial primary key,
    nom varchar not null unique,
    etat int default 0
);

insert into mode_paiements values(default, 'V.A.T', 0),
                                (default, 'Carte de crédit', 0),
                                (default, 'Mobile Money', 0),
                                (default, 'Espèces', 0);

insert into historique_role_utilisateurs values(default, 1, 1, current_timestamp, 0),
                                               (default, 2, 2, current_timestamp, 0),
                                               (default, 4, 4, current_timestamp, 0);

insert into categories values(default, 'patisseries', 0);

insert into produits values(default, 'brownie', 10000, 0, 3);

insert into historique_prix_produits values(default, 2, 3, 10000, current_timestamp, 0);

insert into role_categorie_produits values(default, 1, 3, 0),
                                          (default, 2, 3, 0),
                                          (default, 4, 3, 0);

create or replace view v_accompagnements as
select *
from accompagnements where etat=0;

create or replace view v_accompagnement_commandes as
select *
from accompagnement_commandes where etat=0;

create or replace view v_commandefille_accompagnements as
select vcf.*, v_accompagnements.nom as nom_accompagnement
from v_commande_filles vcf
join v_accompagnement_commandes vac on vcf.id=vac.idcommandefille
join v_accompagnements on vac.idaccompagnement=v_accompagnements.id;

create table demande_addition(
    id serial primary key,
    idutilisateur int not null references utilisateurs(id),
    idcommande int not null references commandes(id),
    dateheure timestamp default current_timestamp,
    etat int default 0
);

alter table demande_addition rename to demande_additions;

insert into roles values(default, 'caisse', 0, 4);
alter table utilisateurs drop constraint utilisateurs_contact_key;
alter sequence utilisateurs_id_seq restart with 6;
insert into utilisateurs values(default, 5, 'Jack', 'jack@mail.io', '032 63 156 28', 'root', 0, 0);
insert into historique_role_utilisateurs values(default, 6, 5, current_timestamp, 0);

alter table commandes add reste_a_payer price;
drop view v_commande_avec_details_en_cours;
drop view v_commandes;
create or replace view v_commandes as 
select commandes.*, v_places.nom as nom_place, v_places.idtypeplace, v_type_places.numero as numero_type_place
from commandes
join v_places on commandes.idplace=v_places.id
join v_type_places on v_places.idtypeplace=v_type_places.id
join v_utilisateurs vu on commandes.idutilisateur=vu.id
where commandes.etat<40;

create table paiements(
    id serial primary key,
    idcommande int not null references commandes(id),
    idmodepaiement int not null references mode_paiements(id),
    dateheure timestamp default current_timestamp,
    montant price not null
);
alter table paiements add etat int default 0;
alter table paiements add idutilisateur int not null references utilisateurs(id);

update commandes set reste_a_payer=montant;

create or replace view v_commandes as 
select commandes.*, v_places.nom as nom_place, v_places.idtypeplace, v_type_places.numero as numero_type_place
from commandes
join v_places on commandes.idplace=v_places.id
join v_type_places on v_places.idtypeplace=v_type_places.id
join v_utilisateurs vu on commandes.idutilisateur=vu.id
where commandes.etat<40;

insert into roles values(default, 'cuisinier', 0, '3');
insert into utilisateurs values(default, 6, 'Luc', 'luc@mail.io', '032 63 156 28', 'root', 0, 0);
insert into historique_role_utilisateurs values(default, 7, 6, current_timestamp, 0);

insert into role_categorie_produits_checkings values(default, 6, 2, 0),
                                                    (default, 6, 3, 0);

create or replace view v_dispatch_staff_part as
select vu.*, v_rangees.nom as nom_rangee, v_utilisateurs.nom as nom_utilisateur, v_utilisateurs.email as email_utilisateur, v_utilisateurs.contact as contact_utilisateur, vdm.max_dateheure, v_utilisateurs.idrole, v_roles.nom as nom_role, v_roles.numero as numero_role
from v_rangee_utilisateurs vu
join v_rangees on vu.idrangee=v_rangees.id
join v_utilisateurs on vu.idutilisateur=v_utilisateurs.id
join v_roles on v_utilisateurs.idrole=v_roles.id
left join v_dateheure_max_rangee_users vdm on vu.dateheure=vdm.max_dateheure;

create or replace view v_dispatch_staff as
select *
from v_dispatch_staff_part where max_dateheure is not null;

drop view v_places_utilisateurs;

create or replace view v_places_utilisateurs as
select vds.*, vap.idplace, vap.nom_place, vap.nom_type_place, vap.numero_type_place
from v_dispatch_staff vds
join v_arrangement_place vap on vds.idrangee=vap.idrangee;                                                    

create or replace view v_serveurs_encours_1 as
select idutilisateur, nom_utilisateur, email_utilisateur, contact_utilisateur
from v_dispatch_staff
where idrangee=-1;

alter table produits add dernier_stock quantity;

create table stock_produits(
    id serial primary key,
    idproduit int not null references produits(id),
    idutilisateur int not null references utilisateurs(id),
    stock quantity not null,
    dateheure timestamp default current_timestamp,
    etat int default 0
);

alter table produits drop dernier_stock;

alter table produits add dernier_stock int;
update produits set dernier_stock=-1;

drop table demande_additions;
drop view v_role_categorie_produits;
drop table role_categorie_produits;

alter table produits drop dernier_stock;
alter table produits add dernier_stock real;

alter table stock_produits drop stock;
alter table stock_produits add stock real;
update stock_produits set stock=-1;
alter table stock_produits alter column stock set default -1;

alter table produits alter column dernier_stock set default -1;

update produits set dernier_stock=-1;

alter table produits drop dernier_stock;
alter table stock_produits drop stock;

alter table produits add dernier_stock numeric(16,2) default -1;
alter table stock_produits add stock numeric(16,2) default -1;

create table action_superviseurs(
    id serial primary key,
    idutilisateur int not null references utilisateurs(id),
    idcommandefille int not null references commande_filles(id),
    dateheure timestamp default current_timestamp,
    "action" int not null,
    etat int default 0
);

create or replace view v_commande_filles as
select *
from commande_filles where etat<20;

drop view v_commandefille_produits;

create or replace view v_commandefille_produits as
select vcf.*, vp.nom, vp.prix, vp.idcategorie, coalesce(vcft.est_termine, -1) as est_termine
from v_commande_filles vcf
join v_produits vp on vcf.idproduit=vp.id
left join v_commande_filles_terminees vcft on vcf.id=vcft.idcommandefille;