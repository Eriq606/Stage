package com.app.makay.entites;

import veda.godao.annotations.Column;
import veda.godao.annotations.ForeignKey;
import veda.godao.annotations.PrimaryKey;
import veda.godao.annotations.Table;
import java.time.LocalDateTime;

@Table("historique_role_utilisateurs")
public class HistoriqueRoleUtilisateur {
    @PrimaryKey
    @Column("id")
    private Integer id;
    @ForeignKey(recursive = true)
    @Column("idutilisateur")
    private Utilisateur utilisateur;
    @ForeignKey(recursive = true)
    @Column("idrole")
    private Role role;
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
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
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
