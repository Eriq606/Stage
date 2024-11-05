package com.app.makay.entites;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import com.app.makay.entites.statistiques.ChiffreSemaine;
import com.app.makay.utilitaire.Constantes;
import com.app.makay.utilitaire.MyDAO;

import handyman.HandyManUtils;
import veda.godao.annotations.Column;
import veda.godao.annotations.ForeignKey;
import veda.godao.annotations.PrimaryKey;
import veda.godao.annotations.Table;

@Table("commandes")
public class Commande {
    @PrimaryKey
    @Column("id")
    private Integer id;
    @ForeignKey(recursive = false)
    @Column("idutilisateur")
    private Utilisateur utilisateur;
    @ForeignKey(recursive = true)
    @Column("idplace")
    private Place place;
    @Column("dateheure_ouverture")
    private LocalDateTime ouverture;
    @Column("dateheure_cloture")
    private LocalDateTime cloture;
    @Column("montant")
    private Double montant;
    @Column("etat")
    private Integer etat;
    private CommandeFille[] commandeFilles;
    @Column("reste_a_payer")
    private Double resteAPayer;
    private Paiement[] paiements;
    @Column("montant_offert")
    private Double montantOffert;
    @Column("montant_annulee")
    private Double montantAnnulee;
    private ActionSuperviseur[] actions;
    private double paiementTotal;
    private double offertTotales;
    private double annuleTotales;
    @Column("montant_remises")
    private Double montantRemises;
    private Remise[] remises;
    public Remise[] getRemises() {
        return remises;
    }

    public void setRemises(Remise[] remises) {
        this.remises = remises;
    }

    public Double getMontantRemises() {
        return montantRemises;
    }

    public void setMontantRemises(Double montantRemises) {
        this.montantRemises = montantRemises;
    }

    public double getPaiementTotal() {
        return paiementTotal;
    }

    public void setPaiementTotal(double paiementTotal) {
        this.paiementTotal = paiementTotal;
    }
    public double getOffertTotales() {
        return offertTotales;
    }

    public void setOffertTotales(double offertTotales) {
        this.offertTotales = offertTotales;
    }
    public double getAnnuleTotales() {
        return annuleTotales;
    }

    public void setAnnuleTotales(double annuleTotales) {
        this.annuleTotales = annuleTotales;
    }

    public ActionSuperviseur[] getActions() {
        return actions;
    }

    public void setActions(ActionSuperviseur[] actions) {
        this.actions = actions;
    }

    public Double getMontantAnnulee() {
        return montantAnnulee;
    }

    public void setMontantAnnulee(Double montantAnnulee) {
        this.montantAnnulee = montantAnnulee;
    }

    public Double getMontantOffert() {
        return montantOffert;
    }

