package com.app.makay.entites.REST;

import com.app.makay.entites.Commande;

public class DemandeAdditionREST extends ObjectREST{
    private Commande commande;

    public Commande getCommande() {
        return commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }
    
}
