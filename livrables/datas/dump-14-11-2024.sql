CREATE SCHEMA IF NOT EXISTS "public";

CREATE SEQUENCE accompagnement_commandes_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE accompagnement_produits_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE accompagnements_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE action_superviseurs_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE annulation_actions_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE annulation_paiements_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE annulation_remises_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE categories_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE clotures_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE commande_filles_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE commande_filles_terminees_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE commandes_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE historique_prix_produits_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE historique_role_utilisateurs_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE import_produits_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE mode_paiements_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE paiements_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE places_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE produits_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE rangee_places_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE rangee_utilisateurs_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE rangees_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE remises_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE role_categorie_produits_checkings_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE roles_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE semaine_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE session_utilisateurs_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE stock_produits_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE type_places_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE utilisateurs_id_seq START WITH 1 INCREMENT BY 1;

CREATE DOMAIN email character varying;

CREATE DOMAIN phone character varying;

CREATE DOMAIN price numeric(16,2) CONSTRAINT price_check CHECK (VALUE >= 0::numeric);

CREATE DOMAIN quantity numeric(16,2) CONSTRAINT quantity_check CHECK (VALUE >= 0::numeric);

CREATE  TABLE accompagnements ( 
	id                   serial primary key  ,
	nom                  varchar  NOT NULL  ,
	etat                 integer DEFAULT 0   ,
	CONSTRAINT accompagnements_nom_key UNIQUE ( nom ) 
 );

CREATE  TABLE categories ( 
	id                   serial primary key  ,
	nom                  varchar  NOT NULL  ,
	etat                 integer DEFAULT 0   ,
	CONSTRAINT categories_nom_key UNIQUE ( nom ) 
 );

CREATE  TABLE import_produits ( 
	id                   serial primary key  ,
	nomproduit           varchar  NOT NULL  ,
	prix                 price  NOT NULL  ,
	nomcategorie         varchar  NOT NULL  ,
	CONSTRAINT import_produits_nomproduit_key UNIQUE ( nomproduit ) 
 );

CREATE  TABLE mode_paiements ( 
	id                   serial primary key  ,
	nom                  varchar  NOT NULL  ,
	etat                 integer DEFAULT 0   ,
	CONSTRAINT mode_paiements_nom_key UNIQUE ( nom ) 
 );

CREATE  TABLE produits ( 
	id                   serial primary key  ,
	nom                  varchar  NOT NULL  ,
	prix                 price  NOT NULL  ,
	etat                 integer DEFAULT 0   ,
	idcategorie          integer  NOT NULL  ,
	dernier_stock        numeric(16,2) DEFAULT '-1'::integer   ,
	CONSTRAINT produits_nom_key UNIQUE ( nom ) 
 );

CREATE  TABLE rangees ( 
	id                   serial primary key  ,
	nom                  varchar  NOT NULL  ,
	etat                 integer DEFAULT 0   ,
	CONSTRAINT rangees_nom_key UNIQUE ( nom ) 
 );

CREATE  TABLE roles ( 
	id                   serial primary key  ,
	nom                  varchar  NOT NULL  ,
	etat                 integer DEFAULT 0   ,
	numero               varchar    ,
	CONSTRAINT roles_numero_key UNIQUE ( numero ) 
 );

CREATE  TABLE semaine ( 
	id                   serial primary key  ,
	jour                 varchar  NOT NULL  ,
	CONSTRAINT semaine_jour_key UNIQUE ( jour ) 
 );

CREATE  TABLE type_places ( 
	id                   serial primary key  ,
	nom                  varchar  NOT NULL  ,
	etat                 integer DEFAULT 0   ,
	numero               varchar  NOT NULL  ,
	CONSTRAINT type_places_nom_key UNIQUE ( nom ) ,
	CONSTRAINT type_places_numero_key UNIQUE ( numero ) 
 );

CREATE  TABLE utilisateurs ( 
	id                   serial primary key  ,
	idrole               integer  NOT NULL  ,
	nom                  varchar  NOT NULL  ,
	email                email    ,
	contact              phone    ,
	motdepasse           text  NOT NULL  ,
	etat                 integer DEFAULT 0   ,
	autorisation         integer DEFAULT 0   ,
	CONSTRAINT utilisateurs_email_key UNIQUE ( email ) ,
	CONSTRAINT unique_nom_motdepasse UNIQUE ( nom, motdepasse ) 
 );

CREATE  TABLE accompagnement_produits ( 
	id                   serial primary key  ,
	idproduit            integer  NOT NULL  ,
	idaccompagnement     integer  NOT NULL  ,
	etat                 integer DEFAULT 0   
 );

