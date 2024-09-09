package com.app.makay.entites;

import java.time.LocalDateTime;

import veda.godao.annotations.Column;
import veda.godao.annotations.ForeignKey;
import veda.godao.annotations.PrimaryKey;
import veda.godao.annotations.Table;

@Table("v_commandes")
public class CommandeEnCours_Vue {
    @PrimaryKey
    @Column("id")
    private Integer id;
    @ForeignKey(recursive = false)
    @Column("idutilisateur")
    private Utilisateur utilisateur;
    @Column("idplace")
    private Integer place;
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
    public Integer getPlace() {
        return place;
    }
    public void setPlace(Integer place) {
        this.place = place;
    }
    public String getNomPlace() {
        return nomPlace;
    }
    public void setNomPlace(String nomPlace) {
        this.nomPlace = nomPlace;
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
    
}
