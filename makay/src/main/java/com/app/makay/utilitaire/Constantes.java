package com.app.makay.utilitaire;
import java.util.HashMap;

import com.app.makay.entites.Role;

public class Constantes {
    public static final String BRAND="Makay";

    public static final int CODE_ERROR=0;
    public static final int CODE_SUCCESS=10;

    public static final String MSG_UTILISATEUR_NON_IDENTIFIE="Email ou mot de passe erroné.";
    public static final String MSG_UTILISATEUR_NON_AUTHENTIFIE="Veuillez vous identifier";
    public static final String MSG_SESSION_EXPIREE="Session expirée";
    public static final String MSG_SUCCES="Succès";
    public static final String MSG_NON_AUTHORISE="Accès non authorisé";
    public static final String MSG_PAIEMENT_MONTANT_INVALIDE="Le montant du paiement est supérieur au montant de la commande.";
    public static final String MSG_COMMANDE_DEJA_CLOTUREE="La commande est déjà en attente d'addition";
    public static final String MSG_COMMANDE_VIDE="Commande vide.";
    public static final String MSG_STOCK_INSUFFISANT="Stock insuffisant";
    public static final String MSG_TABLE_NON_AUTHORISEE="Cette table n'est pas sous votre juridiction.";
    public static final String MSG_COMMANDE_NON_AUTHORISEE="Vous n'êtes pas celui qui a issu cette commande.";
    public static final String MSG_COMMANDE_INTOUCHABLE="Cette commande ne peut pas encore ou ne peut plus être altérée.";
    public static final String MSG_COMMANDEFILLE_INTOUCHABLE="Cette commande-fille ne peut plus être altérée.";
    public static final String MSG_ACTION_INVALIDE="Action invalide";
    public static final String MSG_COMMANDE_NON_TERMINEE="Commande pas encore terminée.";
    public static final String MSG_QUANTITE_INVALIDE="Quantité invalide.";
    public static final String MSG_VALEUR_INVALIDE="Valeur invalide.";
    public static final String MSG_ACTIONSUPERVISEUR_MONTANT_INVALIDE="La valeur à offrir/annuler ne peut pas être supérieur au reste à payer de la commande.";

    public static final String ROLE_SERVEUR="1";
    public static final String ROLE_BAR="2";
    public static final String ROLE_CUISINIER="3";
    public static final String ROLE_CAISSE="4";
    public static final String ROLE_ANALYSTE="5";
    public static final String ROLE_SUPERVISEUR="6";
    public static final String ROLE_OFF="7";
    public static final String ROLE_ADMIN="8";
    public static final int IDROLE_SERVEUR=1;
    public static final int IDROLE_BAR=2;
    public static final int IDROLE_CUISINIER=3;
    public static final int IDROLE_CAISSE=4;
    public static final int IDROLE_ANALYSTE=5;
    public static final int IDROLE_SUPERVISEUR=6;
    public static final int IDROLE_OFF=7;
    public static final int IDROLE_ADMIN=8;

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
    public static final String VAR_COMMANDESFILLES="commandefilles";
    public static final String VAR_COMMANDES="commandes";
    public static final String VAR_COMMANDE="commande";
    public static final String VAR_INUTILISEEID="inutiliseeid";
    public static final String VAR_OFFID="offid";
    public static final String VAR_SERVEURS="serveurs";
    public static final String VAR_RESETCACHE="resetcache";
    public static final String VAR_RECEIVENOTIFY="receivenotify";
    public static final String VAR_INDICE_PAGINATION="indicepagination";
    public static final String VAR_TABLE="table";
    public static final String VAR_MODEPAIEMENTS="modepaiements";
    public static final String VAR_QUERYSTRING="querystring";
    public static final String VAR_SUPERVISEURNUMERO="superviseurnumero";
    public static final String VAR_MODEPAIEMENTS_CHOISIS="modepaiements_choisis";
    public static final String VAR_COMMANDEFILLE_OFFERT="offert";
    public static final String VAR_COMMANDEFILLE_ANNULEE="annulee";
    public static final String VAR_CODE_ERREUR="code";
    public static final String VAR_MSG_ERREUR="message";
    public static final String VAR_ACTION_SUPERVISEUR="actions";
    public static final String VAR_PAGINATION_LIMIT="paginationlimit";
    public static final String VAR_CATEGORIES="categories";
    public static final String VAR_NOTIF_PATH="notification";
    public static final String VAR_REMISE="remises";
    public static final String VAR_UTILISATEURS_SUPPR="utilisateurs_suppr";
    public static final String VAR_PRODUITS_SUPPR="produits_suppr";
    public static final String VAR_CATEGORIES_SUPPR="categories_suppr";
    public static final String VAR_RANGEES_SUPPR="rangees_suppr";
    public static final String VAR_PLACES_SUPPR="places_suppr";
    public static final String VAR_MODESPAIEMENT_SUPPR="modespaiement_suppr";
    public static final String VAR_CODECRUD_SUPPR="code_suppr";
    public static final String VAR_CODECRUD_RESTAURER="code_restaurer";
    public static final String VAR_CODECRUD_AJOUT="code_ajout";
    public static final String VAR_CODECRUD_MODIF="code_modif";
    public static final String VAR_TYPEPLACES="types_places";
    public static final String VAR_CHIFFRE_SEMAINE="chiffre_semaine";
    public static final String VAR_CHIFFRE_PRODUIT="chiffre_produit";
    public static final String VAR_CHIFFRE_PLACE_VISITEES="chiffre_place_visitees";
    public static final String VAR_CHIFFRE_PLACE_NON_VISITEES="chiffre_place_non_visitees";
    public static final String VAR_CHIFFRE_TOTAUX="chiffre_totaux";
    public static final String VAR_CHIFFRE_PAIEMENTS="chiffre_paiements";
    public static final String VAR_JOURS_SEMAINE="jours_semaine";
    public static final String VAR_ETAT_COMMANDEFILLE_TERMINEE="etat_termine";

