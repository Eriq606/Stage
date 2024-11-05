package com.app.makay.entites.statistiques;

public class ChiffreSemaine {
    private String etiquette;
    private Double[] donnees;
    private String couleur;
    public String getEtiquette() {
        return etiquette;
    }
    public void setEtiquette(String etiquette) {
        this.etiquette = etiquette;
    }
    public String getCouleur() {
        return couleur;
    }
    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }
    public Double[] getDonnees() {
        return donnees;
    }
    public void setDonnees(Double[] donnees) {
        this.donnees = donnees;
    }
    
}
