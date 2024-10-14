package com.app.makay.entites.temporary;

import veda.godao.annotations.Column;

public class ProduitCommandeStockForSelect {
    @Column("idproduit")
    private Integer idproduit;
    @Column("quantite")
    private Double quantite;
    @Column("nom_produit")
    private String nomProduit;
    @Column("dernier_stock")
    private Double stock;
    public Double getStock() {
        return stock;
    }
    public void setStock(Double stock) {
        this.stock = stock;
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
    
}