    public static final String PLACE_BAR="1";
    public static final String CLASSE_BAR="place-bar";
    public static final String LABEL_BAR="Bar";
    public static final String PLACE_SALLE="2";
    public static final String CLASSE_SALLE="place-table";
    public static final String LABEL_SALLE="Table";
    public static final String PLACE_TERRASSE="3";
    public static final String CLASSE_TERRASSE="place-terrasse";
    public static final String LABEL_TERRASSE="Terrasse";

    public static final int INUTILISEE_ID=0;
    public static final int OFF_ID=-1;

    public static final Double SESSION_EXPIRATION=24.;
    public static final Integer SESSION_ESTINVALIDE=0;
    public static final Integer SESSION_ESTVALIDE=1;

    public static final int COMMANDE_CREEE=0;
    public static final int COMMANDE_ADDITION=10;
    public static final int COMMANDE_PAYEE=20;
    public static final int COMMANDE_ANNULEE=30;
    public static final int COMMANDE_SUPPRIMEE=40;

    public static final int COMMANDEFILLE_CREEE=0;
    public static final String COMMANDEFILLE_CREEE_LABEL="Creee";
    public static final int COMMANDEFILLE_OFFERT=10;
    public static final String COMMANDEFILLE_OFFERT_LABEL="Offert";
    public static final int COMMANDEFILLE_ANNULEE=-10;
    public static final String COMMANDEFILLE_ANNULEE_LABEL="Annulee";
    public static final int COMMANDEFILLE_SUPPRIMEE=20;
    public static final String COMMANDEFILLE_SUPPRIMEE_LABEL="Supprimee";

    public static final int UTILISATEUR_CREE=0;
    public static final int UTILISATEUR_SUPPRIME=10;

    public static final int PRODUIT_CREE=0;
    public static final int PRODUIT_SUPPRIME=10;

    public static final int RANGEE_CREE=0;
    public static final int RANGEE_SUPPRIME=10;

    public static final int PLACE_CREE=0;
    public static final int PLACE_SUPPRIME=10;

    public static final int TYPEPLACE_CREE=0;
    public static final int TYPEPLACE_SUPPRIME=10;

    public static final int CATEGORIE_CREE=0;
    public static final int CATEGORIE_SUPPRIME=10;

    public static final int PAIEMENT_CREE=0;
    public static final int PAIEMENT_ANNULE=10;

    public static final int ACTION_CREE=0;
    public static final int ACTION_ANNULE=10;

    public static final int REMISE_CREE=0;
    public static final int REMISE_ANNULE=10;

    public static final int MODEPAIEMENT_CREE=0;
    public static final int MODEPAIEMENT_SUPPRIME=10;