CREATE  TABLE historique_prix_produits ( 
	id                   serial primary key  ,
	idutilisateur        integer  NOT NULL  ,
	idproduit            integer  NOT NULL  ,
	prix                 price  NOT NULL  ,
	dateheure            timestamp DEFAULT CURRENT_TIMESTAMP   ,
	etat                 integer DEFAULT 0   
 );

CREATE  TABLE historique_role_utilisateurs ( 
	id                   serial primary key  ,
	idutilisateur        integer  NOT NULL  ,
	idrole               integer  NOT NULL  ,
	dateheure            timestamp DEFAULT now()   ,
	etat                 integer DEFAULT 0   
 );

CREATE  TABLE places ( 
	id                   serial primary key  ,
	nom                  varchar  NOT NULL  ,
	etat                 integer DEFAULT 0   ,
	idtypeplace          integer  NOT NULL  ,
	CONSTRAINT places_nom_key UNIQUE ( nom ) 
 );

CREATE  TABLE rangee_places ( 
	id                   serial primary key  ,
	idrangee             integer  NOT NULL  ,
	idplace              integer  NOT NULL  ,
	dateheure            timestamp DEFAULT now()   ,
	etat                 integer DEFAULT 0   ,
	idutilisateur        integer  NOT NULL  
 );

CREATE  TABLE rangee_utilisateurs ( 
	id                   serial primary key  ,
	idrangee             integer  NOT NULL  ,
	idutilisateur        integer  NOT NULL  ,
	dateheure            timestamp DEFAULT now()   ,
	etat                 integer DEFAULT 0   ,
	idutilisateur_responsable integer  NOT NULL  
 );

CREATE  TABLE role_categorie_produits_checkings ( 
	id                   serial primary key  ,
	idrole               integer  NOT NULL  ,
	idcategorie          integer  NOT NULL  ,
	etat                 integer DEFAULT 0   
 );

CREATE  TABLE session_utilisateurs ( 
	id                   serial primary key  ,
	sessionid            varchar  NOT NULL  ,
	expiration           timestamp  NOT NULL  ,
	idutilisateur        integer  NOT NULL  ,
	estvalide            integer DEFAULT 0   ,
	CONSTRAINT session_utilisateurs_sessionid_key UNIQUE ( sessionid ) 
 );

CREATE  TABLE stock_produits ( 
	id                   serial primary key  ,
	idproduit            integer  NOT NULL  ,
	idutilisateur        integer  NOT NULL  ,
	dateheure            timestamp DEFAULT CURRENT_TIMESTAMP   ,
	etat                 integer DEFAULT 0   ,
	stock                numeric(16,2) DEFAULT '-1'::integer   
 );

CREATE  TABLE commandes ( 
	id                   serial primary key  ,
	idutilisateur        integer  NOT NULL  ,
	idplace              integer  NOT NULL  ,
	dateheure_ouverture  timestamp  NOT NULL  ,
	dateheure_cloture    timestamp    ,
	montant              price  NOT NULL  ,
	etat                 integer DEFAULT 0   ,
	reste_a_payer        price    ,
	montant_offert       price DEFAULT 0   ,
	montant_annulee      price DEFAULT 0   ,
	montant_remises      price DEFAULT 0   
 );

CREATE  TABLE paiements ( 
	id                   serial primary key  ,
	idcommande           integer  NOT NULL  ,
	idmodepaiement       integer  NOT NULL  ,
	dateheure            timestamp DEFAULT CURRENT_TIMESTAMP   ,
	montant              price  NOT NULL  ,
	etat                 integer DEFAULT 0   ,
	idutilisateur        integer  NOT NULL  
 );

CREATE  TABLE annulation_paiements ( 
	id                   serial primary key  ,
	idpaiement           integer  NOT NULL  ,
	idutilisateur        integer  NOT NULL  ,
	dateheure            timestamp DEFAULT CURRENT_TIMESTAMP   ,
	etat                 integer DEFAULT 0   
 );

CREATE  TABLE clotures ( 
	id                   serial primary key  ,
	idcommande           integer  NOT NULL  ,
	idutilisateur        integer  NOT NULL  ,
	dateheure            timestamp DEFAULT CURRENT_TIMESTAMP   ,
	etat                 integer DEFAULT 0   
 );

