package com.app.makay.entites;

import java.io.File;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;

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
        for(ActionSuperviseur a:actions){
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
}
