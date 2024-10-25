package com.app.makay.entites.liaison;

import com.app.makay.entites.Accompagnement;
import com.app.makay.entites.Produit;

import veda.godao.annotations.Column;
import veda.godao.annotations.ForeignKey;
import veda.godao.annotations.PrimaryKey;
import veda.godao.annotations.Table;

@Table("accompagnement_produits")
public class AccompagnementProduit {
    @PrimaryKey
    @Column("id")
    private Integer id;
    @ForeignKey(recursive = true)
    @Column("idproduit")
    private Produit produit;
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
    public Produit getProduit() {
        return produit;
    }
    public void setProduit(Produit produit) {
        this.produit = produit;
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
