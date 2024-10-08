package com.app.makay.entites.REST;

import com.app.makay.entites.Produit;

public class ModificationStockREST extends ObjectREST{
    private Produit produit;
    private double quantite;

    public double getQuantite() {
        return quantite;
    }

    public void setQuantite(double quantite) {
        this.quantite = quantite;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }
    
}
