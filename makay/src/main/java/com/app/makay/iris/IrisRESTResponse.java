package com.app.makay.iris;

import java.util.HashMap;

public class IrisRESTResponse {
    private HashMap<String, Object> donnees;
    private int code;
    private String message;
    public HashMap<String, Object> getDonnees() {
        return donnees;
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
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void addItem(String key, Object value){
        getDonnees().put(key, value);
    }
}
