package com.app.makay.entites.temporary;

import veda.godao.annotations.Column;
import veda.godao.annotations.PrimaryKey;
import veda.godao.annotations.Table;

@Table("table_fictive_stock")
public class ProduitCommandeStock {
    @PrimaryKey
    @Column("id")
    private Integer id;
    @Column("idproduit")
    private Integer idproduit;
    @Column("quantite")
    private Double quantite;
    @Column("nom_produit")
    private String nomProduit;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getIdproduit() {
        return idproduit;
    }
    public void setIdproduit(Integer idproduit) {
        this.idproduit = idproduit;
    }
    public Double getQuantite() {
        return quantite;
    }
    public void setQuantite(Double quantite) {
        this.quantite = quantite;
    }
    public String getNomProduit() {
        return nomProduit;
    }
    public void setNomProduit(String nomProduit) {
        this.nomProduit = nomProduit;
    }
    public ProduitCommandeStock(Integer idproduit, Double quantite, String nomProduit) {
        this.idproduit = idproduit;
        this.quantite = quantite;
        this.nomProduit = nomProduit;
    }
    
}
