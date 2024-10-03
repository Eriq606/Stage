package com.app.makay.entites.REST;

import com.app.makay.entites.Paiement;

public class PayerCommandeREST extends ObjectREST{
    private Paiement paiement;

    public Paiement getPaiement() {
        return paiement;
    }

    public void setPaiement(Paiement paiement) {
        this.paiement = paiement;
    }
    
}
