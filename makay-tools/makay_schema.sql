create domain email as varchar check (value ~ '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$');
create domain phone as varchar check (value ~ '^\d{3}\s\d{2}\s\d{3}\s\d{2}$');

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