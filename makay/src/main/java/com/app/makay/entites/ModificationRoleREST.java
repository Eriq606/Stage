package com.app.makay.entites;

public class ModificationRoleREST {
    private Utilisateur utilisateur;
    private HistoriqueRoleUtilisateur[] attributions;
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
    public HistoriqueRoleUtilisateur[] getAttributions() {
        return attributions;
    }
    public void setAttributions(HistoriqueRoleUtilisateur[] attributions) {
        this.attributions = attributions;
    }
    
}