CREATE  TABLE commande_filles ( 
	id                   serial primary key  ,
	idcommande           integer  NOT NULL  ,
	idproduit            integer  NOT NULL  ,
	prix_unitaire        price  NOT NULL  ,
	quantite             quantity  NOT NULL  ,
	montant              price  NOT NULL  ,
	notes                text    ,
	etat                 integer DEFAULT 0   ,
	quantite_restante    quantity    
 );

CREATE  TABLE commande_filles_terminees ( 
	id                   serial primary key  ,
	idutilisateur        integer  NOT NULL  ,
	idcommandefille      integer  NOT NULL  ,
	dateheure            timestamp  NOT NULL  ,
	est_termine          integer DEFAULT 1   ,
	etat                 integer DEFAULT 0   
 );

CREATE  TABLE remises ( 
	id                   serial primary key  ,
	idutilisateur        integer  NOT NULL  ,
	idcommandefille      integer  NOT NULL  ,
	quantite             quantity  NOT NULL  ,
	nouveau_montant      price  NOT NULL  ,
	etat                 integer DEFAULT 0   ,
	dateheure            timestamp DEFAULT CURRENT_TIMESTAMP   
 );

CREATE  TABLE accompagnement_commandes ( 
	id                   serial primary key  ,
	idcommandefille      integer  NOT NULL  ,
	idaccompagnement     integer  NOT NULL  ,
	etat                 integer DEFAULT 0   
 );

CREATE  TABLE action_superviseurs ( 
	id                   serial primary key  ,
	idcommandefille      integer  NOT NULL  ,
	quantite             quantity  NOT NULL  ,
	dateheure            timestamp DEFAULT CURRENT_TIMESTAMP   ,
	"action"             integer  NOT NULL  ,
	etat                 integer DEFAULT 0   ,
	montant              price  NOT NULL  ,
	idutilisateur        integer  NOT NULL  
 );

CREATE  TABLE annulation_actions ( 
	id                   serial primary key  ,
	idaction             integer  NOT NULL  ,
	idutilisateur        integer  NOT NULL  ,
	dateheure            timestamp DEFAULT CURRENT_TIMESTAMP   ,
	etat                 integer DEFAULT 0   
 );

CREATE  TABLE annulation_remises ( 
	id                   serial primary key  ,
	idremise             integer  NOT NULL  ,
	idutilisateur        integer  NOT NULL  ,
	dateheure            timestamp DEFAULT CURRENT_TIMESTAMP   ,
	etat                 integer DEFAULT 0   
 );

ALTER TABLE accompagnement_commandes ADD CONSTRAINT accompagnement_commandes_idaccompagnement_fkey FOREIGN KEY ( idaccompagnement ) REFERENCES accompagnements( id );

ALTER TABLE accompagnement_commandes ADD CONSTRAINT accompagnement_commandes_idcommandefille_fkey FOREIGN KEY ( idcommandefille ) REFERENCES commande_filles( id );

ALTER TABLE accompagnement_produits ADD CONSTRAINT accompagnement_produits_idaccompagnement_fkey FOREIGN KEY ( idaccompagnement ) REFERENCES accompagnements( id );

ALTER TABLE accompagnement_produits ADD CONSTRAINT accompagnement_produits_idproduit_fkey FOREIGN KEY ( idproduit ) REFERENCES produits( id );

ALTER TABLE action_superviseurs ADD CONSTRAINT action_superviseurs_idcommandefille_fkey FOREIGN KEY ( idcommandefille ) REFERENCES commande_filles( id );

ALTER TABLE action_superviseurs ADD CONSTRAINT action_superviseurs_idutilisateur_fkey FOREIGN KEY ( idutilisateur ) REFERENCES utilisateurs( id );

ALTER TABLE annulation_actions ADD CONSTRAINT annulation_actions_idaction_fkey FOREIGN KEY ( idaction ) REFERENCES action_superviseurs( id );

ALTER TABLE annulation_actions ADD CONSTRAINT annulation_actions_idutilisateur_fkey FOREIGN KEY ( idutilisateur ) REFERENCES utilisateurs( id );

ALTER TABLE annulation_paiements ADD CONSTRAINT annulation_paiements_idpaiement_fkey FOREIGN KEY ( idpaiement ) REFERENCES paiements( id );

ALTER TABLE annulation_paiements ADD CONSTRAINT annulation_paiements_idutilisateur_fkey FOREIGN KEY ( idutilisateur ) REFERENCES utilisateurs( id );

ALTER TABLE annulation_remises ADD CONSTRAINT annulation_remises_idremise_fkey FOREIGN KEY ( idremise ) REFERENCES remises( id );

ALTER TABLE annulation_remises ADD CONSTRAINT annulation_remises_idutilisateur_fkey FOREIGN KEY ( idutilisateur ) REFERENCES utilisateurs( id );

