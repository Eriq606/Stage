package com.app.makay.entites;

import com.app.makay.utilitaire.Constantes;

import veda.godao.annotations.Column;
import veda.godao.annotations.ForeignKey;
import veda.godao.annotations.PrimaryKey;
import veda.godao.annotations.Table;

@Table("places")
public class Place {
    @Override
    public String toString() {
        return "Place [id=" + id + ", nom=" + nom + ", etat=" + etat + ", typePlace=" + typePlace + ", classeHTML="
                + classeHTML + "]";
    }
    @PrimaryKey
    @Column("id")
    private Integer id;
    @Column("nom")
    private String nom;
    @Column("etat")
    private Integer etat;
    @ForeignKey(recursive = true)
    @Column("idtypeplace")
    private TypePlace typePlace;
    private String classeHTML;
    public String getClasseHTML() {
        return classeHTML;
    }
    public void setClasseHTML(String classeHTML) {
        this.classeHTML = classeHTML;
    }
    public TypePlace getTypePlace() {
        return typePlace;
    }
    public void setTypePlace(TypePlace typePlace) {
        this.typePlace = typePlace;
        switch(getTypePlace().getNumero()){
            case Constantes.PLACE_BAR:
                setClasseHTML(Constantes.CLASSE_BAR);
                break;
            case Constantes.PLACE_SALLE:
                setClasseHTML(Constantes.CLASSE_SALLE);
                break;
            case Constantes.PLACE_TERRASSE:
                setClasseHTML(Constantes.CLASSE_TERRASSE);
        }
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public Integer getEtat() {
        return etat;
    }
    public void setEtat(Integer etat) {
        this.etat = etat;
    }
    public String getClasse(){
        switch(getTypePlace().getNumero()){
            case Constantes.PLACE_BAR:
                return Constantes.CLASSE_BAR;
            case Constantes.PLACE_SALLE:
                return Constantes.CLASSE_SALLE;
            case Constantes.PLACE_TERRASSE:
                return Constantes.CLASSE_TERRASSE;
        }
        return null;
    }
    public Place() {
    }
    public Place(Integer etat) {
        this.etat = etat;
    }
    
}