    public void setMontantOffert(Double montantOffert) {
        this.montantOffert = montantOffert;
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
    public String recupererOuvertureStringDocument(){
        String date=getOuverture().toLocalDate().toString();
        String heure=getOuverture().toLocalTime().minusNanos(getOuverture().toLocalTime().getNano()).toString();
        return date+" "+heure;
    }
    public String recupererClotureStringDocument(){
        String date=getCloture().toLocalDate().toString();
        String heure=getCloture().toLocalTime().minusNanos(getCloture().toLocalTime().getNano()).toString();
        return date+" "+heure;
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
    public CommandeFille[] getCommandeFilles() {
        return commandeFilles;
    }
    public void setCommandeFilles(CommandeFille[] commandeFilles) {
        this.commandeFilles = commandeFilles;
    }
    public CommandeFille[] recupererCommandeFilles(Connection connect, MyDAO dao) throws Exception{
        CommandeFille where=new CommandeFille();
        where.setCommande(this);
        where.setEtat(0);
        CommandeFille[] commandeFilles=dao.select(connect, CommandeFille.class, where);
        for(int i=0;i<commandeFilles.length;i++){
            commandeFilles[i].recupererAccompagnements(connect, dao);
        }
        setCommandeFilles(commandeFilles);
        return commandeFilles;
    }
    @Override
    public String toString() {
        return "Commande [id=" + id + ", utilisateur=" + utilisateur + ", place=" + place + ", ouverture=" + ouverture
                + ", cloture=" + cloture + ", montant=" + montant + ", etat=" + etat + ", commandeFilles="
                + Arrays.toString(commandeFilles) + "]";
    }
    public String recupererResteAPayerString(){
        return HandyManUtils.number_format(getResteAPayer(), ' ', ',', 2)+" Ar";
    }
    public String recupererMontantString(){
        return HandyManUtils.number_format(getMontant(), ' ', ',', 2)+" Ar";
    }
    public Paiement[] recupererPaiements(Connection connect, MyDAO dao) throws Exception{
        Commande commande=new Commande();
        commande.setId(getId());
        Paiement where=new Paiement();
        where.setCommande(commande);
        where.setEtat(0);
        Paiement[] paiements=dao.select(connect, Paiement.class, where);
        Utilisateur whereUser;
        for(Paiement p:paiements){
            whereUser=new Utilisateur();
            whereUser.setId(p.getUtilisateur().getId());
            p.setUtilisateur(dao.select(connect, Utilisateur.class, whereUser)[0]);
            p.getUtilisateur().setEmail(null);
            p.getUtilisateur().setMotdepasse(null);
        }
        setPaiements(paiements);
        return paiements;
    }
    public String formatterHTML(Connection connect, MyDAO dao, File html) throws Exception{
        Utilisateur utilisateur=dao.select(connect, Utilisateur.class, getUtilisateur())[0];
        utilisateur.setMotdepasse(null);
        String htmlContent=HandyManUtils.getFileContent(html);
        htmlContent=htmlContent.replace("<!-- PLACE-LABEL -->", recupererPlaceLabel());
        htmlContent=htmlContent.replace("<!-- DATEHEURE -->", recupererOuvertureStringDocument());
        htmlContent=htmlContent.replace("<!-- SERVEUR -->", utilisateur.getNom());

        String lignesCmdFilles="";
        for(CommandeFille cf:getCommandeFilles()){
            lignesCmdFilles+=cf.toHtml();
        }
        htmlContent=htmlContent.replace("<!-- LIGNES -->", lignesCmdFilles);

        htmlContent=htmlContent.replace("<!-- MONTANT TOTAL -->", recupererMontantString());

        String lignesPaiements="";
        for(Paiement p:getPaiements()){
            lignesPaiements+=p.toHtml();
        }
        htmlContent=htmlContent.replace("<!-- PAIEMENTS -->", lignesPaiements);
        htmlContent=htmlContent.replace("<!-- PAIEMENT TOTAL -->", recupererPaiementTotalString());
        htmlContent=htmlContent.replace("<!-- CLOTURE -->", recupererClotureStringDocument());

        String lignesActions="";
        for(ActionSuperviseur a:getActions()){
            lignesActions+=a.toHtml();
        }
        htmlContent=htmlContent.replace("<!-- ACTIONS -->", lignesActions);
        htmlContent=htmlContent.replace("<!-- OFFERTS TOTAL -->", recupererOffertTotalString());
        htmlContent=htmlContent.replace("<!-- ANNULEES TOTAL -->", recupererAnnulesTotalString());

        String lignesRemises="";
        for(Remise r:getRemises()){
            lignesRemises+=r.toHtml();
        }
        htmlContent=htmlContent.replace("<!-- REMISES -->", lignesRemises);
        htmlContent=htmlContent.replace("<!-- REMISES TOTAL -->", HandyManUtils.number_format(getMontantRemises(), ' ', ',', 2)+" Ar");
        return htmlContent;
    }
    public int recupererIdUtilisateur(Connection connect, MyDAO dao) throws Exception{
        String query="select idutilisateur from commandes where id=%s";
        query=String.format(query, getId());
        HashMap<String, Object>[] result=dao.select(connect, query);
        int iduser=(int)result[0].get("idutilisateur");
        return iduser;
    }
    public Utilisateur recupereUtilisateur(Connection connect, MyDAO dao) throws Exception{
        Utilisateur where=new Utilisateur();
        where.setId(getUtilisateur().getId());
        Utilisateur utilisateur=dao.select(connect, Utilisateur.class, where)[0];
        utilisateur.setMotdepasse("null");
        setUtilisateur(utilisateur);
        return utilisateur;
    }
    public ActionSuperviseur[] recupererActionsSuperviseurs(Connection connect, MyDAO dao) throws Exception{
        String addon="where idcommandefille in (select id from commande_filles where idcommande=%s) and etat=0";
        addon=String.format(addon, getId());
        ActionSuperviseur[] actions=dao.select(connect, ActionSuperviseur.class, addon);
        Utilisateur where;
        for(ActionSuperviseur a:actions){
            a.getCommandeFille().recupererAccompagnements(connect, dao);
            where=new Utilisateur();
            where.setId(a.getUtilisateur().getId());
            a.setUtilisateur(dao.select(connect, Utilisateur.class, where)[0]);
            a.getUtilisateur().setEmail(null);
            a.getUtilisateur().setMotdepasse(null);
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
    /*
     * select sum(montant / montant_offert / montant_annule / montant_remise) from commandes where etat=20 and ouverture>=%s and ouverture<%s;
     * select *, extract(isodow from date (dateheure_ouverture::date)) as jour_semaine from commandes as v_commande_semaine;
     * select sum(montant), sum(offert), sum(annule), sum(remise), jour_semaine from v_commande_semaine where etat=20 and ouverture>=%s and ouverture<%s group by jour_semaine;
     * select * from commande_filles join commandes as v_commandefilles_commandes;
     * select idproduit, sum(quantite) from v_commandefilles_commandes where etat=0 and ouverture>=%s and ouverture<%s and idcategorie=%s group by idproduit order by sum desc;
     * select idplace, count(*) from commandes where etat=20 and ouverture>=%s and ouverture<%s group by idplace;
     * 
     * ChiffreSemaine{
     *      etiquette,
     *      donnees,
     *      couleur
     * }
     * 
     * ChiffreProduit{
     *      Produit,
     *      quantite
     * }
     * 
     * ChiffrePlace{
     *      Place,
     *      nbCommandes
     * }
     */
    public static double[] chiffresTotales(Connection connect, MyDAO dao, LocalDateTime dateDebut, LocalDateTime dateFin) throws SQLException{
        double[] chiffres=new double[]{0,0,0,0};
        String query="""
            select coalesce(sum(montant), 0) as montant, coalesce(sum(montant_offert),0) as offert, coalesce(sum(montant_annulee),0) as suppression, coalesce(sum(montant_remises),0) as remise
            from commandes where etat=? and dateheure_ouverture>=? and dateheure_ouverture<?
        """;
        try(PreparedStatement statement=connect.prepareStatement(query)){
            statement.setInt(1, Constantes.COMMANDE_PAYEE);
            statement.setTimestamp(2, Timestamp.valueOf(dateDebut));
            statement.setTimestamp(3, Timestamp.valueOf(dateFin));
            try(ResultSet result=statement.executeQuery()){
                if(result.next()){
                    chiffres[0]=result.getDouble("montant");
                    chiffres[1]=result.getDouble("offert");
                    chiffres[2]=result.getDouble("suppression");
                    chiffres[3]=result.getDouble("remise");
                }
                return chiffres;
            }
        }
    }
    public static ChiffreSemaine[] chiffresSemaine(Connection connect, MyDAO dao, LocalDateTime dateDebut, LocalDateTime dateFin) throws SQLException{
        String query="""
            select coalesce(sum(montant), 0) as montant, coalesce(sum(montant_offert),0) as offert, coalesce(sum(montant_annulee),0) as suppression, coalesce(sum(montant_remises),0) as remise, jour_semaine_ouverture
            from v_commande_semaine where etat=? and dateheure_ouverture>=? and dateheure_ouverture<? group by jour_semaine_ouverture order by jour_semaine_ouverture
        """;
        boolean vide=true;
        LinkedList<Double> montants=new LinkedList<>(){{add(0.);add(0.);add(0.);add(0.);add(0.);add(0.);add(0.);}};
        LinkedList<Double> offerts=new LinkedList<>(){{add(0.);add(0.);add(0.);add(0.);add(0.);add(0.);add(0.);}};
        LinkedList<Double> supprimes=new LinkedList<>(){{add(0.);add(0.);add(0.);add(0.);add(0.);add(0.);add(0.);}};
        LinkedList<Double> remises=new LinkedList<>(){{add(0.);add(0.);add(0.);add(0.);add(0.);add(0.);add(0.);}};
        try(PreparedStatement statement=connect.prepareStatement(query)){
            statement.setInt(1, Constantes.COMMANDE_PAYEE);
            statement.setTimestamp(2, Timestamp.valueOf(dateDebut));
            statement.setTimestamp(3, Timestamp.valueOf(dateFin));
            try(ResultSet result=statement.executeQuery()){
                while(result.next()){
                    if(vide==true){
                        montants.clear();
                        offerts.clear();
                        supprimes.clear();
                        remises.clear();
                        vide=false;
                    }
                    montants.add(result.getDouble("montant"));
                    offerts.add(result.getDouble("offert"));
                    supprimes.add(result.getDouble("suppression"));
                    remises.add(result.getDouble("remise"));
                }
            }
        }
        ChiffreSemaine[] chiffres=new ChiffreSemaine[4];
        chiffres[0]=new ChiffreSemaine();
        chiffres[0].setEtiquette("Montant");
        chiffres[0].setDonnees(montants.toArray(new Double[montants.size()]));
        chiffres[0].setCouleur(Constantes.STAT_COULEUR_1);

        chiffres[1]=new ChiffreSemaine();
        chiffres[1].setEtiquette("Offerts");
        chiffres[1].setDonnees(offerts.toArray(new Double[offerts.size()]));
        chiffres[1].setCouleur(Constantes.STAT_COULEUR_2);

        chiffres[2]=new ChiffreSemaine();
        chiffres[2].setEtiquette("Suppressions");
        chiffres[2].setDonnees(supprimes.toArray(new Double[supprimes.size()]));
        chiffres[2].setCouleur(Constantes.STAT_COULEUR_3);

        chiffres[3]=new ChiffreSemaine();
        chiffres[3].setEtiquette("Remises");
        chiffres[3].setDonnees(remises.toArray(new Double[remises.size()]));
        chiffres[3].setCouleur(Constantes.STAT_COULEUR_4);
        return chiffres;
    }
    public static Object[] statistiques(Connection connect, MyDAO dao, LocalDateTime dateDebut, LocalDateTime dateFin, String idcategorie) throws Exception{
        Object[] objets=new Object[2];
        objets[0]=chiffresSemaine(connect, dao, dateDebut, dateFin);
        objets[1]=Produit.chiffreProduits(connect, dao, dateDebut, dateFin, Integer.parseInt(idcategorie));
        // objets[2]=Place.chiffrePlacesVisitees(connect, dao, dateDebut, dateFin);
        // objets[3]=Place.chiffrePlacesNonVisitees(connect, dao, dateDebut, dateFin);
        return objets;
    }
    public static LocalDateTime recupererPremiereDate(Connection connect, MyDAO dao) throws SQLException{
        String query="select coalesce(min(dateheure_ouverture),current_timestamp) as datedebut from commandes where etat=?";
        LocalDateTime dateDebut=null;
        try(PreparedStatement statement=connect.prepareStatement(query)){
            statement.setInt(1, Constantes.COMMANDE_PAYEE);
            try(ResultSet result=statement.executeQuery()){
                if(result.next()){
                    dateDebut=result.getTimestamp("datedebut").toLocalDateTime();
                }
                return dateDebut;
            }
        }
    }
    public static LocalDateTime recupererDerniereDate(Connection connect, MyDAO dao) throws SQLException{
        String query="select coalesce(max(dateheure_ouverture),current_timestamp) as datefin from commandes where etat=?";
        LocalDateTime dateFin=null;
        try(PreparedStatement statement=connect.prepareStatement(query)){
            statement.setInt(1, Constantes.COMMANDE_PAYEE);
            try(ResultSet result=statement.executeQuery()){
                if(result.next()){
                    dateFin=result.getTimestamp("datefin").toLocalDateTime();
                }
                return dateFin;
            }
        }
    }
}