ALTER TABLE clotures ADD CONSTRAINT clotures_idcommande_fkey FOREIGN KEY ( idcommande ) REFERENCES commandes( id );

ALTER TABLE clotures ADD CONSTRAINT clotures_idutilisateur_fkey FOREIGN KEY ( idutilisateur ) REFERENCES utilisateurs( id );

ALTER TABLE commande_filles ADD CONSTRAINT commande_filles_idcommande_fkey FOREIGN KEY ( idcommande ) REFERENCES commandes( id );

ALTER TABLE commande_filles ADD CONSTRAINT commande_filles_idproduit_fkey FOREIGN KEY ( idproduit ) REFERENCES produits( id );

ALTER TABLE commande_filles_terminees ADD CONSTRAINT commande_filles_terminees_idcommandefille_fkey FOREIGN KEY ( idcommandefille ) REFERENCES commande_filles( id );

ALTER TABLE commande_filles_terminees ADD CONSTRAINT commande_filles_terminees_idutilisateur_fkey FOREIGN KEY ( idutilisateur ) REFERENCES utilisateurs( id );

ALTER TABLE commandes ADD CONSTRAINT commandes_idplace_fkey FOREIGN KEY ( idplace ) REFERENCES places( id );

ALTER TABLE commandes ADD CONSTRAINT commandes_idutilisateur_fkey FOREIGN KEY ( idutilisateur ) REFERENCES utilisateurs( id );

ALTER TABLE historique_prix_produits ADD CONSTRAINT historique_prix_produits_idproduit_fkey FOREIGN KEY ( idproduit ) REFERENCES produits( id );

ALTER TABLE historique_prix_produits ADD CONSTRAINT historique_prix_produits_idutilisateur_fkey FOREIGN KEY ( idutilisateur ) REFERENCES utilisateurs( id );

ALTER TABLE historique_role_utilisateurs ADD CONSTRAINT historique_role_utilisateurs_idrole_fkey FOREIGN KEY ( idrole ) REFERENCES roles( id );

ALTER TABLE historique_role_utilisateurs ADD CONSTRAINT historique_role_utilisateurs_idutilisateur_fkey FOREIGN KEY ( idutilisateur ) REFERENCES utilisateurs( id );

ALTER TABLE paiements ADD CONSTRAINT paiements_idcommande_fkey FOREIGN KEY ( idcommande ) REFERENCES commandes( id );

ALTER TABLE paiements ADD CONSTRAINT paiements_idmodepaiement_fkey FOREIGN KEY ( idmodepaiement ) REFERENCES mode_paiements( id );

ALTER TABLE paiements ADD CONSTRAINT paiements_idutilisateur_fkey FOREIGN KEY ( idutilisateur ) REFERENCES utilisateurs( id );

ALTER TABLE places ADD CONSTRAINT places_idtypeplace_fkey FOREIGN KEY ( idtypeplace ) REFERENCES type_places( id );

ALTER TABLE produits ADD CONSTRAINT produits_idcategorie_fkey FOREIGN KEY ( idcategorie ) REFERENCES categories( id );

ALTER TABLE rangee_places ADD CONSTRAINT rangee_places_idplace_fkey FOREIGN KEY ( idplace ) REFERENCES places( id );

ALTER TABLE rangee_places ADD CONSTRAINT rangee_places_idrangee_fkey FOREIGN KEY ( idrangee ) REFERENCES rangees( id );

ALTER TABLE rangee_places ADD CONSTRAINT rangee_places_idutilisateur_fkey FOREIGN KEY ( idutilisateur ) REFERENCES utilisateurs( id );

ALTER TABLE rangee_utilisateurs ADD CONSTRAINT rangee_utilisateurs_idrangee_fkey FOREIGN KEY ( idrangee ) REFERENCES rangees( id );

ALTER TABLE rangee_utilisateurs ADD CONSTRAINT rangee_utilisateurs_idutilisateur_fkey FOREIGN KEY ( idutilisateur ) REFERENCES utilisateurs( id );

ALTER TABLE rangee_utilisateurs ADD CONSTRAINT rangee_utilisateurs_idutilisateur_responsable_fkey FOREIGN KEY ( idutilisateur_responsable ) REFERENCES utilisateurs( id );

ALTER TABLE remises ADD CONSTRAINT remises_idcommandefille_fkey FOREIGN KEY ( idcommandefille ) REFERENCES commande_filles( id );

