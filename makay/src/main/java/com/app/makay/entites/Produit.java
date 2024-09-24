package com.app.makay.entites;

import java.sql.Connection;
import java.util.HashMap;

import com.app.makay.utilitaire.MyDAO;

import veda.godao.annotations.Column;
import veda.godao.annotations.ForeignKey;
import veda.godao.annotations.PrimaryKey;
import veda.godao.annotations.Table;

@Table("produits")
public class Produit {
    @PrimaryKey
    @Column("id")
    private Integer id;
    @Column("nom")
    private String nom;
    @Column("prix")
    private Double prix;
    @Column("etat")
    private Integer etat;
    @ForeignKey(recursive = true)
    @Column("idcategorie")
    private Categorie categorie;
    private Accompagnement[] accompagnements;
    
    public Produit(Integer etat) {
        this.etat = etat;
    }
    public Produit() {
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
    public Double getPrix() {
        return prix;
    }
    public void setPrix(Double prix) {
        this.prix = prix;
    }
    public Integer getEtat() {
        return etat;
    }
    public void setEtat(Integer etat) {
        this.etat = etat;
    }
    public Categorie getCategorie() {
        return categorie;
    }
    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }
    public Accompagnement[] getAccompagnements() {
        return accompagnements;
    }
    public void setAccompagnements(Accompagnement[] accompagnements) {
        this.accompagnements = accompagnements;
    }
    
    public Accompagnement[] getAllAccompagnements(Connection connect, MyDAO dao) throws Exception{
        AccompagnementProduit where=new AccompagnementProduit();
        where.setProduit(this);
        AccompagnementProduit[] accompagnementProduits=dao.select(connect, AccompagnementProduit.class, where);
        String selectAccompagnements="select * from accompagnements where id in(";
        for(AccompagnementProduit a:accompagnementProduits){
            selectAccompagnements+=a.getId()+",";
        }
        selectAccompagnements=selectAccompagnements.substring(0, selectAccompagnements.length()-1);
        selectAccompagnements+=") and etat=0";
        if(accompagnementProduits.length==0){
            return new Accompagnement[0];
        }
        HashMap<String, Object>[] query=dao.select(connect, selectAccompagnements);
        Accompagnement[] accompagnements=new Accompagnement[query.length];
        for(int i=0;i<accompagnements.length;i++){
            accompagnements[i]=new Accompagnement();
            accompagnements[i].setId((int)query[i].get("id"));
            accompagnements[i].setNom(String.valueOf(query[i].get("nom")));
            accompagnements[i].setEtat((int)query[i].get("etat"));
        }
        return accompagnements;
    }
}
