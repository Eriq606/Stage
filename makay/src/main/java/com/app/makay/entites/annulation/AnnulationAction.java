package com.app.makay.entites.annulation;

import java.time.LocalDateTime;

import com.app.makay.entites.ActionSuperviseur;
import com.app.makay.entites.Utilisateur;

import veda.godao.annotations.Column;
import veda.godao.annotations.ForeignKey;
import veda.godao.annotations.PrimaryKey;
import veda.godao.annotations.Table;

@Table("annulation_actions")
public class AnnulationAction {
    @PrimaryKey
    @Column("id")
    private Integer id;
    @ForeignKey(recursive = true)
    @Column("idaction")
    private ActionSuperviseur action;
    @ForeignKey(recursive = false)
    @Column("idutilisateur")
    private Utilisateur utilisateur;
    @Column("dateheure")
    private LocalDateTime dateheure;
    @Column("etat")
    private Integer etat;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public ActionSuperviseur getAction() {
        return action;
    }
    public void setAction(ActionSuperviseur action) {
        this.action = action;
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
    
}
