package com.app.makay.entites.REST;

import com.app.makay.entites.liaison.RangeeUtilisateur;

public class ModificationDispatchREST extends ObjectREST{
    private RangeeUtilisateur[] dispatchs;

    public RangeeUtilisateur[] getDispatchs() {
        return dispatchs;
    }

    public void setDispatchs(RangeeUtilisateur[] dispatchs) {
        this.dispatchs = dispatchs;
    }
    
}
