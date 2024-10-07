package com.app.makay.entites;

import veda.godao.annotations.Column;
import veda.godao.annotations.PrimaryKey;
import veda.godao.annotations.Table;

@Table("accompagnements")
public class Accompagnement {
    @PrimaryKey
    @Column("id")
    private Integer id;
    @Column("nom")
    private String nom;
    @Column("etat")
    private Integer etat;
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
    @Override
    public String toString() {
        return "Accompagnement [id=" + id + ", nom=" + nom + ", etat=" + etat + "]";
    }
    public String getHtml(){
        String html="<li>%s</li>";
        return String.format(html, getNom());
    }
}
