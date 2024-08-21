package com.app.makay.entites;

import veda.godao.annotations.Column;
import veda.godao.annotations.PrimaryKey;
import veda.godao.annotations.Table;

@Table("roles")
public class Role {
    @PrimaryKey
    @Column("id")
    private Integer id;
    @Column("numero")
    private String numero;
    @Column("nom")
    private String nom;
    @Column("autorisation")
    private Integer autorisation;
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
    public Integer getAutorisation() {
        return autorisation;
    }
    public void setAutorisation(Integer autorisation) {
        this.autorisation = autorisation;
    }
    public Integer getEtat() {
        return etat;
    }
    public void setEtat(Integer etat) {
        this.etat = etat;
    }
    public String getNumero() {
        return numero;
    }
    public void setNumero(String numero) {
        this.numero = numero;
    }
    
}
