package com.app.makay.entites;

import java.sql.Connection;
import java.time.LocalDateTime;

import com.app.makay.utilitaire.MyDAO;

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
    @Column("dateheure_fermeture")
    private LocalDateTime cloture;
    @Column("montant")
    private Double montant;
    @Column("etat")
    private Integer etat;
    private CommandeFille[] commandeFilles;
    
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
        CommandeFille[] commandeFilles=dao.select(connect, CommandeFille.class, where);
        setCommandeFilles(commandeFilles);
        return commandeFilles;
    }
}
