package com.app.makay.entites;

import veda.godao.annotations.Column;
import veda.godao.annotations.ForeignKey;
import veda.godao.annotations.PrimaryKey;
import veda.godao.annotations.Table;

@Table("accompagnement_commandes")
public class AccompagnementCommande {
    @PrimaryKey
    @Column("id")
    private Integer id;
    @ForeignKey(recursive = true)
    @Column("idcommandefille")
    private CommandeFille commandeFille;
    @ForeignKey(recursive = true)
    @Column("idaccompagnement")
    private Accompagnement accompagnement;
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
    public Accompagnement getAccompagnement() {
        return accompagnement;
    }
    public void setAccompagnement(Accompagnement accompagnement) {
        this.accompagnement = accompagnement;
    }
    public Integer getEtat() {
        return etat;
    }
    public void setEtat(Integer etat) {
        this.etat = etat;
    }
    
}