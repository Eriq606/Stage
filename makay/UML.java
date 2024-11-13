public class UML{
    public class Accompagnement {
        private Integer id;
        private String nom;
        private Integer etat;
    }
    
    public class ActionSuperviseur {
        private Integer id;
        private CommandeFille commandeFille;
        private Double quantite;
        private LocalDateTime dateheure;
        private Integer action;
        private Integer etat;
        private Double montant;
        private Utilisateur utilisateur;
    }
    
    public class Categorie {
        private Integer id;
        private String nom;
        private Integer etat;
        private Role[] roles;
    }
    
    public class Cloture {
        private Integer id;
        private Commande commande;
        private Utilisateur utilisateur;
        private LocalDateTime dateheure;
        private Integer etat;
    }
    
    public class Commande {
        private Integer id;
        private Utilisateur utilisateur;
        private Place place;
        private LocalDateTime ouverture;
        private LocalDateTime cloture;
        private Double montant;
        private Integer etat;
        private CommandeFille[] commandeFilles;
        private Double resteAPayer;
        private Paiement[] paiements;
        private Double montantOffert;
        private Double montantAnnulee;
        private ActionSuperviseur[] actions;
        private double paiementTotal;
        private double offertTotales;
        private double annuleTotales;
        private Double montantRemises;
        private Remise[] remises;
    }
    
    public class CommandeEnCours{
        @PrimaryKey
        @Column("id")
        private Integer id;
        @ForeignKey(recursive = false)
        @Column("idutilisateur")
        private Utilisateur utilisateur;
        @ForeignKey(recursive = true)
        @Column("idplace")
        private Place place;
        @Column("nom_place")
        private String nomPlace;
        @Column("dateheure_ouverture")
        private LocalDateTime ouverture;
        @Column("dateheure_cloture")
        private LocalDateTime cloture;
        @Column("montant")
        private Double montant;
        @Column("etat")
        private Integer etat;
        private CommandeFilleEnCours[] commandeFilles;
        @Column("reste_a_payer")
        private Double resteAPayer;
        private Paiement[] paiements;
        private ActionSuperviseur[] actions;
        private double paiementTotal;
        private double offertTotales;
        private double annuleTotales;
        private Remise[] remises;
        private double montantRemises;
    }

    public class CommandeFille {
        @PrimaryKey
        @Column("id")
        private Integer id;
        @ForeignKey(recursive = true)
        @Column("idcommande")
        private Commande commande;
        @ForeignKey(recursive = true)
        @Column("idproduit")
        private Produit produit;
        @Column("prix_unitaire")
        private Double pu;
        @Column("quantite")
        private Double quantite;
        @Column("montant")
        private Double montant;
        @Column("notes")
        private String notes;
        @Column("etat")
        private Integer etat;
        private Accompagnement[] accompagnements;
        @Column("quantite_restante")
        private Double quantiteRestante;
    }

    public class CommandeFilleEnCours {
        @PrimaryKey
        @Column("id")
        private Integer id;
        @ForeignKey(recursive = true)
        @Column("idcommande")
        private CommandeEnCours commande;
        @ForeignKey(recursive = true)
        @Column("idproduit")
        private Produit produit;
        @Column("prix_unitaire")
        private Double pu;
        @Column("quantite")
        private Double quantite;
        @Column("montant")
        private Double montant;
        @Column("notes")
        private String notes;
        @Column("etat")
        private Integer etat;
        private Accompagnement[] accompagnements;
        private int estTermine;
    }

    public class CommandeFilleTerminee {
        @PrimaryKey
        @Column("id")
        private Integer id;
        @ForeignKey(recursive = false)
        @Column("idutilisateur")
        private Utilisateur utilisateur;
        @ForeignKey(recursive = false)
        @Column("idcommandefille")
        private CommandeFille commandeFille;
        @Column("dateheure")
        private LocalDateTime dateheure;
        @Column("est_termine")
        private Integer estTermine;
        @Column("etat")
        private Integer etat;
    }

    public class HistoriquePrixProduit {
        @PrimaryKey
        @Column("id")
        private Integer id;
        @ForeignKey(recursive = true)
        @Column("idproduit")
        private Produit produit;
        @ForeignKey(recursive = true)
        @Column("idutilisateur")
        private Utilisateur utilisateur;
        @Column("prix")
        private Double prix;
        @Column("dateheure")
        private LocalDateTime dateheure;
        @Column("etat")
        private Integer etat;
    }

    public class HistoriqueRoleUtilisateur {
        @PrimaryKey
        @Column("id")
        private Integer id;
        @ForeignKey(recursive = true)
        @Column("idutilisateur")
        private Utilisateur utilisateur;
        @ForeignKey(recursive = true)
        @Column("idrole")
        private Role role;
        @Column("dateheure")
        private LocalDateTime dateheure;
        @Column("etat")
        private Integer etat;
    }

    public class ImportProduit {
        @PrimaryKey
        @Column("id")
        private Integer id;
        @Column("nomproduit")
        private String nomProduit;
        @Column("prix")
        private Double prix;
        @Column("nomcategorie")
        private String nomCategorie;
    }

    public class ModePaiement {
        @PrimaryKey
        @Column("id")
        private Integer id;
        @Column("nom")
        private String nom;
        @Column("etat")
        private Integer etat;
    }

    public class ModificationStock {
        @PrimaryKey
        @Column("id")
        private Integer id;
        @ForeignKey(recursive = true)
        @Column("idproduit")
        private Produit produit;
        @ForeignKey(recursive = false)
        @Column("idutilisateur")
        private Utilisateur utilisateur;
        @Column("stock")
        private Double stock;
        @Column("dateheure")
        private LocalDateTime dateheure;
        @Column("etat")
        private Integer etat;
    }

    public class Paiement {
        @PrimaryKey
        @Column("id")
        private Integer id;
        @ForeignKey(recursive = true)
        @Column("idcommande")
        private Commande commande;
        @ForeignKey(recursive = true)
        @Column("idmodepaiement")
        private ModePaiement modePaiement;
        @Column("dateheure")
        private LocalDateTime dateheure;
        @Column("montant")
        private Double montant;
        @Column("etat")
        private Integer etat;
        @ForeignKey(recursive = true)
        @Column("idutilisateur")
        private Utilisateur utilisateur;
    }

    public class Place {
        @PrimaryKey
        @Column("id")
        private Integer id;
        @Column("nom")
        private String nom;
        @Column("etat")
        private Integer etat;
        @ForeignKey(recursive = true)
        @Column("idtypeplace")
        private TypePlace typePlace;
        private String classeHTML;
    }

    public class Produit {
        @PrimaryKey
        @Column("id")
        private Integer id;
        @Column("nom")
        private String nom;
        @Column("prix")
        private Double prix;
        @Column("etat")
        private Integer etat;
        @ForeignKey(recursive = true)
        @Column("idcategorie")
        private Categorie categorie;
        private Accompagnement[] accompagnements;
        @Column("dernier_stock")
        private Double dernierStock;
    }

    public class Rangee {
        @PrimaryKey
        @Column("id")
        private Integer id;
        @Column("nom")
        private String nom;
        @Column("etat")
        private Integer etat;
        private Utilisateur[] utilisateurs;
        private Place[] places;
    }

    public class Relance {
        private Utilisateur utilisateur;
        private Place place;
        private LocalDateTime dateheure;
        private CommandeFille commandeFille;
    }

    public class Remise {
        @PrimaryKey
        @Column("id")
        private Integer id;
        @ForeignKey(recursive = true)
        @Column("idutilisateur")
        private Utilisateur utilisateur;
        @ForeignKey(recursive = true)
        @Column("idcommandefille")
        private CommandeFille commandeFille;
        @Column("quantite")
        private Double quantite;
        @Column("nouveau_montant")
        private Double nouveauMontant;
        @Column("etat")
        private Integer etat;
        @Column("dateheure")
        private LocalDateTime dateheure;
        private String commandeLabel;
        private String taux;
    }

    public class Role {
        @PrimaryKey
        @Column("id")
        private Integer id;
        @Column("numero")
        private String numero;
        @Column("nom")
        private String nom;
        @Column("etat")
        private Integer etat;
    }
    /* 
        Accompagnement
        ActionSuperviseur
        Categorie
        Cloture
        Commande
        CommandeEnCours
        CommandeFille
        CommandeFilleEnCours
        CommandeFilleTerminee
        HistoriquePrixProduit
        HistoriqueRoleUtilisateur
        ImportProduit
        ModePaiement
        ModificationStock
        Paiement
        Place
        Produit
        Rangee
        Relance
        Remise
        Role
        TypePlace
        Utilisateur
        UtilisateurSafe

AnnulationAction
AnnulationPaiement
AnnulationRemise

AccompagnementCommande
AccompagnementProduit
ActionPaiement
RangeePlace
RangeeUtilisateur
RoleChecking

ActionSuperviseurREST
AnnulerActionREST
AnnulerPaiementREST
AnnulerRemiseREST
CheckCommandeFilleREST
ClotureREST
DemandeAdditionREST
EnvoiCommandeREST
EnvoiREST
ModificationDispatchREST
ModificationRoleREST
ModificationStockREST
ObjectREST
PayerCommandeREST
RechercheProduitREST
RemiseREST

ChiffrePaiement
ChiffreProduit
ChiffreSemaine

LoginController
DefaultController
ErrorController
AdminController
AnalysteController
BarmanController
CaissierController
ServeurController
SuperviseurController
SuperviseurRESTController

Constantes
ModelLink
MyDAO
MyFilter
ReponseREST
RestData
SessionUtilisateur
WebSocketConfig
*/
}