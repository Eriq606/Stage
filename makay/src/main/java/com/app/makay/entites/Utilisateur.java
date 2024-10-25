package com.app.makay.entites;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import com.app.makay.entites.annulation.AnnulationPaiement;
import com.app.makay.entites.liaison.AccompagnementCommande;
import com.app.makay.entites.liaison.RangeePlace;
import com.app.makay.entites.liaison.RangeeUtilisateur;
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
        Utilisateur where=new Utilisateur();
        where.setEmail(email);
        where.setMotdepasse(motDePasse);
        where.setEtat(0);
        Utilisateur[] target=dao.select(connect, Utilisateur.class, where);
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
        if(serveur!=null&&serveur.isEmpty()==false&&serveur!="0"){
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
            if(serveur!=null&&serveur.isEmpty()==false&&serveur!="0"){
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
        if(serveur!=null&&serveur.isEmpty()==false&&serveur!="0"){
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
            if(serveur!=null&&serveur.isEmpty()==false&&serveur!="0"){
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
            ModePaiement vat=new ModePaiement();
            vat.setId(Constantes.IDMODEPAIEMENT_VAT);
            Paiement paiementVAT=new Paiement();
            paiementVAT.setCommande(commandeFille.getCommande());
            paiementVAT.setModePaiement(vat);
            paiementVAT.setUtilisateur(this);
            paiementVAT.setMontant(montantAction);
            Commande whereCommande=new Commande();
            whereCommande.setId(commandeFille.getCommande().getId());
            Commande changeCommande=new Commande();
            changeCommande.setResteAPayer(commandeFille.getCommande().getResteAPayer()-montantAction);
            switch(actionSuperviseur.getAction()){
                case Constantes.COMMANDEFILLE_OFFERT:
                    changeCommande.setMontantOffert(commandeFille.getCommande().getMontantOffert()+montantAction);
                    dao.insertWithoutPrimaryKey(connect, paiementVAT);
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
            actionSuperviseur.setUtilisateur(this);
            dao.insertWithoutPrimaryKey(connect, actionSuperviseur);
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
        String addOn="where etat=0 and id in (select idutilisateur from commandes group by idutilisateur)";
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
            whereCommande.setEtat(Constantes.COMMANDE_ADDITION);
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
            if(paiement.getModePaiement().getId()==Constantes.IDMODEPAIEMENT_VAT&&getRole().getNumero().equals(Constantes.ROLE_SUPERVISEUR)){
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
}
