package com.app.makay.entites;

import java.time.LocalDate;
import java.time.LocalDateTime;

import handyman.HandyManUtils;
import veda.godao.annotations.Column;
import veda.godao.annotations.ForeignKey;
import veda.godao.annotations.PrimaryKey;
import veda.godao.annotations.Table;

@Table("paiements")
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
    @ForeignKey(recursive = false)
    @Column("idutilisateur")
    private Utilisateur utilisateur;
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Commande getCommande() {
        return commande;
    }
    public void setCommande(Commande commande) {
        this.commande = commande;
    }
    public ModePaiement getModePaiement() {
        return modePaiement;
    }
    public void setModePaiement(ModePaiement modePaiement) {
        this.modePaiement = modePaiement;
    }
    public LocalDateTime getDateheure() {
        return dateheure;
    }
    public void setDateheure(LocalDateTime dateheure) {
        this.dateheure = dateheure;
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
    public String recupererHeure(){
        String reponse=LocalDate.now().isAfter(getDateheure().toLocalDate())?getDateheure().toLocalDate().toString()+" ":"";
        int heure=getDateheure().getHour();
        String heureStr=String.valueOf(heure);
        if(heure<10){
            heureStr="0"+heureStr;
        }
        int minute=getDateheure().getMinute();
        String minuteStr=String.valueOf(minute);
        if(minute<10){
            minuteStr="0"+minuteStr;
        }
        reponse+=heureStr+"h"+minuteStr;
        return reponse;
    }
    public String recupererMontantString(){
        return HandyManUtils.number_format(getMontant(), ' ', ',', 2)+" Ar";
    }
}
