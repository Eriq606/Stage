CREATE OR REPLACE VIEW v_arrangement_place_part AS SELECT rp.id,
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

CREATE OR REPLACE VIEW v_arrangement_place AS SELECT v_arrangement_place_part.id,
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

CREATE OR REPLACE VIEW v_attribution_roles_part AS SELECT vh.id,
    vh.idutilisateur,
    vh.idrole,
    vh.dateheure,
    vh.etat,
    v_utilisateurs.nom AS nom_utilisateur,
    roles.nom AS nom_role,
    vd.max_dateheure,
    roles.numero AS numero_role
   FROM (((v_historique_utilisateur_roles vh
     JOIN v_utilisateurs ON ((vh.idutilisateur = v_utilisateurs.id)))
     JOIN roles ON ((vh.idrole = roles.id)))
     LEFT JOIN v_dateheure_max_roles vd ON ((vh.dateheure = vd.max_dateheure)));

CREATE OR REPLACE VIEW v_attribution_roles AS SELECT v_attribution_roles_part.id,
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