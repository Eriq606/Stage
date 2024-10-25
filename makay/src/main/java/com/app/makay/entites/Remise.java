package com.app.makay.entites;

import java.time.LocalDateTime;

import handyman.HandyManUtils;
import veda.godao.annotations.Column;
import veda.godao.annotations.ForeignKey;
import veda.godao.annotations.PrimaryKey;
import veda.godao.annotations.Table;

@Table("remises")
public class Remise {
    @PrimaryKey
    @Column("id")
    private Integer id;
    @ForeignKey(recursive = false)
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
    public LocalDateTime getDateheure() {
        return dateheure;
    }
    public void setDateheure(LocalDateTime dateheure) {
        this.dateheure = dateheure;
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
    public CommandeFille getCommandeFille() {
        return commandeFille;
    }
    public void setCommandeFille(CommandeFille commandeFille) {
        this.commandeFille = commandeFille;
    }
    public Double getQuantite() {
        return quantite;
    }
    public void setQuantite(Double quantite) {
        this.quantite = quantite;
    }
    public Double getNouveauMontant() {
        return nouveauMontant;
    }
    public void setNouveauMontant(Double nouveauMontant) {
        this.nouveauMontant = nouveauMontant;
    }
    public Integer getEtat() {
        return etat;
    }
    public void setEtat(Integer etat) {
        this.etat = etat;
    }
    public String recupererDateHeureString(){
        String date=getDateheure().toLocalDate().toString();
        int heure=getDateheure().toLocalTime().getHour();
        int minute=getDateheure().toLocalTime().getMinute();
        int secondes=getDateheure().toLocalTime().getSecond();
        return date+" "+heure+":"+minute+":"+secondes;
    }
    public String recupererCommandeLabel(){
        if(getCommandeFille().getAccompagnements().length==0){
            return getCommandeFille().getProduit().getNom()+" "+getCommandeFille().getNotes();
        }
        String accompagnements="(";
        for(Accompagnement a:getCommandeFille().getAccompagnements()){
            accompagnements+=a.getNom()+",";
        }
        accompagnements=accompagnements.substring(0, accompagnements.length()-1)+")";
        return getCommandeFille().getProduit().getNom()+" "+getCommandeFille().getNotes()+accompagnements;
    }
    public String recupererAncienMontantString(){
        double ancienMontant=getCommandeFille().getPu()*getQuantite();
        return HandyManUtils.number_format(ancienMontant, ' ', ',', 2);
    }
    public String recupererNouveauMontantString(){
        return HandyManUtils.number_format(nouveauMontant*getQuantite(), ' ', ',', 2);
    }
    public String recupererTaux(){
        double taux=100-(getNouveauMontant()*100/getCommandeFille().getPu());
        return taux+" %";
    }
    @Override
    public String toString() {
        return "Remise [id=" + id + ", quantite=" + quantite + ", nouveauMontant=" + nouveauMontant + ", etat=" + etat
                + ", dateheure=" + dateheure + "]";
    }
    
}
