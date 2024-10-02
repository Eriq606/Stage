package com.app.makay.entites;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedList;

import veda.godao.annotations.Column;
import veda.godao.annotations.ForeignKey;
import veda.godao.annotations.PrimaryKey;
import veda.godao.annotations.Table;

@Table("demande_additions")
public class DemandeAddition {
    @PrimaryKey
    @Column("id")
    private Integer id;
    @ForeignKey(recursive = false)
    @Column("idutilisateur")
    private Utilisateur utilisateur;
    @ForeignKey(recursive = true)
    @Column("idcommande")
    private Commande commande;
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
    public Commande getCommande() {
        return commande;
    }
    public void setCommande(Commande commande) {
        this.commande = commande;
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
    // public DemandeAddition[] recupererAllDemandeAdditions(Connection connect, int offset, int limit) throws SQLException{
    //     String query="select * from v_demande_additions where etat=0 limit ? offset ?";
    //     try(PreparedStatement statement=connect.prepareStatement(query)){
    //         statement.setInt(1, limit);
    //         statement.setInt(2, offset);
    //         try(ResultSet result=statement.executeQuery()){
    //             LinkedList<DemandeAddition> liste=new LinkedList<>();
    //             DemandeAddition demande;
    //             Utilisateur utilisateur;
    //             Commande commande;
    //             while(result.next()){
    //                 demande=new DemandeAddition();
    //                 demande.setId(result.getInt("id"));
    //                 utilisateur=new Utilisateur();
    //                 utilisateur.setId(result.getInt("idutilisateur"));
    //                 utilisateur.setNom(result.getString("nom_utilisateur"));
    //                 utilisateur.setEmail(result.getString("email"));
    //                 utilisateur.setContact(result.getString("contact"));
    //                 demande.setUtilisateur(utilisateur);
    //                 commande=new Commande();
    //                 // commande.set
    //             }
    //         }
    //     }
    // }
}
