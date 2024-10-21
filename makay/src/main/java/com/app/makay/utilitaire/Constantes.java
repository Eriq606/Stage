package com.app.makay.utilitaire;
import java.util.HashMap;

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
    public static final String MSG_ACTIONSUPERVISEUR_MONTANT_INVALIDE="La valeur à offrir/annuler ne peut pas être supérieur au reste à payer de la commande.";

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

    public static final ModelLink[] LINK_SERVEUR={
        new ModelLink("/serveur-passer-commande", "fa-solid fa-pencil", "Passer une commande"),
        new ModelLink("/liste-commande", "fa-solid fa-list", "Commandes en cours"),
        new ModelLink("/historique-de-commande", "fa-solid fa-clock-rotate-left", "Historique de commande")
    };
    public static final ModelLink[] LINK_SUPERVISEUR={
        new ModelLink("/plan-de-table", "fa-solid fa-chair", "Plan de table"),
        new ModelLink("/attribution-de-roles", "fa-solid fa-user", "Roles"),
        new ModelLink("/dispatch-tables-staff", "fa-solid fa-list", "Dispatching du staff"),
        new ModelLink("/monitoring-des-serveurs", "fa-solid fa-users", "Monitoring du service"),
        new ModelLink("/modifier-stock", "fa-solid fa-pencil", "Mettre le stock a jour"),
        new ModelLink("/historique-de-commande", "fa-solid fa-clock-rotate-left", "Historique de commande")
    };
    public static final ModelLink[] LINK_BARMAN={
        new ModelLink("/serveur-passer-commande", "fa-solid fa-pencil", "Passer une commande"),
        new ModelLink("/commandes-en-cours", "fa-solid fa-clock", "Commandes en cours"),
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

    public static final int IDMODEPAIEMENT_VAT=-1;

    public static final HashMap<String, String> ERREURS=new HashMap<>(){{
        put("403", "Accès non autorisé");
        put("423", "Ressource vérouillée");
    }};
}
