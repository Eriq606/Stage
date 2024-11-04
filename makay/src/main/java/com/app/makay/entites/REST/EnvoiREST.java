package com.app.makay.entites.REST;

import java.util.HashMap;

public class EnvoiREST extends ObjectREST{
    private HashMap<String, String> donnees;
    public HashMap<String, String> getDonnees() {
        return donnees;
    }
    public void setDonnees(HashMap<String, String> donnees) {
        this.donnees = donnees;
    }
    @Override
    public String toString() {
        return "EnvoiREST [utilisateur=" + utilisateur + ", donnees=" + donnees + ", sessionid=" + sessionid + "]";
    }
    public EnvoiREST() {
        setDonnees(new HashMap<>());
    }
    
    
}