ALTER TABLE remises ADD CONSTRAINT remises_idutilisateur_fkey FOREIGN KEY ( idutilisateur ) REFERENCES utilisateurs( id );

ALTER TABLE role_categorie_produits_checkings ADD CONSTRAINT role_categorie_produits_checkings_idcategorie_fkey FOREIGN KEY ( idcategorie ) REFERENCES categories( id );

ALTER TABLE role_categorie_produits_checkings ADD CONSTRAINT role_categorie_produits_checkings_idrole_fkey FOREIGN KEY ( idrole ) REFERENCES roles( id );

ALTER TABLE session_utilisateurs ADD CONSTRAINT session_utilisateurs_idutilisateur_fkey FOREIGN KEY ( idutilisateur ) REFERENCES utilisateurs( id );

ALTER TABLE stock_produits ADD CONSTRAINT stock_produits_idproduit_fkey FOREIGN KEY ( idproduit ) REFERENCES produits( id );

ALTER TABLE stock_produits ADD CONSTRAINT stock_produits_idutilisateur_fkey FOREIGN KEY ( idutilisateur ) REFERENCES utilisateurs( id );

ALTER TABLE utilisateurs ADD CONSTRAINT utilisateurs_idrole_fkey FOREIGN KEY ( idrole ) REFERENCES roles( id );

CREATE VIEW v_accompagnement_commandes AS SELECT accompagnement_commandes.id,
    accompagnement_commandes.idcommandefille,
    accompagnement_commandes.idaccompagnement,
    accompagnement_commandes.etat
   FROM accompagnement_commandes
  WHERE (accompagnement_commandes.etat = 0);

CREATE VIEW v_accompagnements AS SELECT accompagnements.id,
    accompagnements.nom,
    accompagnements.etat
   FROM accompagnements
  WHERE (accompagnements.etat = 0);

CREATE VIEW v_categories AS SELECT categories.id,
    categories.nom,
    categories.etat
   FROM categories
  WHERE (categories.etat = 0);

CREATE VIEW v_commande_filles AS SELECT commande_filles.id,
    commande_filles.idcommande,
    commande_filles.idproduit,
    commande_filles.prix_unitaire,
    commande_filles.quantite,
    commande_filles.montant,
    commande_filles.notes,
    commande_filles.etat
   FROM commande_filles
  WHERE (commande_filles.etat < 20);

CREATE VIEW v_commande_filles_commandes AS SELECT cf.id,
    cf.idcommande,
    cf.idproduit,
    cf.prix_unitaire,
    cf.quantite,
    cf.montant,
    cf.notes,
    cf.etat,
    cf.quantite_restante,
    commandes.dateheure_ouverture,
    commandes.etat AS etat_commande,
    produits.idcategorie
   FROM ((commande_filles cf
     JOIN commandes ON ((cf.idcommande = commandes.id)))
     JOIN produits ON ((cf.idproduit = produits.id)));

CREATE VIEW v_commande_filles_terminees AS SELECT commande_filles_terminees.id,
    commande_filles_terminees.idutilisateur,
    commande_filles_terminees.idcommandefille,
    commande_filles_terminees.dateheure,
    commande_filles_terminees.est_termine,
    commande_filles_terminees.etat
   FROM commande_filles_terminees
  WHERE (commande_filles_terminees.etat = 0);

CREATE VIEW v_commande_semaine AS SELECT commandes.id,
    commandes.idutilisateur,
    commandes.idplace,
    commandes.dateheure_ouverture,
    commandes.dateheure_cloture,
    commandes.montant,
    commandes.etat,
    commandes.reste_a_payer,
    commandes.montant_offert,
    commandes.montant_annulee,
    commandes.montant_remises,
    date_part('isodow'::text, (commandes.dateheure_ouverture)::date) AS jour_semaine_ouverture
   FROM commandes;

CREATE VIEW v_commandefille_accompagnements AS SELECT vcf.id,
    vcf.idcommande,
    vcf.idproduit,
    vcf.prix_unitaire,
    vcf.quantite,
    vcf.montant,
    vcf.notes,
    vcf.etat,
    accompagnements.nom AS nom_accompagnement
   FROM ((v_commande_filles vcf
     JOIN v_accompagnement_commandes vac ON ((vcf.id = vac.idcommandefille)))
     JOIN accompagnements ON ((vac.idaccompagnement = accompagnements.id)));

