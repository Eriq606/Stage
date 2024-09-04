package com.app.makay.entites;

public class ModificationDispatchREST {
    private Utilisateur utilisateur;
    private RangeeUtilisateur[] dispatchs;
    private String sessionid;
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
    public RangeeUtilisateur[] getDispatchs() {
        return dispatchs;
    }
    public void setDispatchs(RangeeUtilisateur[] dispatchs) {
        this.dispatchs = dispatchs;
    }
    public String getSessionid() {
        return sessionid;
    }
    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }
    
}