    public static final ModelLink[] LINK_SERVEUR={
        new ModelLink("/serveur-passer-commande", "fa-solid fa-pencil", "Passer une commande"),
        new ModelLink("/liste-commande", "fa-solid fa-list", "Commandes en cours"),
        new ModelLink("/historique-de-commande", "fa-solid fa-clock-rotate-left", "Historique de commande")
    };
    public static final ModelLink[] LINK_SUPERVISEUR={
        new ModelLink("/plan-de-table", "fa-solid fa-chair", "Plan de table"),
        new ModelLink("/attribution-de-roles", "fa-solid fa-user", "Roles"),
        new ModelLink("/dispatch-tables-staff", "fa-solid fa-list", "Dispatching du staff"),
        new ModelLink("/demande-addition", "fa-solid fa-clock", "En attente d'addition"),
        new ModelLink("/monitoring-des-serveurs", "fa-solid fa-users", "Monitoring du service"),
        new ModelLink("/modifier-stock", "fa-solid fa-pencil", "Mettre le stock a jour"),
        new ModelLink("/historique-de-commande", "fa-solid fa-clock-rotate-left", "Historique de commande")
    };
    public static final ModelLink[] LINK_BARMAN={
        new ModelLink("/commandes-en-cours", "fa-solid fa-clock", "Commandes en cours"),
        new ModelLink("/serveur-passer-commande", "fa-solid fa-pencil", "Passer une commande"),
        new ModelLink("/liste-commande", "fa-solid fa-list", "Mes commandes"),
        new ModelLink("/modifier-stock", "fa-solid fa-pencil", "Mettre le stock a jour"),
        new ModelLink("/historique-de-commande", "fa-solid fa-clock-rotate-left", "Historique de commande")
    };
    public static final ModelLink[] LINK_CAISSIER={
        new ModelLink("/demande-addition", "fa-solid fa-clock", "En attente d'addition"),
        new ModelLink("/historique-de-commande", "fa-solid fa-clock-rotate-left", "Historique de commande")
    };
    public static final ModelLink[] LINK_CUISINIER={
        new ModelLink("/commandes-en-cours", "fa-solid fa-clock", "Commandes en cours"),
        new ModelLink("/modifier-stock", "fa-solid fa-pencil", "Mettre le stock a jour"),
        new ModelLink("/historique-de-commande", "fa-solid fa-clock-rotate-left", "Historique de commande")
    };
    public static final ModelLink[] LINK_ADMIN={
        new ModelLink("/utilisateurs", "fa-solid fa-users", "Utilisateurs"),
        new ModelLink("/produits", "fa-solid fa-utensils", "Produits"),
        new ModelLink("/categories", "fa-solid fa-list", "Categories"),
        new ModelLink("/rangees", "fa-solid fa-list", "Rangées"),
        new ModelLink("/places", "fa-solid fa-chair", "Places"),
        new ModelLink("/modes-paiement", "fa-solid fa-wallet", "Modes de paiement"),
    };
    public static final ModelLink[] LINK_ANALYSTE={
        new ModelLink("/tableau-bord", "fa-solid fa-tachometer", "Tableau de bord"),
        new ModelLink("/historique-de-commande", "fa-solid fa-clock-rotate-left", "Historique de commande")
    };

    public static final int PAGINATION_LIMIT=5;

    public static final String URL_RESET_CACHE_SERVEUR="reset-cache-serveur";
    public static final String URL_RECEIVE_NOTIFY_SERVEUR="receive-notify-redirect-serveur";
    public static final String URL_RESET_CACHE_SUPERVISEUR="reset-cache-superviseur";
    public static final String URL_RECEIVE_NOTIFY_SUPERVISEUR="receive-notify-redirect-superviseur";
    public static final String URL_RESET_CACHE_BARMAN="reset-cache-barman";
    public static final String URL_RECEIVE_NOTIFY_BARMAN="receive-notify-redirect-barman";
    public static final String URL_RESET_CACHE_CAISSIER="reset-cache-caissier";
    public static final String URL_RECEIVE_NOTIFY_CAISSIER="receive-notify-redirect-caissier";

    public static final String ETATCOMMANDE_PAYEE="Payée";
    public static final String COULEUR_SUCCES="#70ff9b";
    public static final String ETATCOMMANDE_ANNULEE="Annulée";
    public static final String COULEUR_CANCELED="#ff7171";

    public static final int COMMANDEFILLE_ESTTERMINEE=1;
    public static final int COMMANDEFILLE_ESTENCOURS=-1;

    public static final int IDMODEPAIEMENT_VAT_CADRE=-1;
    public static final int IDMODEPAIEMENT_VAT_CLIENT=-2;

    public static final HashMap<String, String> ERREURS=new HashMap<>(){{
        put("403", "Accès non autorisé");
        put("423", "Ressource vérouillée");
        put("404", "Ressource introuvable");
    }};

    public static final String SND_NOTIFICATION="snd/notif.mp3";

    public static final int CRUD_SUPPRESSION=0;
    public static final int CRUD_RESTAURATION=10;
    public static final int CRUD_AJOUT=20;
    public static final int CRUD_MODIFICATION=30;

    public static final String ACCOMP_SEPARATOR=",";

    public static final Role[] ROLES_AUTORISES={
        new Role(2, ROLE_BAR, "Barman"),
        new Role(3, ROLE_CUISINIER, "Cuisinier"),
        new Role(6, ROLE_SUPERVISEUR, "Superviseur")
    };
    // public static final HashMap<Integer, String> JOURS_SEMAINE=new HashMap<>(){{
    //     put(1, "Lundi");
    //     put(2, "Mardi");
    //     put(3, "Mercredi");
    //     put(4, "Jeudi");
    //     put(5, "Vendredi");
    //     put(6, "Samedi");
    //     put(7, "Dimanche");
    // }};
    public static final String[] JOURS_SEMAINE={
        "Lundi",
        "Mardi",
        "Mercredi",
        "Jeudi",
        "Vendredi",
        "Samedi",
        "Dimanche",
    };
    public static final int LIMIT_CLASSEMENT_PAIEMENT=4;
    public static final String STAT_COULEUR_1="#0062ff";
    public static final String STAT_COULEUR_2="#00ff7b";
    public static final String STAT_COULEUR_3="#dc143c";
    public static final String STAT_COULEUR_4="#ffa500";
    public static final String STAT_COULEUR_5="#808080";
}
