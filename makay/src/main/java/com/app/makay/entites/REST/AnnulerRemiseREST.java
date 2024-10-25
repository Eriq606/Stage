package com.app.makay.entites.REST;

import com.app.makay.entites.annulation.AnnulationRemise;

public class AnnulerRemiseREST extends ObjectREST{
    private AnnulationRemise annulation;

    public AnnulationRemise getAnnulation() {
        return annulation;
    }

    public void setAnnulation(AnnulationRemise annulation) {
        this.annulation = annulation;
    }
    
}
