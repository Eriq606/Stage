package com.app.makay.entites;

import java.time.LocalDateTime;

public class Relance {
    private Utilisateur utilisateur;
    private Place place;
    private LocalDateTime dateheure;
    private CommandeFille commandeFille;
    public CommandeFille getCommandeFille() {
        return commandeFille;
    }
    public void setCommandeFille(CommandeFille commandeFille) {
        this.commandeFille = commandeFille;
    }
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
    public Place getPlace() {
        return place;
    }
    public void setPlace(Place place) {
        this.place = place;
    }
    public LocalDateTime getDateheure() {
        return dateheure;
    }
    public void setDateheure(LocalDateTime dateheure) {
        this.dateheure = dateheure;
    }
    
}
