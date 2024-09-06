package com.app.makay.entites;

import veda.godao.annotations.Column;
import veda.godao.annotations.ForeignKey;
import veda.godao.annotations.PrimaryKey;
import veda.godao.annotations.Table;

@Table("v_commande_filles")
public class CommandeFilleEnCours {
    @PrimaryKey
    @Column("id")
    private Integer id;
    @ForeignKey(recursive = true)
    @Column("idcommande")
    private CommandeEnCours commande;
    @ForeignKey(recursive = true)
    @Column("idproduit")
    private Produit produit;
    @Column("prix_unitaire")
    private Double pu;
    @Column("quantite")
    private Double quantite;
    @Column("montant")
    private Double montant;
    @Column("notes")
    private String notes;
    @Column("etat")
    private Integer etat;
    private Accompagnement[] accompagnements;
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public CommandeEnCours getCommande() {
        return commande;
    }
    public void setCommande(CommandeEnCours commande) {
        this.commande = commande;
    }
    public Produit getProduit() {
        return produit;
    }
    public void setProduit(Produit produit) {
        this.produit = produit;
    }
    
    public Double getQuantite() {
        return quantite;
    }
    public void setQuantite(Double quantite) {
        this.quantite = quantite;
    }
    public Double getMontant() {
        return montant;
    }
    public void setMontant(Double montant) {
        this.montant = montant;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public Integer getEtat() {
        return etat;
    }
    public void setEtat(Integer etat) {
        this.etat = etat;
    }
    public Double getPu() {
        return pu;
    }
    public void setPu(Double pu) {
        this.pu = pu;
    }
    public Accompagnement[] getAccompagnements() {
        return accompagnements;
    }
    public void setAccompagnements(Accompagnement[] accompagnements) {
        this.accompagnements = accompagnements;
    }
    
}