CREATE VIEW v_commandefille_produits AS SELECT vcf.id,
    vcf.idcommande,
    vcf.idproduit,
    vcf.prix_unitaire,
    vcf.quantite,
    vcf.montant,
    vcf.notes,
    vcf.etat,
    vp.nom,
    vp.prix,
    vp.idcategorie,
    COALESCE(vcft.est_termine, '-1'::integer) AS est_termine
   FROM ((v_commande_filles vcf
     JOIN produits vp ON ((vcf.idproduit = vp.id)))
     LEFT JOIN v_commande_filles_terminees vcft ON ((vcf.id = vcft.idcommandefille)));

CREATE VIEW v_commandes AS SELECT commandes.id,
    commandes.idutilisateur,
    commandes.idplace,
    commandes.dateheure_ouverture,
    commandes.dateheure_cloture,
    commandes.montant,
    commandes.etat,
    commandes.reste_a_payer,
    commandes.montant_offert,
    commandes.montant_annulee,
    commandes.montant_remises,
    places.nom AS nom_place,
    places.idtypeplace,
    type_places.numero AS numero_type_place
   FROM (((commandes
     JOIN places ON ((commandes.idplace = places.id)))
     JOIN type_places ON ((places.idtypeplace = type_places.id)))
     JOIN utilisateurs vu ON ((commandes.idutilisateur = vu.id)))
  WHERE (commandes.etat < 40);

CREATE VIEW v_historique_utilisateur_roles AS SELECT historique_role_utilisateurs.id,
    historique_role_utilisateurs.idutilisateur,
    historique_role_utilisateurs.idrole,
    historique_role_utilisateurs.dateheure,
    historique_role_utilisateurs.etat
   FROM historique_role_utilisateurs
  WHERE (historique_role_utilisateurs.etat = 0);

CREATE VIEW v_import_produit_categories AS SELECT ip.id,
    ip.nomproduit,
    ip.prix,
    ip.nomcategorie,
    categories.id AS idcategorie
   FROM (import_produits ip
     LEFT JOIN categories ON (((ip.nomcategorie)::text = (categories.nom)::text)));

CREATE VIEW v_import_produit_produits AS SELECT ip.id,
    ip.nomproduit,
    ip.prix,
    ip.nomcategorie,
    categories.id AS idcategorie,
    produits.id AS idproduit
   FROM ((import_produits ip
     JOIN categories ON (((ip.nomcategorie)::text = (categories.nom)::text)))
     LEFT JOIN produits ON (((ip.nomproduit)::text = (produits.nom)::text)));

CREATE VIEW v_paiement_mode_paiements AS SELECT paiements.id,
    paiements.idcommande,
    paiements.idmodepaiement,
    paiements.dateheure,
    paiements.montant,
    paiements.etat,
    paiements.idutilisateur,
    mp.nom AS nom_mode
   FROM (paiements
     JOIN mode_paiements mp ON ((paiements.idmodepaiement = mp.id)));

CREATE VIEW v_places AS SELECT places.id,
    places.nom,
    places.etat,
    places.idtypeplace
   FROM places
  WHERE (places.etat = 0);

CREATE VIEW v_produits AS SELECT produits.id,
    produits.nom,
    produits.prix,
    produits.etat,
    produits.idcategorie
   FROM produits
  WHERE (produits.etat = 0);

CREATE VIEW v_rangee_places AS SELECT rangee_places.id,
    rangee_places.idrangee,
    rangee_places.idplace,
    rangee_places.dateheure,
    rangee_places.etat,
    rangee_places.idutilisateur
   FROM rangee_places
  WHERE (rangee_places.etat = 0);

CREATE VIEW v_rangee_utilisateurs AS SELECT rangee_utilisateurs.id,
    rangee_utilisateurs.idrangee,
    rangee_utilisateurs.idutilisateur,
    rangee_utilisateurs.dateheure,
    rangee_utilisateurs.etat
   FROM rangee_utilisateurs
  WHERE (rangee_utilisateurs.etat = 0);

CREATE VIEW v_rangees AS SELECT rangees.id,
    rangees.nom,
    rangees.etat
   FROM rangees
  WHERE (rangees.etat = 0);

CREATE VIEW v_remise_commandes AS SELECT remises.id,
    remises.idutilisateur,
    remises.idcommandefille,
    remises.quantite,
    remises.nouveau_montant,
    remises.etat,
    remises.dateheure,
    commandes.id AS idcommande
   FROM ((remises
     JOIN commande_filles cf ON ((remises.idcommandefille = cf.id)))
     JOIN commandes ON ((cf.idcommande = commandes.id)));

