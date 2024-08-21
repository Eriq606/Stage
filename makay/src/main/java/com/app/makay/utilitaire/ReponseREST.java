package com.app.makay.utilitaire;

import java.util.LinkedList;

public class ReponseREST {
    private LinkedList<Object> donnees;
    private String code;
    private String message;
    
    public ReponseREST() {
        setDonnees(new LinkedList<Object>());
    }
    public LinkedList<Object> getDonnees() {
        return donnees;
    }
    public void setDonnees(LinkedList<Object> donnees) {
        this.donnees = donnees;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    
}
