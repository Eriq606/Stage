package com.app.makay.entites.statistiques;

public class ChiffrePaiement {
    private String[] couleurs;
    private Double[] donnees;
    private String[] etiquettes;
    public String[] getEtiquettes() {
        return etiquettes;
    }
    public void setEtiquettes(String[] etiquettes) {
        this.etiquettes = etiquettes;
    }
    public String[] getCouleurs() {
        return couleurs;
    }
    public void setCouleurs(String[] couleurs) {
        this.couleurs = couleurs;
    }
    public Double[] getDonnees() {
        return donnees;
    }
    public void setDonnees(Double[] donnees) {
        this.donnees = donnees;
    }
    
}
