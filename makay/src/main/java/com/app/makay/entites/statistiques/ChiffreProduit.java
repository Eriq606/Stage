package com.app.makay.entites.statistiques;

import com.app.makay.entites.Produit;

public class ChiffreProduit {
    private Produit produit;
    private double quantite;
    public Produit getProduit() {
        return produit;
    }
    public void setProduit(Produit produit) {
        this.produit = produit;
    }
    public double getQuantite() {
        return quantite;
    }
    public void setQuantite(double quantite) {
        this.quantite = quantite;
    }
    
}
