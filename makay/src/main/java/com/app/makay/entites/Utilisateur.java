package com.app.makay.entites;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import com.app.makay.iris.IrisUser;
import com.app.makay.utilitaire.Constantes;
import com.app.makay.utilitaire.ModelLink;
import com.app.makay.utilitaire.MyDAO;
import com.app.makay.utilitaire.SessionUtilisateur;

import handyman.HandyManUtils;
import jakarta.servlet.http.HttpSession;
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
                responses[0]=Constantes.URL_RESET_CACHE_SUPERVISEUR;
                responses[1]=Constantes.URL_RECEIVE_NOTIFY_SUPERVISEUR;
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
        String query="select idplace, nom_place, nom_type_place, numero_type_place from v_places_utilisateurs where idutilisateur="+getId();
        HashMap<String, Object>[] objets=dao.select(connect, query);
        Place[] places=new Place[objets.length];
        TypePlace type;
        for(int i=0;i<places.length;i++){
            places[i]=new Place();
            places[i].setId((int)objets[i].get("idplace"));
            places[i].setNom((String)objets[i].get("nom_place"));
            type=new TypePlace();
            type.setNom((String)objets[i].get("nom_type_place"));
            type.setNumero((String)objets[i].get("numero_type_place"));
            places[i].setTypePlace(type);
        }
        setPlaces(places);
        return places;
    }
    public int passerCommande(Connection connect, MyDAO dao, Commande commande, CommandeFille[] commandeFilles) throws Exception{
        try{
            if(commandeFilles.length==0){
                throw new Exception("Commande vide");
            }
            commande.setOuverture(LocalDateTime.now());
            commande.setResteAPayer(commande.getMontant());
            int idCommande=dao.insertWithoutPrimaryKey(connect, commande);
            commande.setId(idCommande);
            int idcommandeFille;
            LinkedList<AccompagnementCommande> accompCommandes=new LinkedList<>();
            AccompagnementCommande accompCommande;
            for(int i=0;i<commandeFilles.length;i++){
                commandeFilles[i].setCommande(commande);
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
    public Produit[] recupererProduitsCorrespondant(Connection connect, MyDAO dao) throws Exception{
        String addOn="where idcategorie in (select idcategorie from v_role_categorie_produits where idrole=%s) and etat=0";
        addOn=String.format(addOn, getRole().getId());
        Produit[] produits=dao.select(connect, Produit.class, addOn);
        return produits;
    }
    public CommandeEnCours[] recupererCommandesCorrespondantes(Connection connect, MyDAO dao) throws Exception{
        String addOn="where idutilisateur=%s and etat<20 order by dateheure_ouverture";
        addOn=String.format(addOn, getId());
        CommandeEnCours[] commandes=dao.select(connect, CommandeEnCours.class, addOn);
        for(int i=0;i<commandes.length;i++){
            commandes[i].recupererCommandeFilles(connect, dao);
        }
        return commandes;
    }
    public CommandeEnCours[] recupererCommandesCorrespondantes(Connection connect, MyDAO dao, int offset, String table) throws Exception{
        String addOn="where idutilisateur=%s and etat<20 order by dateheure_ouverture limit %s offset %s";
        addOn=String.format(addOn, getId(), Constantes.PAGINATION_LIMIT, offset);
        if(table!=null){
            addOn="where nom_place='%s' and idutilisateur=%s and etat<20 order by dateheure_ouverture limit %s offset %s";
            addOn=String.format(addOn, table, getId(), Constantes.PAGINATION_LIMIT, offset);
        }
        CommandeEnCours[] commandes=dao.select(connect, CommandeEnCours.class, addOn);
        for(int i=0;i<commandes.length;i++){
            commandes[i].recupererCommandeFilles(connect, dao);
        }
        return commandes;
    }
    public CommandeEnCours[] recupererCommandesChecking(Connection connect, MyDAO dao, int offset, String table) throws Exception{
        String addOn="where id in (select idcommande from v_commandefille_produits where idcategorie in (select idcategorie from v_role_categorie_produits_checkings where idrole=%s) and est_termine=-1 group by idcommande) and etat<20 order by dateheure_ouverture limit %s offset %s";
        addOn=String.format(addOn, getRole().getId(), Constantes.PAGINATION_LIMIT, offset);
        if(table!=null){
            addOn="where nom_place='%s' and id in (select idcommande from v_commandefille_produits where idcategorie in (select idcategorie from v_role_categorie_produits_checkings where idrole=%s) and est_termine=-1 group by idcommande) and etat<20 order by dateheure_ouverture limit %s offset %s";
            addOn=String.format(addOn, table, getRole().getId(), Constantes.PAGINATION_LIMIT, offset);
        }
        CommandeEnCours[] commandes=dao.select(connect, CommandeEnCours.class, addOn);
        for(int i=0;i<commandes.length;i++){
            commandes[i].recupererCommandeFillesChecking(connect, dao, getRole());
        }
        return commandes;
    }
    public void modifierCommande(Connection connect, MyDAO dao, Commande commande, CommandeFille[] commandeFilles) throws Exception{
        try{
            Commande where=new Commande();
            where.setId(commande.getId());
            Commande change=new Commande();
            change.setMontant(commande.getMontant());
            change.setResteAPayer(commande.getMontant());
            dao.update(connect, change, where);
            int idcommandeFille;
            LinkedList<AccompagnementCommande> accompCommandes=new LinkedList<>();
            AccompagnementCommande accompCommande;
            for(int i=0;i<commandeFilles.length;i++){
                commandeFilles[i].setCommande(where);
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
                return;
            }
            AccompagnementCommande[] accomps=accompCommandes.toArray(new AccompagnementCommande[accompCommandes.size()]);
            dao.insertWithoutPrimaryKey(connect, AccompagnementCommande.class, accomps);
        }catch(Exception e){
            connect.rollback();
            throw e;
        }
    }
    public void checkCommandeFille(Connection connect, MyDAO dao, CommandeFille commandeFille) throws Exception{
        try{
            CommandeFilleTerminee ct=new CommandeFilleTerminee();
            ct.setUtilisateur(this);
            ct.setCommandeFille(commandeFille);
            ct.setDateheure(LocalDateTime.now());
            ct.setEstTermine(1);
            dao.insertWithoutPrimaryKey(connect, ct);
        }catch(Exception e){
            connect.rollback();
            throw e;
        }
    }
    public CommandeEnCours[] recupererHistoriqueCommande(Connection connect, MyDAO dao, int offset) throws Exception{
        try(PreparedStatement statement=connect.prepareStatement("select * from v_commandes where etat>10 and etat<40 order by dateheure_ouverture desc limit ? offset ?");
            PreparedStatement statement2=connect.prepareStatement("select count(*) from v_commandes where etat>10 and etat<40 limit ?")){
            statement.setInt(1, Constantes.PAGINATION_LIMIT);
            statement.setInt(2, (offset-1)*Constantes.PAGINATION_LIMIT);
            statement2.setInt(1, Constantes.PAGINATION_LIMIT);
            try(ResultSet result=statement.executeQuery();
                ResultSet result2=statement2.executeQuery()){
                int compte=result2.next()?result2.getInt(1):0;
                CommandeEnCours[] commandes=new CommandeEnCours[compte];
                Place place;
                TypePlace typePlace;
                Utilisateur utilisateur;
                for(int i=0;result.next();i++){
                    commandes[i]=new CommandeEnCours();
                    commandes[i].setId(result.getInt("id"));
                    commandes[i].setCloture(result.getTimestamp("dateheure_cloture").toLocalDateTime());
                    commandes[i].setEtat(result.getInt("etat"));
                    commandes[i].setMontant(result.getDouble("montant"));
                    commandes[i].setNomPlace(result.getString("nom_place"));
                    place=new Place();
                    place.setId(result.getInt("idplace"));
                    place.setNom(result.getString("nom_place"));
                    typePlace=new TypePlace();
                    typePlace.setId(result.getInt("idtypeplace"));
                    typePlace.setNumero(result.getString("numero_type_place"));
                    place.setTypePlace(typePlace);
                    commandes[i].setPlace(place);
                    utilisateur=new Utilisateur();
                    utilisateur.setId(result.getInt("idutilisateur"));
                    commandes[i].setUtilisateur(dao.select(connect, Utilisateur.class, utilisateur)[0]);
                    commandes[i].setOuverture(result.getTimestamp("dateheure_ouverture").toLocalDateTime());
                    commandes[i].recupererCommandeFilles(connect, dao);
                }
                return commandes;
            }
        }
    }
    public CommandeEnCours[] recupererHistoriqueCommande(Connection connect, MyDAO dao, int offset, String table, String ouvertureDebut, String ouvertureFin, String clotureDebut,
                                                         String clotureFin, String[] modepaiement, String produit, String accompagnement, String notes) throws Exception{
        String query="select * from v_commandes where etat>10 and etat<40 and ";
        LinkedList<String> listQuery=new LinkedList<>();
        LocalDateTime ouvertureDebutTime=null, ouvertureFinTime=null, clotureDebutTime=null, clotureFinTime=null;
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
        System.out.println(query);
        try(PreparedStatement statement=connect.prepareStatement(query)){
            int indice=1;
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
                    liste.add(commande);
                }
                CommandeEnCours[] commandes=liste.toArray(new CommandeEnCours[]{});
                return commandes;
            }
        }
    }
    public int recupererCountHistoriqueCommande(Connection connect, MyDAO dao, int offset, String table, String ouvertureDebut, String ouvertureFin, String clotureDebut,
                                                         String clotureFin, String[] modepaiement, String produit, String accompagnement, String notes) throws Exception{
        String query="select count(*) from v_commandes where etat>10 and etat<40 and ";
        LinkedList<String> listQuery=new LinkedList<>();
        LocalDateTime ouvertureDebutTime=null, ouvertureFinTime=null, clotureDebutTime=null, clotureFinTime=null;
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
            Commande change=new Commande();
            change.setEtat(Constantes.COMMANDE_ADDITION);
            dao.update(connect, change, commande);
        }catch(Exception e){
            connect.rollback();
            e.printStackTrace();
            throw e;
        }
    }
    public CommandeEnCours[] recupererDemandesAddition(Connection connect, MyDAO dao, int offset, String table) throws Exception{
        String addOn="where etat=10 and reste_a_payer>0 order by dateheure_ouverture limit %s offset %s";
        // CommandeEnCours where=new CommandeEnCours();
        // where.setEtat(Constantes.COMMANDE_ADDITION);
        addOn=String.format(addOn, Constantes.PAGINATION_LIMIT, offset);
        if(table.isEmpty()==false){
            addOn="where nom_table='%s' and etat=10 and reste_a_payer>0 order by dateheure_ouverture limit %s offset %s";
            addOn=String.format(addOn, table.replace("\'", "\'\'"), Constantes.PAGINATION_LIMIT, offset);
            // where.setNomPlace(table);
        }
        CommandeEnCours[] commandes=dao.select(connect, CommandeEnCours.class, addOn);
        for(CommandeEnCours c:commandes){
            c.recupererCommandeFilles(connect, dao);
        }
        return commandes;
    }
    public void payer(Connection connect, MyDAO dao, Paiement paiement) throws Exception{
        try{
            if(paiement.getMontant()>paiement.getCommande().getResteAPayer()){
                throw new Exception(Constantes.MSG_PAIEMENT_MONTANT_INVALIDE);
            }
            paiement.setUtilisateur(this);
            dao.insertWithoutPrimaryKey(connect, paiement);
            Commande where=new Commande();
            where.setId(paiement.getCommande().getId());
            Commande change=new Commande();
            change.setResteAPayer(paiement.getCommande().getResteAPayer()-paiement.getMontant());
            if(change.getResteAPayer()==0){
                change.setCloture(LocalDateTime.now());
                change.setEtat(Constantes.COMMANDE_PAYEE);
            }
            dao.update(connect, change, where);
        }catch(Exception e){
            connect.rollback();
            throw e;
        }
    }
}
