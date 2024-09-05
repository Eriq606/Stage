package com.app.makay.entites.REST;

import com.app.makay.entites.Utilisateur;

public class ObjectREST {
    protected Utilisateur utilisateur;
    protected String sessionid;
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
    public String getSessionid() {
        return sessionid;
    }
    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }
    
}
