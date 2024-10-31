package com.app.makay.entites;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import com.app.makay.entites.annulation.AnnulationAction;
import com.app.makay.entites.annulation.AnnulationPaiement;
import com.app.makay.entites.annulation.AnnulationRemise;
import com.app.makay.entites.liaison.AccompagnementCommande;
import com.app.makay.entites.liaison.AccompagnementProduit;
// import com.app.makay.entites.liaison.ActionPaiement;
import com.app.makay.entites.liaison.RangeePlace;
import com.app.makay.entites.liaison.RangeeUtilisateur;
import com.app.makay.entites.liaison.RoleChecking;
import com.app.makay.entites.temporary.ProduitCommandeStock;
import com.app.makay.entites.temporary.ProduitCommandeStockForSelect;
import com.app.makay.iris.IrisUser;
import com.app.makay.utilitaire.Constantes;
import com.app.makay.utilitaire.ModelLink;
import com.app.makay.utilitaire.MyDAO;
import com.app.makay.utilitaire.SessionUtilisateur;
import com.app.makay.utilitaire.exception.StockException;

import handyman.HandyManUtils;
import jakarta.servlet.http.HttpSession;
import veda.EntityTable;
import veda.godao.DAO;
import veda.godao.annotations.Column;
import veda.godao.annotations.ForeignKey;
import veda.godao.annotations.PrimaryKey;
import veda.godao.annotations.Table;

@Table("utilisateurs")
public class Utilisateur extends IrisUser{
    @PrimaryKey
    @Column("id")
    private Integer id;
    @Column("nom")
    private String nom;
    @Column("email")
    private String email;
    @Column("contact")
    private String contact;
    @Column("motdepasse")
    private String motdepasse;
    @ForeignKey(recursive = true)
    @Column("idrole")
    private Role role;
    @Column("etat")
    private Integer etat;
    @Column("autorisation")
    private Integer autorisation;
    private Place[] places;
    private CommandeEnCours[] commandes;
    
    public CommandeEnCours[] getCommandes() {
        return commandes;
    }
    public void setCommandes(CommandeEnCours[] commandes) {
        this.commandes = commandes;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getContact() {
        return contact;
    }
    public void setContact(String contact) {
        this.contact = contact;
    }
    public String getMotdepasse() {
        return motdepasse;
    }
    public void setMotdepasse(String motdepasse) {
        this.motdepasse = motdepasse;
    }
    public Integer getEtat() {
        return etat;
    }
    public void setEtat(Integer etat) {
        this.etat = etat;
    }
    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }
    public ModelLink[] recupererLinks(){
        ModelLink[] links=null;
        switch(getRole().getNumero()){
            case Constantes.ROLE_SERVEUR:
                links=Constantes.LINK_SERVEUR;
                break;
            case Constantes.ROLE_BAR:
                links=Constantes.LINK_BARMAN;
                break;
            case Constantes.ROLE_SUPERVISEUR:
                links=Constantes.LINK_SUPERVISEUR;
                break;
            case Constantes.ROLE_CAISSE:
                links=Constantes.LINK_CAISSIER;
                break;
            case Constantes.ROLE_CUISINIER:
                links=Constantes.LINK_CUISINIER;
            case Constantes.ROLE_ADMIN:
                links=Constantes.LINK_ADMIN;
                break;
        }
        return links;
    }
    public String[] recupererResetCacheAndNotify(){
        String[] responses=new String[2];
        switch(getRole().getNumero()){
            case Constantes.ROLE_SERVEUR:
                responses[0]=Constantes.URL_RESET_CACHE_SERVEUR;
                responses[1]=Constantes.URL_RECEIVE_NOTIFY_SERVEUR;
                break;
            case Constantes.ROLE_BAR:
                responses[0]=Constantes.URL_RESET_CACHE_BARMAN;
                responses[1]=Constantes.URL_RECEIVE_NOTIFY_BARMAN;
                break;
            case Constantes.ROLE_SUPERVISEUR:
                responses[0]=Constantes.URL_RESET_CACHE_SUPERVISEUR;
                responses[1]=Constantes.URL_RECEIVE_NOTIFY_SUPERVISEUR;
                break;
            case Constantes.ROLE_CUISINIER:
                responses[0]=Constantes.URL_RESET_CACHE_BARMAN;
                responses[1]=Constantes.URL_RECEIVE_NOTIFY_BARMAN;
                break;
            case Constantes.ROLE_CAISSE:
                responses[0]=Constantes.URL_RESET_CACHE_CAISSIER;
                responses[1]=Constantes.URL_RECEIVE_NOTIFY_CAISSIER;
                break;
        }
        return responses;
    }
    public static Utilisateur seConnecter(MyDAO dao, Connection connect, String email, String motDePasse) throws Exception{
        Utilisateur utilisateur=null;
        String addOn="where etat=0 and email='%s' and motdepasse='%s' and id>0";
        addOn=String.format(addOn, email.replace("'", "''"), motDePasse.replace("'", "''"));
        // Utilisateur where=new Utilisateur();
        // where.setEmail(email);
        // where.setMotdepasse(motDePasse);
        // where.setEtat(0);
        Utilisateur[] target=dao.select(connect, Utilisateur.class, addOn);
        if(target.length==1){
            utilisateur=target[0];
            utilisateur.setMotdepasse("*******");
            utilisateur.setIrisRole(utilisateur.getRole().getNumero());
            utilisateur.setIrisAuthorization(utilisateur.getAutorisation());
            utilisateur.getPlacesActuels(connect, dao);
        }
        return utilisateur;
    }
    public static Utilisateur seConnecterAdmin(MyDAO dao, Connection connect, String email, String motDePasse) throws Exception{
        Utilisateur utilisateur=null;
        String addOn="where etat=0 and email='%s' and motdepasse='%s' and id=-1";
        addOn=String.format(addOn, email.replace("'", "''"), motDePasse.replace("'", "''"));
        // Utilisateur where=new Utilisateur();
        // where.setEmail(email);
        // where.setMotdepasse(motDePasse);
        // where.setEtat(0);
        Utilisateur[] target=dao.select(connect, Utilisateur.class, addOn);
        if(target.length==1){
            utilisateur=target[0];
            utilisateur.setMotdepasse("*******");
            utilisateur.setIrisRole(utilisateur.getRole().getNumero());
            utilisateur.setIrisAuthorization(utilisateur.getAutorisation());
            utilisateur.getPlacesActuels(connect, dao);
        }
        return utilisateur;
    }
    public void seDeconnecter(Connection connect, MyDAO dao, HttpSession session) throws Exception{
        try{
            SessionUtilisateur where=new SessionUtilisateur();
            where.setSessionId(session.getId());
            where.setUtilisateur(this);
            SessionUtilisateur change=new SessionUtilisateur();
            change.setExpiration(LocalDateTime.now());
            change.setEstValide(Constantes.SESSION_ESTINVALIDE);
            dao.update(connect, change, where);
        }catch(Exception e){
            connect.rollback();
            throw e;
        }
    }
    public Integer getAutorisation() {
        return autorisation;
    }
    public void setAutorisation(Integer autorisation) {
        this.autorisation = autorisation;
    }
    public void mettreAJourPlanTable(Connection connect, MyDAO dao, String arrangementsString) throws Exception{
        try{
            RangeePlace[] arrangements=HandyManUtils.fromJson(RangeePlace[].class, arrangementsString);
            for(RangeePlace r:arrangements){
                r.setUtilisateur(this);
            }
            dao.insertWithoutPrimaryKey(connect, RangeePlace.class, arrangements);
        }catch(Exception e){
            connect.rollback();
            throw e;
        }
    }
    public void mettreAJourAttributionsRoles(Connection connect, MyDAO dao, HistoriqueRoleUtilisateur[] attributions) throws Exception{
        try{
            Utilisateur where, change;
            dao.insertWithoutPrimaryKey(connect, HistoriqueRoleUtilisateur.class, attributions);
            for(HistoriqueRoleUtilisateur h:attributions){
                where=h.getUtilisateur();
                change=new Utilisateur();
                change.setRole(h.getRole());
                dao.update(connect, change, where);
            }
        }catch(Exception e){
            connect.rollback();
            throw e;
        }
    }
    public void mettreAJourDispatchStaff(Connection connect, MyDAO dao, RangeeUtilisateur[] rangeesUser) throws Exception{
        try{
            for(RangeeUtilisateur r:rangeesUser){
                r.setUtilisateurResponsable(this);
            }
            dao.insertWithoutPrimaryKey(connect, RangeeUtilisateur.class, rangeesUser);
        }catch(Exception e){
            connect.rollback();
            throw e;
        }
    }

