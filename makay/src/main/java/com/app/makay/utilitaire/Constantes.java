package com.app.makay.utilitaire;

public class Constantes {
    public static final String BRAND="Makay";

    public static final String MSG_UTILISATEUR_NON_IDENTIFIE="Email ou mot de passe erroné.";
    public static final String MSG_UTILISATEUR_NON_AUTHENTIFIE="Veuillez vous identifier";
    public static final String MSG_SESSION_EXPIREE="Session expirée";
    public static final String MSG_SUCCES="Succès";

    public static final String ROLE_SERVEUR="1";
    public static final String ROLE_BAR="2";
    public static final String ROLE_CUISINIER="3";
    public static final String ROLE_CAISSE="4";
    public static final String ROLE_ANALYSTE="5";
    public static final String ROLE_SUPERVISEUR="6";
    public static final String ROLE_OFF="7";

    public static final String VAR_SESSIONUTILISATEUR="utilisateur";
    public static final String VAR_TITLE="title";
    public static final String VAR_VIEWPAGE="viewpage";
    public static final String VAR_BRAND="brand";
    public static final String VAR_LOGINURL="loginurl";
    public static final String VAR_MESSAGE="message";
    public static final String VAR_CURRENTUSER="currentuser";
    public static final String VAR_PRODUITS="produits";
    public static final String VAR_PLACES="places";
    public static final String VAR_RANGEES="rangees";
    public static final String VAR_RANGEEPLACES="rangeeplaces";
    public static final String VAR_UTILISATEURS="utilisateurs";
    public static final String VAR_ROLES="roles";
    public static final String VAR_ROLEUTILISATEURS="roleutilisateurs";
    public static final String VAR_LINKS="links";
    public static final String VAR_IP="ip";
    public static final String VAR_SESSIONID="sessionid";

    public static final String PLACE_BAR="1";
    public static final String CLASSE_BAR="place-bar";
    public static final String PLACE_SALLE="2";
    public static final String CLASSE_SALLE="place-table";
    public static final String PLACE_TERRASSE="3";
    public static final String CLASSE_TERRASSE="place-terrasse";

    public static final Double SESSION_EXPIRATION=24.;
    public static final Integer SESSION_ESTINVALIDE=0;
    public static final Integer SESSION_ESTVALIDE=1;

    public static final ModelLink[] LINK_SERVEUR={
        new ModelLink("/serveur-passer-commande", "fa-solid fa-pencil", "Passer une commande")
    };
    public static final ModelLink[] LINK_SUPERVISEUR={
        new ModelLink("/plan-de-table", "fa-solid fa-chair", "Plan de table"),
        new ModelLink("/attribution-de-roles", "fa-solid fa-user", "Roles"),
        new ModelLink("/dispatch-tables-staff", "fa-solid fa-list", "Dispatching du staff")
    };
}
