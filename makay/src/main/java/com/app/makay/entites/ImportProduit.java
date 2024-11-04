package com.app.makay.entites;

import java.util.HashMap;

import veda.godao.annotations.Column;
import veda.godao.annotations.PrimaryKey;
import veda.godao.annotations.Table;

@Table("import_produits")
public class ImportProduit {
    @PrimaryKey
    @Column("id")
    private Integer id;
    @Column("nomproduit")
    private String nomProduit;
    @Column("prix")
    private Double prix;
    @Column("nomcategorie")
    private String nomCategorie;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getNomProduit() {
        return nomProduit;
    }
    public void setNomProduit(String nomProduit) {
        this.nomProduit = nomProduit;
    }
    public Double getPrix() {
        return prix;
    }
    public void setPrix(Double prix) {
        this.prix = prix;
    }
    public String getNomCategorie() {
        return nomCategorie;
    }
    public void setNomCategorie(String nomCategorie) {
        this.nomCategorie = nomCategorie;
    }
    public ImportProduit() {
    }
    public ImportProduit(HashMap<String, String> map){
        setNomProduit(map.get("nomproduit"));
        setPrix(Double.parseDouble(map.get("prix")));
        setNomCategorie(map.get("nomcategorie"));
    }
    
}