    public Role getRoleActuel(Connection connect, DAO dao) throws Exception{
        // String query="select idrole, nom_role, numero_role from v_attribution_roles where idutilisateur="+getId();
        String query="select idrole, nom_role, numero_role from v_utilisateurs where id="+getId();
        HashMap<String, Object>[] objets=dao.select(connect, query);
        Role role=null;
        if(objets.length!=1){
            return role;
        }
        role=new Role();
        role.setId((int)objets[0].get("idrole"));
        role.setNom((String)objets[0].get("nom_role"));
        role.setNumero((String)objets[0].get("numero_role"));
        setRole(role);
        setIrisRole(role.getNumero());
        return role;
    }
    public Place[] getPlacesActuels(Connection connect, MyDAO dao) throws Exception{
        String query="select idplace, nom_place, idtypeplace, nom_type_place, numero_type_place from v_places_utilisateurs where idutilisateur="+getId();
        HashMap<String, Object>[] objets=dao.select(connect, query);
        Place[] places=new Place[objets.length];
        TypePlace type;
        for(int i=0;i<places.length;i++){
            places[i]=new Place();
            places[i].setId((int)objets[i].get("idplace"));
            places[i].setNom((String)objets[i].get("nom_place"));
            type=new TypePlace();
            type.setId((int)objets[i].get("idtypeplace"));
            type.setNom((String)objets[i].get("nom_type_place"));
            type.setNumero((String)objets[i].get("numero_type_place"));
            places[i].setTypePlace(type);
        }
        setPlaces(places);
        return places;
    }
    public void creerTableFictiveVerificationStock(Connection connect, MyDAO dao, CommandeFille[] commandeFilles) throws Exception{
        EntityTable table=new EntityTable();
        table.setNom("table_fictive_stock");
        table.getColonnes().put("id", "serial primary key");
        table.getColonnes().put("idproduit", "int not null");
        table.getColonnes().put("quantite", "quantity not null");
        table.getColonnes().put("nom_produit", "varchar not null");
        try{
            dao.createTable(connect, table, true);
            LinkedList<ProduitCommandeStock> liste=new LinkedList<>();
            ProduitCommandeStock produitCommande;
            for(CommandeFille c:commandeFilles){
                produitCommande=new ProduitCommandeStock(c.getProduit().getId(), c.getQuantite(), c.getProduit().getNom());
                liste.add(produitCommande);
            }
            dao.insertWithoutPrimaryKey(connect, ProduitCommandeStock.class, liste.toArray(new ProduitCommandeStock[liste.size()]));
        }catch(Exception e){
            connect.rollback();
            throw e;
        }
    }
    public ModificationStock[] verifierStock(Connection connect, MyDAO dao) throws Exception{
        String query="select tf.idproduit, sum(tf.quantite) as quantite, tf.nom_produit, produits.dernier_stock from table_fictive_stock tf join produits on tf.idproduit=produits.id group by tf.idproduit, tf.nom_produit, produits.dernier_stock";
        ProduitCommandeStockForSelect[] qteCommandes=dao.select(connect, query, ProduitCommandeStockForSelect.class);
        LinkedList<Exception> exceptions=new LinkedList<>();
        LinkedList<ModificationStock> modifStocks=new LinkedList<>();
        ModificationStock modif;
        Produit produit;
        double nouveauStock;
        for(ProduitCommandeStockForSelect p:qteCommandes){
            if(p.getQuantite()>p.getStock()&&p.getStock()>=0){
                exceptions.add(new Exception(Constantes.MSG_STOCK_INSUFFISANT+". "+p.getNomProduit()+" : Qte demandee "+p.getQuantite()+"| En stock "+p.getStock()));
                continue;
            }
            if(p.getStock()<0){
                continue;
            }
            nouveauStock=p.getStock()-p.getQuantite();
            produit=new Produit();
            produit.setId(p.getIdproduit());
            modif=new ModificationStock();
            modif.setProduit(produit);
            modif.setStock(nouveauStock);
            modif.setUtilisateur(this);
            modifStocks.add(modif);
        }
        if(exceptions.size()>0){
            throw new StockException(exceptions);
        }
        return modifStocks.toArray(new ModificationStock[modifStocks.size()]);
    }
    public int passerCommande(Connection connect, MyDAO dao, Commande commande, CommandeFille[] commandeFilles) throws Exception{
        try{
            boolean estValide=false;
            creerTableFictiveVerificationStock(connect, dao, commandeFilles);
            ModificationStock[] modifStocks= verifierStock(connect, dao);
            for(CommandeFille c:commandeFilles){
                if(c.getQuantite()>0&&estValide==false){
                    estValide=true;
                    break;
                }
            }
            if(commandeFilles.length==0||estValide==false){
                throw new Exception(Constantes.MSG_COMMANDE_VIDE);
            }
            boolean placeValide=dao.exists(connect, "v_places_utilisateurs", "idutilisateur="+getId(), "idplace="+commande.getPlace().getId());
            if(placeValide==false){
                throw new Exception(Constantes.MSG_TABLE_NON_AUTHORISEE+" : "+commande.recupererPlaceLabel());
            }
            commande.setOuverture(LocalDateTime.now());
            commande.setResteAPayer(commande.getMontant());
            commande.setUtilisateur(this);
            int idCommande=dao.insertWithoutPrimaryKey(connect, commande);
            if(modifStocks.length>0){
                dao.insertWithoutPrimaryKey(connect, ModificationStock.class, modifStocks);
            }
            Produit change, where;
            for(ModificationStock m:modifStocks){
                change=new Produit();
                change.setDernierStock(m.getStock());

                where=new Produit();
                where.setId(m.getProduit().getId());
                dao.update(connect, change, where);
            }
            commande.setId(idCommande);
            int idcommandeFille;
            LinkedList<AccompagnementCommande> accompCommandes=new LinkedList<>();
            AccompagnementCommande accompCommande;
            for(int i=0;i<commandeFilles.length;i++){
                commandeFilles[i].setCommande(commande);
                commandeFilles[i].setQuantiteRestante(commandeFilles[i].getQuantite());
                commandeFilles[i].setNotes(commandeFilles[i].getNotes().trim());
                idcommandeFille=dao.insertWithoutPrimaryKey(connect, commandeFilles[i]);
                commandeFilles[i].setId(idcommandeFille);
                for(int j=0;j<commandeFilles[i].getAccompagnements().length;j++){
                    accompCommande=new AccompagnementCommande();
                    accompCommande.setCommandeFille(commandeFilles[i]);
                    accompCommande.setAccompagnement(commandeFilles[i].getAccompagnements()[j]);
                    accompCommandes.add(accompCommande);
                }
            }
            if(accompCommandes.size()==0){
                return idCommande;
            }
            AccompagnementCommande[] accomps=accompCommandes.toArray(new AccompagnementCommande[accompCommandes.size()]);
            dao.insertWithoutPrimaryKey(connect, AccompagnementCommande.class, accomps);
            return idCommande;
        }catch(Exception e){
            connect.rollback();
            e.printStackTrace();
            throw e;
        }
    }
    public Integer[] modifierCommande(Connection connect, MyDAO dao, Commande commande, CommandeFille[] commandeFilles) throws Exception{
        try{
            boolean estValide=false;
            for(CommandeFille c:commandeFilles){
                if(c.getQuantite()>0&&estValide==false){
                    estValide=true;
                    break;
                }
            }
            if(commandeFilles.length==0||estValide==false){
                throw new Exception(Constantes.MSG_COMMANDE_VIDE);
            }
            
            Commande where=new Commande();
            where.setId(commande.getId());
            Commande commandeBase=dao.select(connect, Commande.class, where)[0];
            if(commandeBase.getEtat()!=Constantes.COMMANDE_CREEE){
                throw new Exception(Constantes.MSG_COMMANDE_INTOUCHABLE);
            }
            if(commandeBase.getUtilisateur().getId()!=getId()){
                throw new Exception(Constantes.MSG_COMMANDE_NON_AUTHORISEE);
            }
            creerTableFictiveVerificationStock(connect, dao, commandeFilles);
            ModificationStock[] modifStocks= verifierStock(connect, dao);
            Commande change=new Commande();
            change.setMontant(commande.getMontant());
            change.setResteAPayer(commande.getMontant());
            dao.update(connect, change, where);
            
            if(modifStocks.length>0){
                dao.insertWithoutPrimaryKey(connect, ModificationStock.class, modifStocks);
            }
            Produit changeProduit, whereProduit;
            for(ModificationStock m:modifStocks){
                changeProduit=new Produit();
                changeProduit.setDernierStock(m.getStock());

                whereProduit=new Produit();
                whereProduit.setId(m.getProduit().getId());
                dao.update(connect, changeProduit, whereProduit);
            }

            int idcommandeFille;
            LinkedList<AccompagnementCommande> accompCommandes=new LinkedList<>();
            LinkedList<Integer> ids=new LinkedList<>();
            AccompagnementCommande accompCommande;
            for(int i=0;i<commandeFilles.length;i++){
                commandeFilles[i].setCommande(where);
                commandeFilles[i].setQuantiteRestante(commandeFilles[i].getQuantite());
                idcommandeFille=dao.insertWithoutPrimaryKey(connect, commandeFilles[i]);
                commandeFilles[i].setId(idcommandeFille);
                ids.add(idcommandeFille);
                for(int j=0;j<commandeFilles[i].getAccompagnements().length;j++){
                    accompCommande=new AccompagnementCommande();
                    accompCommande.setCommandeFille(commandeFilles[i]);
                    accompCommande.setAccompagnement(commandeFilles[i].getAccompagnements()[j]);
                    accompCommandes.add(accompCommande);
                }
            }
            if(accompCommandes.size()==0){
                return ids.toArray(new Integer[ids.size()]);
            }
            AccompagnementCommande[] accomps=accompCommandes.toArray(new AccompagnementCommande[accompCommandes.size()]);
            dao.insertWithoutPrimaryKey(connect, AccompagnementCommande.class, accomps);
            return ids.toArray(new Integer[ids.size()]);
        }catch(Exception e){
            connect.rollback();
            e.printStackTrace();
            throw e;
        }
    }
    public Utilisateur() {
    }
    public Utilisateur(Integer etat) {
        this.etat = etat;
    }
    public Place[] getPlaces() {
        return places;
    }
    public void setPlaces(Place[] places) {
        this.places = places;
    }
    @Override
    public String toString() {
        return "Utilisateur [id=" + id + ", nom=" + nom + ", email=" + email + ", contact=" + contact + ", motdepasse="
                + motdepasse + ", role=" + role + ", etat=" + etat + ", autorisation=" + autorisation + ", places="
                + Arrays.toString(places) + "]";
    }
    public static Utilisateur[] recupererServeursEnCours(Connection connect, MyDAO dao) throws Exception{
        String query="select * from v_serveurs_encours";
        HashMap<String, Object>[] objets=dao.select(connect, query);
        Utilisateur[] utilisateurs=new Utilisateur[objets.length];
        Role role;
        for(int i=0;i<utilisateurs.length;i++){
            utilisateurs[i]=new Utilisateur();
            utilisateurs[i].setId((int)objets[i].get("id"));
            utilisateurs[i].setNom((String)objets[i].get("nom"));
            utilisateurs[i].setEmail((String)objets[i].get("email"));
            utilisateurs[i].setContact((String)objets[i].get("contact"));
            role=new Role();
            role.setId((int)objets[i].get("idrole"));
            role.setNom((String)objets[i].get("nom_role"));
            role.setNumero((String)objets[i].get("numero_role"));
            utilisateurs[i].setRole(role);
            utilisateurs[i].setCommandes(utilisateurs[i].recupererCommandesCorrespondantes(connect, dao));
        }
        return utilisateurs;
    }
    public CommandeEnCours[] recupererCommandesCorrespondantes(Connection connect, MyDAO dao) throws Exception{
        String addOn="where idutilisateur=%s and etat<20 order by dateheure_ouverture";
        addOn=String.format(addOn, getId());
        CommandeEnCours[] commandes=dao.select(connect, CommandeEnCours.class, addOn);
        for(int i=0;i<commandes.length;i++){
            commandes[i].recupererPaiements(connect, dao);
            commandes[i].recupererCommandeFilles(connect, dao);
            commandes[i].recupererActionsSuperviseurs(connect, dao);
            commandes[i].recupererActionsTotales();
            commandes[i].recupererPaiementTotal();
            commandes[i].recupererRemises(connect, dao);
        }
        return commandes;
    }
    public CommandeEnCours[] recupererCommandesCorrespondantes(Connection connect, MyDAO dao, int offset, String table) throws Exception{
        String addOn="where idutilisateur=%s and etat<10 order by dateheure_ouverture limit %s offset %s";
        addOn=String.format(addOn, getId(), Constantes.PAGINATION_LIMIT, offset);
        if(table!=null){
            addOn="where nom_place='%s' and idutilisateur=%s and etat<10 order by dateheure_ouverture limit %s offset %s";
            addOn=String.format(addOn, table, getId(), Constantes.PAGINATION_LIMIT, offset);
        }
        CommandeEnCours[] commandes=dao.select(connect, CommandeEnCours.class, addOn);
        for(int i=0;i<commandes.length;i++){
            commandes[i].recupererCommandeFilles(connect, dao);
        }
        return commandes;
    }
    public CommandeEnCours[] recupererCommandesChecking(Connection connect, MyDAO dao, int offset, String table) throws Exception{
        String addOn="where id in (select idcommande from v_commandefille_produits where idcategorie in (select idcategorie from v_role_categorie_produits_checkings where idrole=%s) group by idcommande) and etat=0 order by dateheure_ouverture limit %s offset %s";
        addOn=String.format(addOn, getRole().getId(), Constantes.PAGINATION_LIMIT, offset);
        if(table!=null){
            addOn="where nom_place='%s' and id in (select idcommande from v_commandefille_produits where idcategorie in (select idcategorie from v_role_categorie_produits_checkings where idrole=%s) group by idcommande) and etat=0 order by dateheure_ouverture limit %s offset %s";
            addOn=String.format(addOn, table, getRole().getId(), Constantes.PAGINATION_LIMIT, offset);
        }
        CommandeEnCours[] commandes=dao.select(connect, CommandeEnCours.class, addOn);
        for(int i=0;i<commandes.length;i++){
            commandes[i].recupererCommandeFillesChecking(connect, dao, getRole());
        }
        return commandes;
    }
    public void checkCommandeFille(Connection connect, MyDAO dao, CommandeFille commandeFille) throws Exception{
        try{
            String query="select * from v_commandefille_produits where etat=%s and id=%s and est_termine=-1 and idcategorie in (select idcategorie from role_categorie_produits_checkings where idrole=%s and etat=0)";
            query=String.format(query, Constantes.COMMANDEFILLE_CREEE, commandeFille.getId(), getRole().getId());
            System.out.println(query);
            CommandeFille commandeFilleBase=dao.select(connect, query, CommandeFille.class)[0];
            Commande where=new Commande();
            where.setId(commandeFilleBase.getCommande().getId());
            where.setEtat(Constantes.COMMANDE_CREEE);
            Commande[] commandeBase=dao.select(connect, Commande.class, where);
            if(commandeBase.length!=1){
                throw new Exception(Constantes.MSG_COMMANDE_INTOUCHABLE);
            }
            CommandeFilleTerminee ct=new CommandeFilleTerminee();
            ct.setUtilisateur(this);
            ct.setCommandeFille(commandeFille);
            ct.setDateheure(LocalDateTime.now());
            ct.setEstTermine(1);
            dao.insertWithoutPrimaryKey(connect, ct);
        }catch(ArrayIndexOutOfBoundsException e){
            connect.rollback();
            throw new Exception(Constantes.MSG_ACTION_INVALIDE);
        }catch(Exception e){
            connect.rollback();
            throw e;
        }
    }
    public CommandeEnCours[] recupererHistoriqueCommande(Connection connect, MyDAO dao, int offset, String serveur, String table, String ouvertureDebut, String ouvertureFin, String clotureDebut,
                                                        String clotureFin, String montantDebut, String montantFin, String montantOffertDebut, String montantOffertFin,
                                                        String montantAnnuleDebut, String montantAnnuleFin, String[] modepaiement, String produit, String accompagnement, String notes) throws Exception{
        String query="select * from v_commandes where etat>10 and etat<40 and ";
        LinkedList<String> listQuery=new LinkedList<>();
        LocalDateTime ouvertureDebutTime=null, ouvertureFinTime=null, clotureDebutTime=null, clotureFinTime=null;
        if(serveur!=null&&serveur.isEmpty()==false&&serveur.equals("0")==false){
            listQuery.add("idutilisateur=?");
        }
        if(table!=null&&table.isEmpty()==false){
            listQuery.add("nom_place=?");
        }
        if(ouvertureDebut!=null&&ouvertureDebut.isEmpty()==false){
            listQuery.add("dateheure_ouverture>=?");
            ouvertureDebutTime=LocalDateTime.parse(ouvertureDebut);
        }
        if(ouvertureFin!=null&&ouvertureFin.isEmpty()==false){
            listQuery.add("dateheure_ouverture<?");
            ouvertureFinTime=LocalDateTime.parse(ouvertureFin);
        }
        if(clotureDebut!=null&&clotureDebut.isEmpty()==false){
            listQuery.add("dateheure_cloture>=?");
            clotureDebutTime=LocalDateTime.parse(clotureDebut);
        }
        if(clotureFin!=null&&clotureFin.isEmpty()==false){
            listQuery.add("dateheure_cloture<?");
            clotureFinTime=LocalDateTime.parse(clotureFin);
        }
        if(montantDebut!=null&&montantDebut.isEmpty()==false){
            listQuery.add("montant>=?");
        }
        if(montantFin!=null&&montantFin.isEmpty()==false){
            listQuery.add("montant<?");
        }
        if(montantOffertDebut!=null&&montantOffertDebut.isEmpty()==false){
            listQuery.add("montant_offert>=?");
        }
        if(montantOffertFin!=null&&montantOffertFin.isEmpty()==false){
            listQuery.add("montant_offert<?");
        }
        if(montantAnnuleDebut!=null&&montantAnnuleDebut.isEmpty()==false){
            listQuery.add("montant_annulee>=?");
        }
        if(montantAnnuleFin!=null&&montantAnnuleFin.isEmpty()==false){
            listQuery.add("montant_annulee<?");
        }
        String[] modes={};
        String modesPaiement="";
        if(modepaiement!=null){
            modesPaiement="id in (select idcommande from paiements where etat=0 and (";
            modes=modepaiement;
        }
        for(int i=0;i<modes.length;i++){
            modesPaiement+="idmodepaiement=? or ";
        }
        if(modesPaiement.length()>0){
            modesPaiement=modesPaiement.substring(0, modesPaiement.length()-4)+") group by idcommande)";
            listQuery.add(modesPaiement);
        }
        if(produit!=null&&produit.isEmpty()==false){
            listQuery.add("id in (select idcommande from v_commandefille_produits where nom like ? group by idcommande)");
        }
        if(accompagnement!=null&&accompagnement.isEmpty()==false){
            listQuery.add("id in (select idcommande from v_commandefille_accompagnements where nom_accompagnement like ? group by idcommande)");
        }
        if(notes!=null&&notes.isEmpty()==false){
            listQuery.add("id in (select idcommande from commande_filles where etat=0 and notes like ? group by idcommande)");
        }
        for(String s:listQuery){
            query+=s+" and ";
        }
        query=query.substring(0, query.length()-5);
        query+=" order by dateheure_ouverture desc limit ? offset ?";
        try(PreparedStatement statement=connect.prepareStatement(query)){
            int indice=1;
            if(serveur!=null&&serveur.isEmpty()==false&&serveur.equals("0")==false){
                statement.setInt(indice, Integer.parseInt(serveur));
                indice++;
            }
            if(table!=null&&table.isEmpty()==false){
                statement.setString(indice, table);
                indice++;
            }
            if(ouvertureDebut!=null&&ouvertureDebut.isEmpty()==false){
                statement.setTimestamp(indice, Timestamp.valueOf(ouvertureDebutTime));
                indice++;
            }
            if(ouvertureFin!=null&&ouvertureFin.isEmpty()==false){
                statement.setTimestamp(indice, Timestamp.valueOf(ouvertureFinTime));
                indice++;
            }
            if(clotureDebut!=null&&clotureDebut.isEmpty()==false){
                statement.setTimestamp(indice, Timestamp.valueOf(clotureDebutTime));
                indice++;
            }
            if(clotureFin!=null&&clotureFin.isEmpty()==false){
                statement.setTimestamp(indice, Timestamp.valueOf(clotureFinTime));
                indice++;
            }
            if(montantDebut!=null&&montantDebut.isEmpty()==false){
                statement.setDouble(indice, Double.parseDouble(montantDebut));
                indice++;
            }
            if(montantFin!=null&&montantFin.isEmpty()==false){
                statement.setDouble(indice, Double.parseDouble(montantFin));
                indice++;
            }
            if(montantOffertDebut!=null&&montantOffertDebut.isEmpty()==false){
                statement.setDouble(indice, Double.parseDouble(montantOffertDebut));
                indice++;
            }
            if(montantOffertFin!=null&&montantOffertFin.isEmpty()==false){
                statement.setDouble(indice, Double.parseDouble(montantOffertFin));
                indice++;
            }
            if(montantAnnuleDebut!=null&&montantAnnuleDebut.isEmpty()==false){
                statement.setDouble(indice, Double.parseDouble(montantAnnuleDebut));
                indice++;
            }
            if(montantAnnuleFin!=null&&montantAnnuleFin.isEmpty()==false){
                statement.setDouble(indice, Double.parseDouble(montantAnnuleFin));
                indice++;
            }
            for(String s:modes){
                statement.setInt(indice, Integer.parseInt(s));
                indice++;
            }
            if(produit!=null&&produit.isEmpty()==false){
                statement.setString(indice, "%"+produit+"%");
                indice++;
            }
            if(accompagnement!=null&&accompagnement.isEmpty()==false){
                statement.setString(indice, "%"+accompagnement+"%");
                indice++;
            }
            if(notes!=null&&notes.isEmpty()==false){
                statement.setString(indice, "%"+notes+"%");
                indice++;
            }
            statement.setInt(indice, Constantes.PAGINATION_LIMIT);
            indice++;
            statement.setInt(indice, (offset-1)*Constantes.PAGINATION_LIMIT);
            try(ResultSet result=statement.executeQuery()){
                LinkedList<CommandeEnCours> liste=new LinkedList<>();
                Place place;
                TypePlace typePlace;
                Utilisateur utilisateur;
                CommandeEnCours commande;
                while(result.next()){
                    commande=new CommandeEnCours();
                    commande.setId(result.getInt("id"));
                    commande.setCloture(result.getTimestamp("dateheure_cloture").toLocalDateTime());
                    commande.setEtat(result.getInt("etat"));
                    commande.setMontant(result.getDouble("montant"));
                    commande.setNomPlace(result.getString("nom_place"));
                    place=new Place();
                    place.setId(result.getInt("idplace"));
                    place.setNom(result.getString("nom_place"));
                    typePlace=new TypePlace();
                    typePlace.setId(result.getInt("idtypeplace"));
                    typePlace.setNumero(result.getString("numero_type_place"));
                    place.setTypePlace(typePlace);
                    commande.setPlace(place);
                    utilisateur=new Utilisateur();
                    utilisateur.setId(result.getInt("idutilisateur"));
                    commande.setUtilisateur(dao.select(connect, Utilisateur.class, utilisateur)[0]);
                    commande.setOuverture(result.getTimestamp("dateheure_ouverture").toLocalDateTime());
                    commande.recupererCommandeFilles(connect, dao);
                    commande.recupererPaiements(connect, dao);
                    commande.recupererActionsSuperviseurs(connect, dao);
                    commande.recupererActionsTotales();
                    commande.recupererPaiementTotal();
                    commande.recupererRemises(connect, dao);
                    commande.setMontantRemises(result.getDouble("montant_remises"));
                    liste.add(commande);
                }
                CommandeEnCours[] commandes=liste.toArray(new CommandeEnCours[]{});
                return commandes;
            }
        }
    }
    public int recupererCountHistoriqueCommande(Connection connect, MyDAO dao, int offset, String serveur, String table, String ouvertureDebut, String ouvertureFin, String clotureDebut,
                                                         String clotureFin, String montantDebut, String montantFin, String montantOffertDebut, String montantOffertFin,
                                                         String montantAnnuleDebut, String montantAnnuleFin, String[] modepaiement, String produit, String accompagnement, String notes) throws Exception{
        String query="select count(*) from v_commandes where etat>10 and etat<40 and ";
        LinkedList<String> listQuery=new LinkedList<>();
        LocalDateTime ouvertureDebutTime=null, ouvertureFinTime=null, clotureDebutTime=null, clotureFinTime=null;
        if(serveur!=null&&serveur.isEmpty()==false&&serveur.equals("0")==false){
            listQuery.add("idutilisateur=?");
        }
        if(table!=null&&table.isEmpty()==false){
            listQuery.add("nom_place=?");
        }
        if(ouvertureDebut!=null&&ouvertureDebut.isEmpty()==false){
            listQuery.add("dateheure_ouverture>=?");
            ouvertureDebutTime=LocalDateTime.parse(ouvertureDebut);
        }
        if(ouvertureFin!=null&&ouvertureFin.isEmpty()==false){
            listQuery.add("dateheure_ouverture<?");
            ouvertureFinTime=LocalDateTime.parse(ouvertureFin);
        }
        if(clotureDebut!=null&&clotureDebut.isEmpty()==false){
            listQuery.add("dateheure_cloture>=?");
            clotureDebutTime=LocalDateTime.parse(clotureDebut);
        }
        if(clotureFin!=null&&clotureFin.isEmpty()==false){
            listQuery.add("dateheure_cloture<?");
            clotureFinTime=LocalDateTime.parse(clotureFin);
        }
        if(montantDebut!=null&&montantDebut.isEmpty()==false){
            listQuery.add("montant>=?");
        }
        if(montantFin!=null&&montantFin.isEmpty()==false){
            listQuery.add("montant<?");
        }
        if(montantOffertDebut!=null&&montantOffertDebut.isEmpty()==false){
            listQuery.add("montant_offert>=?");
        }
        if(montantOffertFin!=null&&montantOffertFin.isEmpty()==false){
            listQuery.add("montant_offert<?");
        }
        if(montantAnnuleDebut!=null&&montantAnnuleDebut.isEmpty()==false){
            listQuery.add("montant_annulee>=?");
        }
        if(montantAnnuleFin!=null&&montantAnnuleFin.isEmpty()==false){
            listQuery.add("montant_annulee<?");
        }
        String[] modes={};
        String modesPaiement="";
        if(modepaiement!=null){
            modesPaiement="id in (select idcommande from paiements where etat=0 and (";
            modes=modepaiement;
        }
        for(int i=0;i<modes.length;i++){
            modesPaiement+="idmodepaiement=? or ";
        }
        if(modesPaiement.length()>0){
            modesPaiement=modesPaiement.substring(0, modesPaiement.length()-4)+") group by idcommande)";
            listQuery.add(modesPaiement);
        }
        if(produit!=null&&produit.isEmpty()==false){
            listQuery.add("id in (select idcommande from v_commandefille_produits where nom like ? group by idcommande)");
        }
        if(accompagnement!=null&&accompagnement.isEmpty()==false){
            listQuery.add("id in (select idcommande from v_commandefille_accompagnements where nom_accompagnement like ? group by idcommande)");
        }
        if(notes!=null&&notes.isEmpty()==false){
            listQuery.add("id in (select idcommande from commande_filles where etat=0 and notes like ? group by idcommande)");
        }
        for(String s:listQuery){
            query+=s+" and ";
        }
        query=query.substring(0, query.length()-5);
        try(PreparedStatement statement=connect.prepareStatement(query)){
            int indice=1;
            if(serveur!=null&&serveur.isEmpty()==false&&serveur.equals("0")==false){
                statement.setInt(indice, Integer.parseInt(serveur));
                indice++;
            }
            if(table!=null&&table.isEmpty()==false){
                statement.setString(indice, table);
                indice++;
            }
            if(ouvertureDebut!=null&&ouvertureDebut.isEmpty()==false){
                statement.setTimestamp(indice, Timestamp.valueOf(ouvertureDebutTime));
                indice++;
            }
            if(ouvertureFin!=null&&ouvertureFin.isEmpty()==false){
                statement.setTimestamp(indice, Timestamp.valueOf(ouvertureFinTime));
                indice++;
            }
            if(clotureDebut!=null&&clotureDebut.isEmpty()==false){
                statement.setTimestamp(indice, Timestamp.valueOf(clotureDebutTime));
                indice++;
            }
            if(clotureFin!=null&&clotureFin.isEmpty()==false){
                statement.setTimestamp(indice, Timestamp.valueOf(clotureFinTime));
                indice++;
            }
            if(montantDebut!=null&&montantDebut.isEmpty()==false){
                statement.setDouble(indice, Double.parseDouble(montantDebut));
                indice++;
            }
            if(montantFin!=null&&montantFin.isEmpty()==false){
                statement.setDouble(indice, Double.parseDouble(montantFin));
                indice++;
            }
            if(montantOffertDebut!=null&&montantOffertDebut.isEmpty()==false){
                statement.setDouble(indice, Double.parseDouble(montantOffertDebut));
                indice++;
            }
            if(montantOffertFin!=null&&montantOffertFin.isEmpty()==false){
                statement.setDouble(indice, Double.parseDouble(montantOffertFin));
                indice++;
            }
            if(montantAnnuleDebut!=null&&montantAnnuleDebut.isEmpty()==false){
                statement.setDouble(indice, Double.parseDouble(montantAnnuleDebut));
                indice++;
            }
            if(montantAnnuleFin!=null&&montantAnnuleFin.isEmpty()==false){
                statement.setDouble(indice, Double.parseDouble(montantAnnuleFin));
                indice++;
            }
            for(String s:modes){
                statement.setInt(indice, Integer.parseInt(s));
                indice++;
            }
            if(produit!=null&&produit.isEmpty()==false){
                statement.setString(indice, "%"+produit+"%");
                indice++;
            }
            if(accompagnement!=null&&accompagnement.isEmpty()==false){
                statement.setString(indice, "%"+accompagnement+"%");
                indice++;
            }
            if(notes!=null&&notes.isEmpty()==false){
                statement.setString(indice, "%"+notes+"%");
                indice++;
            }
            try(ResultSet result=statement.executeQuery()){
                int count=result.next()?result.getInt(1):0;
                return count;
            }
        }
    }

