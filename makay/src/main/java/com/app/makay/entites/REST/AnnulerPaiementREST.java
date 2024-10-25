package com.app.makay.entites.REST;

import com.app.makay.entites.annulation.AnnulationPaiement;

public class AnnulerPaiementREST extends ObjectREST{
    private AnnulationPaiement annulation;

    public AnnulationPaiement getAnnulation() {
        return annulation;
    }

    public void setAnnulation(AnnulationPaiement annulation) {
        this.annulation = annulation;
    }
    
}
