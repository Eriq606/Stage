package com.app.makay.entites;

public class ModificationRoleREST {
    private Utilisateur utilisateur;
    private HistoriqueRoleUtilisateur[] attributions;
    private String sessionid;
    
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
    public String getSessionid() {
        return sessionid;
    }
    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }
    
}