    public void demandeAddition(Connection connect, MyDAO dao, Commande commande) throws Exception{
        try{
            String queryCmdFilleTerminee="select * from v_commandefille_produits where est_termine=-1 and idcommande=%s";
            queryCmdFilleTerminee=String.format(queryCmdFilleTerminee, commande.getId());
            CommandeFille[] cmdFillesNonTerminees=dao.select(connect, queryCmdFilleTerminee, CommandeFille.class);
            if(cmdFillesNonTerminees.length>0){
                throw new Exception(Constantes.MSG_COMMANDE_NON_TERMINEE);
            }
            Commande where=new Commande();
            where.setId(commande.getId());
            where.setEtat(Constantes.COMMANDE_CREEE);
            Commande commandeBase=dao.select(connect, Commande.class, where)[0];
            if(commandeBase.getUtilisateur().getId()!=getId()){
                throw new Exception(Constantes.MSG_COMMANDE_NON_AUTHORISEE);
            }
            Commande change=new Commande();
            change.setEtat(Constantes.COMMANDE_ADDITION);
            // change.setCloture(LocalDateTime.now());
            dao.update(connect, change, commande);
        }catch(ArrayIndexOutOfBoundsException e){
            connect.rollback();
            throw new Exception(Constantes.MSG_COMMANDE_INTOUCHABLE);
        }catch(Exception e){
            connect.rollback();
            e.printStackTrace();
            throw e;
        }
    }
    public CommandeEnCours[] recupererDemandesAddition(Connection connect, MyDAO dao, int offset, String table) throws Exception{
        String addOn="where etat=10 order by dateheure_ouverture limit %s offset %s";
        // CommandeEnCours where=new CommandeEnCours();
        // where.setEtat(Constantes.COMMANDE_ADDITION);
        addOn=String.format(addOn, Constantes.PAGINATION_LIMIT, offset);
        if(table.isEmpty()==false){
            addOn="where nom_table='%s' and etat=10 order by dateheure_ouverture limit %s offset %s";
            addOn=String.format(addOn, table.replace("'", "''"), Constantes.PAGINATION_LIMIT, offset);
            // where.setNomPlace(table);
        }
        CommandeEnCours[] commandes=dao.select(connect, CommandeEnCours.class, addOn);
        for(CommandeEnCours c:commandes){
            c.recupererCommandeFilles(connect, dao);
            c.recupererPaiements(connect, dao);
        }
        return commandes;
    }
    public void payer(Connection connect, MyDAO dao, Paiement paiement) throws Exception{
        try{
            Commande where=new Commande();
            where.setId(paiement.getCommande().getId());
            Commande commande=dao.select(connect, Commande.class, where)[0];
            if(paiement.getMontant()>commande.getResteAPayer()){
                throw new Exception(Constantes.MSG_PAIEMENT_MONTANT_INVALIDE);
            }
            if(commande.getEtat()!=Constantes.COMMANDE_ADDITION){
                throw new Exception(Constantes.MSG_COMMANDE_INTOUCHABLE);
            }
            paiement.setUtilisateur(this);
            dao.insertWithoutPrimaryKey(connect, paiement);
            Commande change=new Commande();
            change.setResteAPayer(commande.getResteAPayer()-paiement.getMontant());
            // if(change.getResteAPayer()==0){
            //     change.setEtat(Constantes.COMMANDE_PAYEE);
            // }
            dao.update(connect, change, where);
        }catch(Exception e){
            connect.rollback();
            throw e;
        }
    }
    public void modifierStock(Connection connect, MyDAO dao, Produit produit, double quantite) throws Exception{
        try{
            ModificationStock modif=new ModificationStock();
            modif.setProduit(produit);
            modif.setStock(quantite);
            modif.setUtilisateur(this);
            dao.insertWithoutPrimaryKey(connect, modif);
            Produit where=new Produit();
            where.setId(produit.getId());
            Produit change=new Produit();
            change.setDernierStock(quantite);
            dao.update(connect, change, where);
        }catch(Exception e){
            connect.rollback();
            throw e;
        }
    }
    public Produit[] rechercheProduit(Connection connect, MyDAO dao, String nomProduit) throws Exception{
        String addOn="where nom like '%s' and etat=0 and idcategorie in (select idcategorie from role_categorie_produits_checkings where idrole=%s and etat=0 group by idcategorie)";
        addOn=String.format(addOn, "%"+nomProduit.replace("'", "''")+"%", getRole().getId());
        Produit[] produits=dao.select(connect, Produit.class, addOn);
        return produits;
    }
    public Produit[] recupereProduitsStockLimite(Connection connect, MyDAO dao) throws Exception{
        String addOn="where dernier_stock>=0 and etat=0 and idcategorie in (select idcategorie from role_categorie_produits_checkings where idrole=%s and etat=0 group by idcategorie)";
        addOn=String.format(addOn, getRole().getId());
        Produit[] produits=dao.select(connect, Produit.class, addOn);
        return produits;
    }
    public boolean actionSuperviseur(Connection connect, MyDAO dao, ActionSuperviseur actionSuperviseur) throws Exception{
        boolean estTermine=false;
        try{
            CommandeFille where=new CommandeFille();
            where.setId(actionSuperviseur.getCommandeFille().getId());
            where.setEtat(Constantes.COMMANDEFILLE_CREEE);
            CommandeFille commandeFille=dao.select(connect, CommandeFille.class, where)[0];
            if(commandeFille.getCommande().getEtat()>Constantes.COMMANDE_ADDITION){
                throw new Exception(Constantes.MSG_COMMANDE_INTOUCHABLE);
            }
            if(commandeFille.getEtat()!=Constantes.COMMANDEFILLE_CREEE){
                throw new Exception(Constantes.MSG_COMMANDEFILLE_INTOUCHABLE);
            }
            if(actionSuperviseur.getQuantite()>commandeFille.getQuantiteRestante()){
                throw new Exception(Constantes.MSG_QUANTITE_INVALIDE);
            }
            double montantAction=actionSuperviseur.getQuantite()*commandeFille.getPu();
            if(montantAction>commandeFille.getCommande().getResteAPayer()){
                throw new Exception(Constantes.MSG_ACTIONSUPERVISEUR_MONTANT_INVALIDE);
            }
            actionSuperviseur.setMontant(montantAction);
            // ModePaiement vat=new ModePaiement();
            // vat.setId(Constantes.IDMODEPAIEMENT_VAT);
            // Paiement paiementVAT=new Paiement();
            // paiementVAT.setCommande(commandeFille.getCommande());
            // paiementVAT.setModePaiement(vat);
            // paiementVAT.setUtilisateur(this);
            // paiementVAT.setMontant(montantAction);
            Commande whereCommande=new Commande();
            whereCommande.setId(commandeFille.getCommande().getId());
            Commande changeCommande=new Commande();
            changeCommande.setResteAPayer(commandeFille.getCommande().getResteAPayer()-montantAction);
            actionSuperviseur.setUtilisateur(this);
            // ActionPaiement actionPaiement;
            // int idpaiement;
            dao.insertWithoutPrimaryKey(connect, actionSuperviseur);
            switch(actionSuperviseur.getAction()){
                case Constantes.COMMANDEFILLE_OFFERT:
                    changeCommande.setMontantOffert(commandeFille.getCommande().getMontantOffert()+montantAction);
                    // idpaiement=dao.insertWithoutPrimaryKey(connect, paiementVAT);
                    // paiementVAT.setId(idpaiement);
                    // actionSuperviseur.setId(idaction);
                    // actionPaiement=new ActionPaiement();
                    // actionPaiement.setPaiement(paiementVAT);
                    // actionPaiement.setAction(actionSuperviseur);
                    // dao.insertWithoutPrimaryKey(connect, actionPaiement);
                    break;
                case Constantes.COMMANDEFILLE_ANNULEE:
                    changeCommande.setMontantAnnulee(commandeFille.getCommande().getMontantAnnulee()+montantAction);
                    break;
            }
            CommandeFille changeCommandeFille=new CommandeFille();
            changeCommandeFille.setQuantiteRestante(commandeFille.getQuantite()-actionSuperviseur.getQuantite());
            // if(changeCommande.getResteAPayer()==0){
            //     changeCommande.setEtat(Constantes.COMMANDE_PAYEE);
            //     estTermine=true;
            // }
            dao.update(connect, changeCommandeFille, where);
            dao.update(connect, changeCommande, whereCommande);
            return estTermine;
        }catch(Exception e){
            connect.rollback();
            e.printStackTrace();
            throw e;
        }
    }
    public int[] recupererCategoriesChecking(Connection connect, MyDAO dao) throws Exception{
        String query="where id in (select idcategorie from role_categorie_produits_checkings where idrole=%s) and etat=0";
        query=String.format(query, getRole().getId());
        Categorie[] categories=dao.select(connect, Categorie.class, query);
        int[] ids=new int[categories.length];
        for(int i=0;i<ids.length;i++){
            ids[i]=categories[i].getId();
        }
        return ids;
    }
    public static Utilisateur[] recupererServeursHistorique(Connection connect, MyDAO dao) throws Exception{
        String addOn="where id in (select idutilisateur from commandes group by idutilisateur)";
        Utilisateur[] serveurs=dao.select(connect, Utilisateur.class, addOn);
        for(Utilisateur u:serveurs){
            u.setMotdepasse(null);
            u.setEmail(null);
        }
        return serveurs;
    }
    public boolean remise(Connection connect, MyDAO dao, Remise remise) throws Exception{
        boolean estTermine=false;
        try{
            CommandeFille where=new CommandeFille();
            where.setId(remise.getCommandeFille().getId());
            where.setEtat(Constantes.COMMANDEFILLE_CREEE);
            CommandeFille commandeFille=dao.select(connect, CommandeFille.class, where)[0];
            if(commandeFille.getCommande().getEtat()>Constantes.COMMANDE_ADDITION){
                throw new Exception(Constantes.MSG_COMMANDE_INTOUCHABLE);
            }
            if(commandeFille.getEtat()!=Constantes.COMMANDEFILLE_CREEE){
                throw new Exception(Constantes.MSG_COMMANDEFILLE_INTOUCHABLE);
            }
            if(remise.getQuantite()>commandeFille.getQuantiteRestante()){
                throw new Exception(Constantes.MSG_QUANTITE_INVALIDE);
            }
            double diffMontant=(commandeFille.getPu()*remise.getQuantite())-(remise.getNouveauMontant()*remise.getQuantite());
            if(diffMontant>commandeFille.getCommande().getResteAPayer()){
                throw new Exception(Constantes.MSG_ACTIONSUPERVISEUR_MONTANT_INVALIDE);
            }
            Commande whereCommande=new Commande();
            whereCommande.setId(commandeFille.getCommande().getId());
            // whereCommande.setEtat(Constantes.COMMANDE_ADDITION);
            Commande changeCommande=new Commande();
            changeCommande.setResteAPayer(commandeFille.getCommande().getResteAPayer()-diffMontant);
            changeCommande.setMontantRemises(commandeFille.getCommande().getMontantRemises()+diffMontant);
            // if(changeCommande.getResteAPayer()==0){
            //     estTermine=true;
            //     changeCommande.setEtat(Constantes.COMMANDE_PAYEE);
            // }
            CommandeFille changeCommandeFille=new CommandeFille();
            changeCommandeFille.setQuantiteRestante(commandeFille.getQuantiteRestante()-remise.getQuantite());
            remise.setUtilisateur(this);
            dao.insertWithoutPrimaryKey(connect, remise);
            dao.update(connect, changeCommande, whereCommande);
            dao.update(connect, changeCommandeFille, where);
            return estTermine;
        }catch(Exception e){
            connect.rollback();
            e.printStackTrace();
            throw e;
        }
    }
    public void cloturer(Connection connect, MyDAO dao, Cloture cloture) throws Exception{
        try{
            Commande where=new Commande();
            where.setId(cloture.getCommande().getId());
            Commande commande=dao.select(connect, Commande.class, where)[0];
            if(commande.getResteAPayer()>0){
                throw new Exception(Constantes.MSG_COMMANDE_NON_TERMINEE);
            }
            Commande changeCommande=new Commande();
            changeCommande.setEtat(Constantes.COMMANDE_PAYEE);
            changeCommande.setCloture(LocalDateTime.now());
            cloture.setUtilisateur(this);
            dao.insertWithoutPrimaryKey(connect, cloture);
            dao.update(connect, changeCommande, where);
        }catch(Exception e){
            connect.rollback();
            e.printStackTrace();
            throw e;
        }
    }
    public void annulerPaiement(Connection connect, MyDAO dao, AnnulationPaiement annulation) throws Exception{
        try{
            Paiement where=new Paiement();
            where.setId(annulation.getPaiement().getId());
            Paiement paiement=dao.select(connect, Paiement.class, where)[0];
            if(paiement.getEtat()>Constantes.PAIEMENT_CREE){
                throw new Exception(Constantes.MSG_ACTION_INVALIDE);
            }
            if(paiement.getUtilisateur().getId()!=getId()){
                throw new Exception(Constantes.MSG_NON_AUTHORISE);
            }
            if(paiement.getCommande().getEtat()>Constantes.COMMANDE_ADDITION){
                throw new Exception(Constantes.MSG_COMMANDE_INTOUCHABLE);
            }
            if(paiement.getModePaiement().getId()<0&&getRole().getNumero().equals(Constantes.ROLE_SUPERVISEUR)==false){
                throw new Exception(Constantes.MSG_NON_AUTHORISE);
            }
            Paiement changePaiement=new Paiement();
            changePaiement.setEtat(Constantes.PAIEMENT_ANNULE);
            Commande whereCommande=new Commande();
            whereCommande.setId(paiement.getCommande().getId());
            Commande changeCommande=new Commande();
            changeCommande.setResteAPayer(paiement.getCommande().getResteAPayer()+paiement.getMontant());
            annulation.setUtilisateur(this);
            dao.insertWithoutPrimaryKey(connect, annulation);
            dao.update(connect, changePaiement, where);
            dao.update(connect, changeCommande, whereCommande);
        }catch(Exception e){
            connect.rollback();
            e.printStackTrace();
            throw e;
        }
    }
    public void annulerAction(Connection connect, MyDAO dao, AnnulationAction annulation) throws Exception{
        try{
            ActionSuperviseur where=new ActionSuperviseur();
            where.setId(annulation.getAction().getId());
            ActionSuperviseur action=dao.select(connect, ActionSuperviseur.class, where)[0];
            if(action.getEtat()!=Constantes.ACTION_CREE){
                throw new Exception(Constantes.MSG_ACTION_INVALIDE);
            }
            if(action.getUtilisateur().getId()!=getId()){
                throw new Exception(Constantes.MSG_NON_AUTHORISE);
            }
            if(action.getCommandeFille().getCommande().getEtat()>Constantes.COMMANDE_ADDITION){
                throw new Exception(Constantes.MSG_COMMANDE_INTOUCHABLE);
            }
            // String addOn="where id in (select idpaiement from action_paiements where idaction=%s) and etat=%s";
            // addOn=String.format(addOn, action.getId(), Constantes.PAIEMENT_CREE);

            ActionSuperviseur changeAction=new ActionSuperviseur();
            changeAction.setEtat(Constantes.ACTION_ANNULE);

            Commande whereCommande=new Commande();
            whereCommande.setId(action.getCommandeFille().getCommande().getId());
            Commande changeCommande=new Commande();
            changeCommande.setResteAPayer(action.getCommandeFille().getCommande().getResteAPayer()+action.getMontant());
            switch(action.getAction()){
                case Constantes.COMMANDEFILLE_OFFERT:
                    // Paiement paiement=dao.select(connect, Paiement.class, addOn)[0];
                    // AnnulationPaiement annulationPaiement=new AnnulationPaiement();
                    // annulationPaiement.setPaiement(paiement);
                    // annulationPaiement.setUtilisateur(this);
                    // Paiement wherePaiement=new Paiement();
                    // wherePaiement.setId(paiement.getId());
                    // Paiement changePaiement=new Paiement();
                    // changePaiement.setEtat(Constantes.PAIEMENT_ANNULE);
                    changeCommande.setMontantOffert(action.getCommandeFille().getCommande().getMontantOffert()-action.getMontant());
                    // dao.update(connect, changePaiement, wherePaiement);
                    // dao.insertWithoutPrimaryKey(connect, annulationPaiement);
                    break;
                case Constantes.COMMANDEFILLE_ANNULEE:
                    changeCommande.setMontantAnnulee(action.getCommandeFille().getCommande().getMontantAnnulee()-action.getMontant());
                    break;
            }
            CommandeFille whereCommandeFille=new CommandeFille();
            whereCommandeFille.setId(action.getCommandeFille().getId());
            CommandeFille changeCommandeFille=new CommandeFille();
            changeCommandeFille.setQuantiteRestante(action.getCommandeFille().getQuantiteRestante()+action.getQuantite());
            annulation.setUtilisateur(this);
            dao.update(connect, changeAction, where);
            dao.update(connect, changeCommande, whereCommande);
            dao.update(connect, changeCommandeFille, whereCommandeFille);
            dao.insertWithoutPrimaryKey(connect, annulation);
        }catch(Exception e){
            connect.rollback();
            e.printStackTrace();
            throw e;
        }
    }
    public void annulerRemise(Connection connect, MyDAO dao, AnnulationRemise annulation) throws Exception{
        try{
            Remise where=new Remise();
            where.setId(annulation.getRemise().getId());
            Remise remise=dao.select(connect, Remise.class, where)[0];
            if(remise.getEtat()!=Constantes.REMISE_CREE){
                throw new Exception(Constantes.MSG_ACTION_INVALIDE);
            }
            if(remise.getUtilisateur().getId()!=getId()){
                throw new Exception(Constantes.MSG_NON_AUTHORISE);
            }
            if(remise.getCommandeFille().getCommande().getEtat()>Constantes.COMMANDE_ADDITION){
                throw new Exception(Constantes.MSG_COMMANDE_INTOUCHABLE);
            }
            Remise changeRemise=new Remise();
            changeRemise.setEtat(Constantes.REMISE_ANNULE);
            Commande changeCommande=new Commande();
            double diffMontant=remise.getCommandeFille().getPu()*remise.getQuantite()-remise.getNouveauMontant()*remise.getQuantite();
            changeCommande.setResteAPayer(remise.getCommandeFille().getCommande().getResteAPayer()+diffMontant);
            Commande whereCommande=new Commande();
            whereCommande.setId(remise.getCommandeFille().getCommande().getId());
            CommandeFille whereCommandeFille=new CommandeFille();
            whereCommandeFille.setId(remise.getCommandeFille().getId());
            CommandeFille changeCommandeFille=new CommandeFille();
            changeCommandeFille.setQuantiteRestante(remise.getCommandeFille().getQuantiteRestante()+remise.getQuantite());
            annulation.setUtilisateur(this);
            dao.update(connect, changeRemise, where);
            dao.update(connect, changeCommande, whereCommande);
            dao.update(connect, changeCommandeFille, whereCommandeFille);
            dao.insertWithoutPrimaryKey(connect, annulation);
        }catch(Exception e){
            connect.rollback();
            e.printStackTrace();
            throw e;
        }
    }
    public ModePaiement[] recupererModePaiements(Connection connect, MyDAO dao) throws Exception{
        String addOn="where etat=0 and id %s 0";
        switch(getRole().getNumero()){
            case Constantes.ROLE_SUPERVISEUR:
                addOn=String.format(addOn, "<");
                break;
            default:
                addOn=String.format(addOn, ">");
        }
        ModePaiement[] modes=dao.select(connect, ModePaiement.class, addOn);
        return modes;
    }
    public void supprimerUtilisateur(Connection connect, MyDAO dao, String idutilisateur) throws Exception{
        try{
            Utilisateur where=new Utilisateur();
            where.setId(Integer.parseInt(idutilisateur));
            Utilisateur change=new Utilisateur();
            change.setEtat(Constantes.UTILISATEUR_SUPPRIME);
            dao.update(connect, change, where);
        }catch(Exception e){
            connect.rollback();
            e.printStackTrace();
            throw e;
        }
    }
    public void restaurerUtilisateur(Connection connect, MyDAO dao, String idutilisateur) throws Exception{
        try{
            Utilisateur where=new Utilisateur();
            where.setId(Integer.parseInt(idutilisateur));
            Utilisateur change=new Utilisateur();
            change.setEtat(Constantes.UTILISATEUR_CREE);
            dao.update(connect, change, where);
        }catch(Exception e){
            connect.rollback();
            e.printStackTrace();
            throw e;
        }
    }
    public void ajouterUtilisateur(Connection connect, MyDAO dao, String nom, String motDePasse, String contact) throws Exception{
        try{
            if(nom==null||nom.isEmpty()||nom.isBlank()||motDePasse==null||motDePasse.isEmpty()||motDePasse.isBlank()||contact==null||contact.isEmpty()||contact.isBlank()){
                throw new Exception(Constantes.MSG_VALEUR_INVALIDE);
            }
            nom=nom.trim();
            motDePasse=motDePasse.trim();
            contact=contact.trim();
            Utilisateur utilisateur=new Utilisateur();
            utilisateur.setNom(nom);
            Role role=new Role();
            role.setId(Constantes.IDROLE_OFF);
            utilisateur.setRole(role);
            utilisateur.setEmail(nom);
            utilisateur.setMotdepasse(motDePasse);
            utilisateur.setContact(contact);
            dao.insertWithoutPrimaryKey(connect, utilisateur);
        }catch(Exception e){
            connect.rollback();
            e.printStackTrace();
            throw e;
        }
    }
    public void supprimerProduit(Connection connect, MyDAO dao, String idproduit) throws Exception{
        try{
            Produit where=new Produit();
            where.setId(Integer.parseInt(idproduit));
            Produit change=new Produit();
            change.setEtat(Constantes.PRODUIT_SUPPRIME);
            dao.update(connect, change, where);
        }catch(Exception e){
            connect.rollback();
            e.printStackTrace();
            throw e;
        }
    }
    public void restaurerProduit(Connection connect, MyDAO dao, String idproduit) throws Exception{
        try{
            Produit where=new Produit();
            where.setId(Integer.parseInt(idproduit));
            Produit change=new Produit();
            change.setEtat(Constantes.PRODUIT_CREE);
            dao.update(connect, change, where);
        }catch(Exception e){
            connect.rollback();
            e.printStackTrace();
            throw e;
        }
    }
    public void ajouterProduit(Connection connect, MyDAO dao, String nom, String idcategorie, String prix, String accompagnements) throws Exception{
        try{
            if(nom==null||idcategorie==null||prix==null){
                throw new Exception(Constantes.MSG_VALEUR_INVALIDE);
            }
            nom=nom.trim();
            prix=prix.trim();
            idcategorie=idcategorie.trim();
            accompagnements=accompagnements.trim();
            String[] accomps=accompagnements.trim().split(Constantes.ACCOMP_SEPARATOR);
            Accompagnement[] accompagnements2=new Accompagnement[accomps.length];
            for(int i=0;i<accompagnements2.length&&accompagnements.isEmpty()==false;i++){
                accompagnements2[i]=new Accompagnement();
                accompagnements2[i].setNom(accomps[i].trim());
            }
            double prixProduit=Double.parseDouble(prix);
            int idcategorieProduit=Integer.parseInt(idcategorie);
            Categorie categorie=new Categorie();
            categorie.setId(idcategorieProduit);
            Produit produit=new Produit();
            produit.setNom(nom);
            produit.setPrix(prixProduit);
            produit.setCategorie(categorie);
            HistoriquePrixProduit histPrix=new HistoriquePrixProduit();
            histPrix.setPrix(prixProduit);
            histPrix.setUtilisateur(this);
            int idproduit=dao.insertWithoutPrimaryKey(connect, produit);
            produit.setId(idproduit);
            histPrix.setProduit(produit);
            dao.insertWithoutPrimaryKey(connect, histPrix);
            int idaccompagnement;
            AccompagnementProduit ap;
            for(Accompagnement a:accompagnements2){
                idaccompagnement=dao.insertWithoutPrimaryKey(connect, a);
                a.setId(idaccompagnement);
                ap=new AccompagnementProduit();
                ap.setAccompagnement(a);
                ap.setProduit(produit);
                dao.insertWithoutPrimaryKey(connect, ap);
            }
        }catch(Exception e){
            connect.rollback();
            e.printStackTrace();
            throw e;
        }
    }
    public void modifierProduit(Connection connect, MyDAO dao, String idproduit, String prix, String accomps) throws Exception{
        try{
            if(idproduit==null||prix==null){
                throw new Exception(Constantes.MSG_VALEUR_INVALIDE);
            }
            idproduit=idproduit.trim();
            prix=prix.trim();
            accomps=accomps.trim();
            String[] accompagnements=accomps.trim().split(Constantes.ACCOMP_SEPARATOR);
            Accompagnement[] accompagnements2=new Accompagnement[accompagnements.length];
            Accompagnement where;
            Produit whereProduit=new Produit();
            whereProduit.setId(Integer.parseInt(idproduit));
            AccompagnementProduit accompProduit;
            int idaccompagnement;
            for(int i=0;i<accompagnements2.length&&accomps.isEmpty()==false;i++){
                where=new Accompagnement();
                where.setNom(accompagnements[i]);
                boolean exists=dao.exists(connect, Accompagnement.class, where);
                if(exists){
                    continue;
                }
                idaccompagnement=dao.insertWithoutPrimaryKey(connect, where);
                where.setId(idaccompagnement);
                accompProduit=new AccompagnementProduit();
                accompProduit.setProduit(whereProduit);
                accompProduit.setAccompagnement(where);
                dao.insertWithoutPrimaryKey(connect, accompProduit);
            }
            double prixProduit=Double.parseDouble(prix);
            Produit changeProduit=new Produit();
            changeProduit.setPrix(prixProduit);
            HistoriquePrixProduit histPrix=new HistoriquePrixProduit();
            histPrix.setUtilisateur(this);
            histPrix.setProduit(whereProduit);
            histPrix.setPrix(prixProduit);
            dao.update(connect, changeProduit, whereProduit);
            dao.insertWithoutPrimaryKey(connect, histPrix);
        }catch(Exception e){
            connect.rollback();
            e.printStackTrace();
            throw e;
        }
    }
    public void supprimerCategories(Connection connect, MyDAO dao, String idcategorie) throws Exception{
        try{
            Categorie where=new Categorie();
            where.setId(Integer.parseInt(idcategorie));
            Categorie change=new Categorie();
            change.setEtat(Constantes.CATEGORIE_SUPPRIME);
            dao.update(connect, change, where);
        }catch(Exception e){
            connect.rollback();
            e.printStackTrace();
            throw e;
        }
    }
    public void restaurerCategorie(Connection connect, MyDAO dao, String idCategorie) throws Exception{
        try{
            Categorie where=new Categorie();
            where.setId(Integer.parseInt(idCategorie));
            Categorie change=new Categorie();
            change.setEtat(Constantes.CATEGORIE_CREE);
            dao.update(connect, change, where);
        }catch(Exception e){
            connect.rollback();
            e.printStackTrace();
            throw e;
        }
    }
    public void ajouterCategorie(Connection connect, MyDAO dao, String nom) throws Exception{
        try{
            if(nom==null||nom.isEmpty()||nom.isBlank()){
                throw new Exception(Constantes.MSG_VALEUR_INVALIDE);
            }
            nom=nom.trim();
            Categorie categorie=new Categorie();
            categorie.setNom(nom);
            dao.insertWithoutPrimaryKey(connect, categorie);
        }catch(Exception e){
            connect.rollback();
            e.printStackTrace();
            throw e;
        }
    }
    public void modifierCategorie(Connection connect, MyDAO dao, String idcategorie, String idrolesString) throws Exception{
        try{
            String[] idroles=HandyManUtils.fromJson(String[].class, idrolesString);
            Role[] rolesValides=Constantes.ROLES_AUTORISES;
            Role[] roles=new Role[idroles.length];
            boolean valide;
            for(int i=0;i<roles.length;i++){
                valide=false;
                roles[i]=new Role();
                roles[i].setId(Integer.parseInt(idroles[i]));
                for(Role r:rolesValides){
                    if(r.getId()==roles[i].getId()){
                        valide=true;
                        break;
                    }
                }
                if(valide==false){
                    throw new Exception(Constantes.MSG_ACTION_INVALIDE);
                }
            }
            RoleChecking where=new RoleChecking();
            Categorie whereCategorie=new Categorie();
            whereCategorie.setId(Integer.parseInt(idcategorie));
            where.setCategorie(whereCategorie);
            dao.delete(connect, where);
            RoleChecking[] checkings=new RoleChecking[roles.length];
            for(int i=0;i<checkings.length;i++){
                checkings[i]=new RoleChecking();
                checkings[i].setCategorie(whereCategorie);
                checkings[i].setRole(roles[i]);
            }
            dao.insertWithoutPrimaryKey(connect, RoleChecking.class, checkings);
        }catch(Exception e){
            connect.rollback();
            e.printStackTrace();
            throw e;
        }
    }
    public void supprimerRangees(Connection connect, MyDAO dao, String idrangee) throws Exception{
        try{
            Rangee where=new Rangee();
            where.setId(Integer.parseInt(idrangee));
            Rangee change=new Rangee();
            change.setEtat(Constantes.RANGEE_SUPPRIME);
            dao.update(connect, change, where);
        }catch(Exception e){
            connect.rollback();
            e.printStackTrace();
            throw e;
        }
    }
    public void restaurerRangee(Connection connect, MyDAO dao, String idrangee) throws Exception{
        try{
            Rangee where=new Rangee();
            where.setId(Integer.parseInt(idrangee));
            Rangee change=new Rangee();
            change.setEtat(Constantes.RANGEE_CREE);
            dao.update(connect, change, where);
        }catch(Exception e){
            connect.rollback();
            e.printStackTrace();
            throw e;
        }
    }
    public void ajouterRangee(Connection connect, MyDAO dao, String nom) throws Exception{
        try{
            if(nom==null||nom.isEmpty()||nom.isBlank()){
                throw new Exception(Constantes.MSG_VALEUR_INVALIDE);
            }
            nom=nom.trim();
            Rangee rangee=new Rangee();
            rangee.setNom(nom);
            dao.insertWithoutPrimaryKey(connect, rangee);
        }catch(Exception e){
            connect.rollback();
            e.printStackTrace();
            throw e;
        }
    }
    public void supprimerPlaces(Connection connect, MyDAO dao, String idplace) throws Exception{
        try{
            Place where=new Place();
            where.setId(Integer.parseInt(idplace));
            Place change=new Place();
            change.setEtat(Constantes.PLACE_SUPPRIME);
            dao.update(connect, change, where);
        }catch(Exception e){
            connect.rollback();
            e.printStackTrace();
            throw e;
        }
    }
    public void restaurerPlace(Connection connect, MyDAO dao, String idplace) throws Exception{
        try{
            Place where=new Place();
            where.setId(Integer.parseInt(idplace));
            Place change=new Place();
            change.setEtat(Constantes.PLACE_CREE);
            dao.update(connect, change, where);
        }catch(Exception e){
            connect.rollback();
            e.printStackTrace();
            throw e;
        }
    }
    public void ajouterPlace(Connection connect, MyDAO dao, String nom, String idtype) throws Exception{
        try{
            if(nom==null||nom.isEmpty()||nom.isBlank()){
                throw new Exception(Constantes.MSG_VALEUR_INVALIDE);
            }
            nom=nom.trim();
            Place place=new Place();
            place.setNom(nom);
            TypePlace type=new TypePlace();
            type.setId(Integer.parseInt(idtype));
            place.setTypePlace(type);
            dao.insertWithoutPrimaryKey(connect, place);
        }catch(Exception e){
            connect.rollback();
            e.printStackTrace();
            throw e;
        }
    }
    public void supprimerModes(Connection connect, MyDAO dao, String idmode) throws Exception{
        try{
            ModePaiement where=new ModePaiement();
            where.setId(Integer.parseInt(idmode));
            ModePaiement change=new ModePaiement();
            change.setEtat(Constantes.MODEPAIEMENT_SUPPRIME);
            dao.update(connect, change, where);
        }catch(Exception e){
            connect.rollback();
            e.printStackTrace();
            throw e;
        }
    }
    public void restaurerMode(Connection connect, MyDAO dao, String idmode) throws Exception{
        try{
            ModePaiement where=new ModePaiement();
            where.setId(Integer.parseInt(idmode));
            ModePaiement change=new ModePaiement();
            change.setEtat(Constantes.MODEPAIEMENT_CREE);
            dao.update(connect, change, where);
        }catch(Exception e){
            connect.rollback();
            e.printStackTrace();
            throw e;
        }
    }
    public void ajouterMode(Connection connect, MyDAO dao, String nom) throws Exception{
        try{
            if(nom==null||nom.isEmpty()||nom.isBlank()){
                throw new Exception(Constantes.MSG_VALEUR_INVALIDE);
            }
            nom=nom.trim();
            ModePaiement modePaiement=new ModePaiement();
            modePaiement.setNom(nom);
            dao.insertWithoutPrimaryKey(connect, modePaiement);
        }catch(Exception e){
            connect.rollback();
            e.printStackTrace();
            throw e;
        }
    }
}
