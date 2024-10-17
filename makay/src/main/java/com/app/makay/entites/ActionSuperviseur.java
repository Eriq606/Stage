package com.app.makay.entites;

import java.time.LocalDateTime;

import veda.godao.annotations.Column;
import veda.godao.annotations.ForeignKey;
import veda.godao.annotations.PrimaryKey;
import veda.godao.annotations.Table;

@Table("action_superviseurs")
public class ActionSuperviseur {
    @PrimaryKey
    @Column("id")
    private Integer id;
    @ForeignKey(recursive = true)
    @Column("idcommandefille")
    private CommandeFille commandeFille;
    @Column("quantite")
    private Double quantite;
    @Column("dateheure")
    private LocalDateTime dateheure;
    @Column("action")
    private Integer action;
    @Column("etat")
    private Integer etat;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
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
    public LocalDateTime getDateheure() {
        return dateheure;
    }
    public void setDateheure(LocalDateTime dateheure) {
        this.dateheure = dateheure;
    }
    public Integer getAction() {
        return action;
    }
    public void setAction(Integer action) {
        this.action = action;
    }
    public Integer getEtat() {
        return etat;
    }
    public void setEtat(Integer etat) {
        this.etat = etat;
    }
    
}
