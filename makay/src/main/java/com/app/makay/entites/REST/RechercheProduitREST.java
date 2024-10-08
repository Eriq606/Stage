package com.app.makay.entites.REST;

import com.app.makay.entites.Produit;

public class RechercheProduitREST extends ObjectREST{
    private Produit produit;

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }
    
}
