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