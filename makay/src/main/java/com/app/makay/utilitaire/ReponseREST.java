package com.app.makay.utilitaire;

import java.util.HashMap;

public class ReponseREST {
    private HashMap<String, Object> donnees;
    private int code;
    private String message;
    
    public ReponseREST() {
        donnees=new HashMap<>();
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public HashMap<String, Object> getDonnees() {
        return donnees;
    }
    public void addItem(String key, String value){
        getDonnees().put(key, value);
    }
    public void setDonnees(HashMap<String, Object> donnees) {
        this.donnees = donnees;
    }
    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    
}
