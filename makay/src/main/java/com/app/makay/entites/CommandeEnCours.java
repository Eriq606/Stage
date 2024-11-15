package com.app.makay.entites;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;

import com.app.makay.utilitaire.Constantes;
import com.app.makay.utilitaire.MyDAO;

import handyman.HandyManUtils;
import veda.godao.annotations.Column;
import veda.godao.annotations.ForeignKey;
import veda.godao.annotations.PrimaryKey;
import veda.godao.annotations.Table;

@Table("v_commandes")
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
    public double getMontantRemises() {
        return montantRemises;
    }

    public void setMontantRemises(double montantRemises) {
        this.montantRemises = montantRemises;
    }

    public Remise[] getRemises() {
        return remises;
    }

    public void setRemises(Remise[] remises) {
        this.remises = remises;
    }

    public double getAnnuleTotales() {
        return annuleTotales;
    }

    public void setAnnuleTotales(double annuleTotales) {
        this.annuleTotales = annuleTotales;
    }

    public double getOffertTotales() {
        return offertTotales;
    }

    public void setOffertTotales(double offertTotales) {
        this.offertTotales = offertTotales;
    }
    public double getPaiementTotal() {
        return paiementTotal;
    }

    public void setPaiementTotal(double paiementTotal) {
        this.paiementTotal = paiementTotal;
    }
    public ActionSuperviseur[] getActions() {
        return actions;
    }

    public void setActions(ActionSuperviseur[] actions) {
        this.actions = actions;
    }

    public Paiement[] getPaiements() {
        return paiements;
    }

    public void setPaiements(Paiement[] paiements) {
        this.paiements = paiements;
    }

    public Double getResteAPayer() {
        return resteAPayer;
    }

    public void setResteAPayer(Double resteAPayer) {
        this.resteAPayer = resteAPayer;
    }

    public String recupererEtatString(){
        switch(getEtat()){
            case Constantes.COMMANDE_PAYEE:
                return Constantes.ETATCOMMANDE_PAYEE;
            case Constantes.COMMANDE_ANNULEE:
                return Constantes.ETATCOMMANDE_ANNULEE;
        }
        return "...";
    }

    public String recupererPlaceLabel(){
        String label="Place : ";
        switch(getPlace().getTypePlace().getNumero()){
            case Constantes.PLACE_BAR:
                label=Constantes.LABEL_BAR+" : ";
                break;
            case Constantes.PLACE_SALLE:
                label=Constantes.LABEL_SALLE+" : ";
                break;
            case Constantes.PLACE_TERRASSE:
                label=Constantes.LABEL_TERRASSE+" : ";
        }
        label+=getPlace().getNom();
        return label;
    }
    
    public String recupererHeure(){
        String reponse=LocalDate.now().isAfter(getOuverture().toLocalDate())?getOuverture().toLocalDate().toString()+" ":"";
        int heure=getOuverture().getHour();
        String heureStr=String.valueOf(heure);
        if(heure<10){
            heureStr="0"+heureStr;
        }
        int minute=getOuverture().getMinute();
        String minuteStr=String.valueOf(minute);
        if(minute<10){
            minuteStr="0"+minuteStr;
        }
        reponse+=heureStr+"h"+minuteStr;
        return reponse;
    }
    public String recupererHeureCloture(){
        String reponse=LocalDate.now().isAfter(getCloture().toLocalDate())?getCloture().toLocalDate().toString()+" ":"";
        int heure=getCloture().getHour();
        String heureStr=String.valueOf(heure);
        if(heure<10){
            heureStr="0"+heureStr;
        }
        int minute=getCloture().getMinute();
        String minuteStr=String.valueOf(minute);
        if(minute<10){
            minuteStr="0"+minuteStr;
        }
        reponse+=heureStr+"h"+minuteStr;
        return reponse;
    }
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
    public Place getPlace() {
        return place;
    }
    public void setPlace(Place place) {
        this.place = place;
    }
    public LocalDateTime getOuverture() {
        return ouverture;
    }
    public void setOuverture(LocalDateTime ouverture) {
        this.ouverture = ouverture;
    }
    public LocalDateTime getCloture() {
        return cloture;
    }
    public void setCloture(LocalDateTime cloture) {
        this.cloture = cloture;
    }
    public Double getMontant() {
        return montant;
    }
    public void setMontant(Double montant) {
        this.montant = montant;
    }
    public Integer getEtat() {
        return etat;
    }
    public void setEtat(Integer etat) {
        this.etat = etat;
    }
    
    public CommandeFilleEnCours[] recupererCommandeFillesWithoutSet(Connection connect, MyDAO dao) throws Exception{
        String query="select * from v_commandefille_produits where idcommande=%s";
        query=String.format(query, getId());
        HashMap<String, Object>[] objets=dao.select(connect, query);
        CommandeFilleEnCours[] commandeFilles=new CommandeFilleEnCours[objets.length];
        Produit produit;
        Categorie categorie;
        for(int i=0;i<commandeFilles.length;i++){
            commandeFilles[i]=new CommandeFilleEnCours();
            commandeFilles[i].setId((int)objets[i].get("id"));
            commandeFilles[i].setCommande(this);
            produit=new Produit();
            produit.setId((int)objets[i].get("idproduit"));
            produit.setNom((String)objets[i].get("nom"));
            produit.setPrix(((BigDecimal)objets[i].get("prix")).doubleValue());
            categorie=new Categorie();
            categorie.setId((int)objets[i].get("idcategorie"));
            produit.setCategorie(categorie);
            commandeFilles[i].setProduit(produit);
            commandeFilles[i].setPu(((BigDecimal)objets[i].get("prix_unitaire")).doubleValue());
            commandeFilles[i].setQuantite(((BigDecimal)objets[i].get("quantite")).doubleValue());
            commandeFilles[i].setMontant(((BigDecimal)objets[i].get("montant")).doubleValue());
            commandeFilles[i].setNotes((String)objets[i].get("notes"));
            commandeFilles[i].setEstTermine((int)objets[i].get("est_termine"));
            commandeFilles[i].setEtat((int)objets[i].get("etat"));
            commandeFilles[i].recupererAccompagnements(connect, dao);
        }
        // setCommandeFilles(commandeFilles);
        return commandeFilles;
    }
    public CommandeFilleEnCours[] recupererCommandeFilles(Connection connect, MyDAO dao) throws Exception{
        setCommandeFilles(recupererCommandeFillesWithoutSet(connect, dao));
        return commandeFilles;
    }
    public CommandeFilleEnCours[] recupererCommandeFillesChecking(Connection connect, MyDAO dao, Role role) throws Exception{
        String query="select * from v_commandefille_produits where idcommande=%s and idcategorie in (select idcategorie from v_role_categorie_produits_checkings where idrole=%s)";
        query=String.format(query, getId(), role.getId());
        HashMap<String, Object>[] objets=dao.select(connect, query);
        CommandeFilleEnCours[] commandeFilles=new CommandeFilleEnCours[objets.length];
        Produit produit;
        Categorie categorie;
        for(int i=0;i<commandeFilles.length;i++){
            commandeFilles[i]=new CommandeFilleEnCours();
            commandeFilles[i].setId((int)objets[i].get("id"));
            commandeFilles[i].setCommande(this);
            produit=new Produit();
            produit.setId((int)objets[i].get("idproduit"));
            produit.setNom((String)objets[i].get("nom"));
            produit.setPrix(((BigDecimal)objets[i].get("prix")).doubleValue());
            categorie=new Categorie();
            categorie.setId((int)objets[i].get("idcategorie"));
            produit.setCategorie(categorie);
            commandeFilles[i].setProduit(produit);
            commandeFilles[i].setPu(((BigDecimal)objets[i].get("prix_unitaire")).doubleValue());
            commandeFilles[i].setQuantite(((BigDecimal)objets[i].get("quantite")).doubleValue());
            commandeFilles[i].setMontant(((BigDecimal)objets[i].get("montant")).doubleValue());
            commandeFilles[i].setNotes((String)objets[i].get("notes"));
            commandeFilles[i].setEstTermine((int)objets[i].get("est_termine"));
            commandeFilles[i].recupererAccompagnements(connect, dao);
        }
        setCommandeFilles(commandeFilles);
        return commandeFilles;
    }
    public CommandeFilleEnCours[] getCommandeFilles() {
        return commandeFilles;
    }
    public void setCommandeFilles(CommandeFilleEnCours[] commandeFilles) {
        this.commandeFilles = commandeFilles;
    }

    public String getNomPlace() {
        return nomPlace;
    }

    public void setNomPlace(String nomPlace) {
        this.nomPlace = nomPlace;
    }
    public String recupererMontantString(){
        String montantString=HandyManUtils.number_format(getMontant(), ' ', ',', 2);
        montantString+=" Ar";
        return montantString;
    }
    public String recupererResteAPayerString(){
        return HandyManUtils.number_format(getResteAPayer(), ' ', ',', 2)+" Ar";
    }
    public Paiement[] recupererPaiements(Connection connect, MyDAO dao) throws Exception{
        Commande commande=new Commande();
        commande.setId(getId());
        Paiement where=new Paiement();
        where.setCommande(commande);
        where.setEtat(0);
        Paiement[] paiements=dao.select(connect, Paiement.class, where);
        for(Paiement p:paiements){
            p.getUtilisateur().setEmail(null);
            p.getUtilisateur().setMotdepasse(null);
        }
        setPaiements(paiements);
        return paiements;
    }
    public String recupererCouleur(){
        switch(getEtat()){
            case Constantes.COMMANDE_PAYEE:
                return Constantes.COULEUR_SUCCES;
            case Constantes.COMMANDE_ANNULEE:
                return Constantes.COULEUR_CANCELED;
        }
        return "";
    }
    public boolean estTermine(){
        boolean termine=true;
        for(CommandeFilleEnCours cf:getCommandeFilles()){
            if(cf.getEstTermine()==Constantes.COMMANDEFILLE_ESTENCOURS){
                termine=false;
                break;
            }
        }
        return termine;
    }
    public String recupererEstTermineCouleur(){
        if(estTermine()){
            return "#9cff9c";
        }
        return "";
    }
    public ActionSuperviseur[] recupererActionsSuperviseurs(Connection connect, MyDAO dao) throws Exception{
        String addon="where idcommandefille in (select id from commande_filles where idcommande=%s) and etat=0";
        addon=String.format(addon, getId());
        ActionSuperviseur[] actions=dao.select(connect, ActionSuperviseur.class, addon);
        for(ActionSuperviseur a:actions){
            a.getUtilisateur().setEmail(null);
            a.getUtilisateur().setMotdepasse(null);
            a.getCommandeFille().recupererAccompagnements(connect, dao);
        }
        setActions(actions);
        return actions;
    }
    public double recupererPaiementTotal(){
        double total=0;
        for(Paiement p:paiements){
            total+=p.getMontant();
        }
        setPaiementTotal(total);
        return total;
    }
    public String recupererPaiementTotalString(){
        return HandyManUtils.number_format(paiementTotal, ' ', ',', 2)+" Ar";
    }
    public String recupererOffertTotalString(){
        return HandyManUtils.number_format(offertTotales, ' ', ',', 2)+" Ar";
    }
    public String recupererAnnulesTotalString(){
        return HandyManUtils.number_format(annuleTotales, ' ', ',', 2)+" Ar";
    }
    public double[] recupererActionsTotales(){
        double totalOfferts=0;
        double totalAnnules=0;
        for(ActionSuperviseur a:actions){
            switch(a.getAction()){
                case Constantes.COMMANDEFILLE_OFFERT:
                    totalOfferts+=a.getMontant();
                    break;
                case Constantes.COMMANDEFILLE_ANNULEE:
                    totalAnnules+=a.getMontant();
            }
        }
        setOffertTotales(totalOfferts);
        setAnnuleTotales(totalAnnules);
        return new double[]{totalOfferts, totalAnnules};
    }
    public Remise[] recupererRemises(Connection connect, MyDAO dao) throws Exception{
        String query="select * from v_remise_commandes where etat=0 and idcommande=%s";
        query=String.format(query, getId());
        Remise[] remises=dao.selectWithPrimary(connect, query, Remise.class);
        for(Remise r:remises){
            r.getUtilisateur().setEmail(null);
            r.getUtilisateur().setMotdepasse(null);
            r.getCommandeFille().recupererAccompagnements(connect, dao);
        }
        setRemises(remises);
        return remises;
    }
    public String recupererMontantRemiseString(){
        return HandyManUtils.number_format(getMontantRemises(), ' ', ',', 2)+" Ar";
    }
}
