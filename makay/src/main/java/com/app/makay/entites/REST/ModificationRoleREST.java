package com.app.makay.entites.REST;

import com.app.makay.entites.HistoriqueRoleUtilisateur;

public class ModificationRoleREST extends ObjectREST{
    private HistoriqueRoleUtilisateur[] attributions;

    public HistoriqueRoleUtilisateur[] getAttributions() {
        return attributions;
    }

    public void setAttributions(HistoriqueRoleUtilisateur[] attributions) {
        this.attributions = attributions;
    }
    
}
