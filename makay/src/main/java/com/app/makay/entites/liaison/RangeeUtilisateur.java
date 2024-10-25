package com.app.makay.entites.liaison;

import java.time.LocalDateTime;

import com.app.makay.entites.Rangee;
import com.app.makay.entites.Utilisateur;

import veda.godao.annotations.Column;
import veda.godao.annotations.ForeignKey;
import veda.godao.annotations.PrimaryKey;
import veda.godao.annotations.Table;

@Table("rangee_utilisateurs")
public class RangeeUtilisateur {
    @PrimaryKey
    @Column("id")
    private Integer id;
    @ForeignKey(recursive = true)
    @Column("idrangee")
    private Rangee rangee;
    @ForeignKey(recursive = true)
    @Column("idutilisateur")
    private Utilisateur utilisateur;
    @Column("dateheure")
    private LocalDateTime dateheure;
    @Column("etat")
    private Integer etat;
    @ForeignKey(recursive = true)
    @Column("idutilisateur_responsable")
    private Utilisateur utilisateurResponsable;
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Rangee getRangee() {
        return rangee;
    }
    public void setRangee(Rangee rangee) {
        this.rangee = rangee;
    }
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
    public LocalDateTime getDateheure() {
        return dateheure;
    }
    public void setDateheure(LocalDateTime dateheure) {
        this.dateheure = dateheure;
    }
    public Integer getEtat() {
        return etat;
    }
    public void setEtat(Integer etat) {
        this.etat = etat;
    }
    public Utilisateur getUtilisateurResponsable() {
        return utilisateurResponsable;
    }
    public void setUtilisateurResponsable(Utilisateur utilisateurResponsable) {
        this.utilisateurResponsable = utilisateurResponsable;
    }
    
}