CREATE VIEW v_role_categorie_produits_checkings AS SELECT role_categorie_produits_checkings.id,
    role_categorie_produits_checkings.idrole,
    role_categorie_produits_checkings.idcategorie,
    role_categorie_produits_checkings.etat
   FROM role_categorie_produits_checkings
  WHERE (role_categorie_produits_checkings.etat = 0);

CREATE VIEW v_roles AS SELECT roles.id,
    roles.nom,
    roles.etat,
    roles.numero
   FROM roles
  WHERE (roles.etat = 0);

CREATE VIEW v_type_places AS SELECT type_places.id,
    type_places.nom,
    type_places.etat,
    type_places.numero
   FROM type_places
  WHERE (type_places.etat = 0);

CREATE VIEW v_utilisateurs AS SELECT utilisateurs.id,
    utilisateurs.idrole,
    utilisateurs.nom,
    utilisateurs.email,
    utilisateurs.contact,
    v_roles.nom AS nom_role,
    v_roles.numero AS numero_role
   FROM (utilisateurs
     JOIN v_roles ON ((utilisateurs.idrole = v_roles.id)))
  WHERE (utilisateurs.etat = 0);

CREATE VIEW v_dateheure_max_rangee_places AS SELECT max(v_rangee_places.dateheure) AS max_dateheure
   FROM v_rangee_places;

CREATE VIEW v_dateheure_max_rangee_users AS SELECT max(v_rangee_utilisateurs.dateheure) AS max_dateheure
   FROM v_rangee_utilisateurs;

CREATE VIEW v_dateheure_max_roles AS SELECT max(v_historique_utilisateur_roles.dateheure) AS max_dateheure
   FROM v_historique_utilisateur_roles;

CREATE VIEW v_dispatch_staff_part AS SELECT vu.id,
    vu.idrangee,
    vu.idutilisateur,
    vu.dateheure,
    vu.etat,
    rangees.nom AS nom_rangee,
    utilisateurs.nom AS nom_utilisateur,
    utilisateurs.email AS email_utilisateur,
    utilisateurs.contact AS contact_utilisateur,
    vdm.max_dateheure,
    utilisateurs.idrole,
    roles.nom AS nom_role,
    roles.numero AS numero_role
   FROM ((((v_rangee_utilisateurs vu
     JOIN rangees ON ((vu.idrangee = rangees.id)))
     JOIN utilisateurs ON ((vu.idutilisateur = utilisateurs.id)))
     JOIN roles ON ((utilisateurs.idrole = roles.id)))
     LEFT JOIN v_dateheure_max_rangee_users vdm ON ((vu.dateheure = vdm.max_dateheure)));

CREATE VIEW v_arrangement_place_part AS SELECT rp.id,
    rp.idrangee,
    rp.idplace,
    rp.dateheure,
    rp.etat,
    rp.idutilisateur,
    rangees.nom AS nom_rangee,
    places.nom AS nom_place,
    type_places.nom AS nom_type_place,
    type_places.numero AS numero_type_place,
    dm.max_dateheure,
    type_places.id AS idtypeplace
   FROM ((((v_rangee_places rp
     JOIN rangees ON ((rp.idrangee = rangees.id)))
     JOIN places ON ((rp.idplace = places.id)))
     JOIN type_places ON ((places.idtypeplace = type_places.id)))
     LEFT JOIN v_dateheure_max_rangee_places dm ON ((rp.dateheure = dm.max_dateheure)));

CREATE VIEW v_attribution_roles_part AS SELECT vh.id,
    vh.idutilisateur,
    vh.idrole,
    vh.dateheure,
    vh.etat,
    utilisateurs.nom AS nom_utilisateur,
    v_roles.nom AS nom_role,
    vd.max_dateheure,
    v_roles.numero AS numero_role
   FROM (((v_historique_utilisateur_roles vh
     JOIN utilisateurs ON ((vh.idutilisateur = utilisateurs.id)))
     JOIN v_roles ON ((vh.idrole = v_roles.id)))
     LEFT JOIN v_dateheure_max_roles vd ON ((vh.dateheure = vd.max_dateheure)));

CREATE VIEW v_dispatch_staff AS SELECT v_dispatch_staff_part.id,
    v_dispatch_staff_part.idrangee,
    v_dispatch_staff_part.idutilisateur,
    v_dispatch_staff_part.dateheure,
    v_dispatch_staff_part.etat,
    v_dispatch_staff_part.nom_rangee,
    v_dispatch_staff_part.nom_utilisateur,
    v_dispatch_staff_part.email_utilisateur,
    v_dispatch_staff_part.contact_utilisateur,
    v_dispatch_staff_part.max_dateheure,
    v_dispatch_staff_part.idrole,
    v_dispatch_staff_part.nom_role,
    v_dispatch_staff_part.numero_role
   FROM v_dispatch_staff_part
  WHERE (v_dispatch_staff_part.max_dateheure IS NOT NULL);

