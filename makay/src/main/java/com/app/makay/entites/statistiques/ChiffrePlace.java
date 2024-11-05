package com.app.makay.entites.statistiques;

import com.app.makay.entites.Place;

public class ChiffrePlace {
    private Place place;
    private double commandes;
    public Place getPlace() {
        return place;
    }
    public void setPlace(Place place) {
        this.place = place;
    }
    public double getCommandes() {
        return commandes;
    }
    public void setCommandes(double commandes) {
        this.commandes = commandes;
    }
    
}