CREATE VIEW v_serveurs_encours_1 AS SELECT v_dispatch_staff.idutilisateur,
    v_dispatch_staff.nom_utilisateur,
    v_dispatch_staff.email_utilisateur,
    v_dispatch_staff.contact_utilisateur
   FROM v_dispatch_staff
  WHERE (v_dispatch_staff.idrangee = '-1'::integer);

CREATE VIEW v_arrangement_place AS SELECT v_arrangement_place_part.id,
    v_arrangement_place_part.idrangee,
    v_arrangement_place_part.idplace,
    v_arrangement_place_part.dateheure,
    v_arrangement_place_part.etat,
    v_arrangement_place_part.idutilisateur,
    v_arrangement_place_part.nom_rangee,
    v_arrangement_place_part.nom_place,
    v_arrangement_place_part.nom_type_place,
    v_arrangement_place_part.numero_type_place,
    v_arrangement_place_part.max_dateheure,
    v_arrangement_place_part.idtypeplace
   FROM v_arrangement_place_part
  WHERE (v_arrangement_place_part.max_dateheure IS NOT NULL);

CREATE VIEW v_attribution_roles AS SELECT v_attribution_roles_part.id,
    v_attribution_roles_part.idutilisateur,
    v_attribution_roles_part.idrole,
    v_attribution_roles_part.dateheure,
    v_attribution_roles_part.etat,
    v_attribution_roles_part.nom_utilisateur,
    v_attribution_roles_part.nom_role,
    v_attribution_roles_part.max_dateheure,
    v_attribution_roles_part.numero_role
   FROM v_attribution_roles_part
  WHERE (v_attribution_roles_part.max_dateheure IS NOT NULL);

CREATE VIEW v_places_utilisateurs AS SELECT vds.id,
    vds.idrangee,
    vds.idutilisateur,
    vds.dateheure,
    vds.etat,
    vds.nom_rangee,
    vds.nom_utilisateur,
    vds.email_utilisateur,
    vds.contact_utilisateur,
    vds.max_dateheure,
    vds.idrole,
    vds.nom_role,
    vds.numero_role,
    vap.idplace,
    vap.nom_place,
    vap.nom_type_place,
    vap.numero_type_place,
    vap.idtypeplace
   FROM (v_dispatch_staff vds
     JOIN v_arrangement_place vap ON ((vds.idrangee = vap.idrangee)));

CREATE OR REPLACE VIEW v_serveurs_encours AS SELECT vu.id,
    vu.idrole,
    vu.nom,
    vu.email,
    vu.contact,
    roles.nom AS nom_role,
    roles.numero AS numero_role
   FROM ((utilisateurs vu
     JOIN roles ON ((vu.idrole = roles.id)))
     LEFT JOIN v_serveurs_encours_1 v1 ON ((vu.id = v1.idutilisateur)))
  WHERE ((v1.idutilisateur IS NULL) AND ((roles.numero)::text = ANY (ARRAY[('1'::character varying)::text, ('2'::character varying)::text])));

insert into roles values(default, 'serveur', 0, '1'),
                        (default, 'bar', 0, '2'),
                        (default, 'cuisinier', 0, '3'),
                        (default, 'caisse', 0, '4'),
                        (default, 'analyste', 0, '5'),
                        (default, 'superviseur', 0, '6'),
                        (default, 'off', 0, '7');

insert into roles values(8, 'admin', 0, '8');

insert into utilisateurs values(-1, 8, 'ADMIN', 'ADMIN', '', 'root', 0, 0),
                               (default, 6, 'Responsable_1', 'Resp_1', '', 'root', 0, 0);

insert into rangees values(0, 'Inutilisée', 0),
                          (-1, 'Off', 0);

insert into mode_paiements values(-1, 'V.A.T cadre', 0),
                                 (-2, 'V.A.T client', 0);

insert into semaine values(default, 'Lundi'),
                          (default, 'Mardi'),
                          (default, 'Mercredi'),
                          (default, 'Jeudi'),
                          (default, 'Vendredi'),
                          (default, 'Samedi'),
                          (default, 'Dimanche');                                                         

insert into type_places values(default, 'Bar', default, '1'),
                               (default, 'Salle', default, '2'),
                               (default, 'Terrasse', default, '3');

insert into mode_paiements values(0, 'espèces', default);                          