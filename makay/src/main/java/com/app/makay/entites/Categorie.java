package com.app.makay.entites;

import java.sql.Connection;

import com.app.makay.utilitaire.MyDAO;

import veda.godao.annotations.Column;
import veda.godao.annotations.PrimaryKey;
import veda.godao.annotations.Table;

@Table("categories")
public class Categorie {
    @PrimaryKey
    @Column("id")
    private Integer id;
    @Column("nom")
    private String nom;
    @Column("etat")
    private Integer etat;
    private Role[] roles;
    public Role[] getRoles() {
        return roles;
    }
    public void setRoles(Role[] roles) {
        this.roles = roles;
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
    public Role[] recupererRoles(Connection connect, MyDAO dao) throws Exception{
        String query="where id in (select idrole from role_categorie_produits_checkings where idcategorie=%s) and etat=0";
        query=String.format(query, getId());
        Role[] roles=dao.select(connect, Role.class, query);
        setRoles(roles);
        return roles;
    }
    public static void insertImportCategorie(Connection connect, MyDAO dao) throws Exception{
        String query="insert into categories(nom) select nomcategorie from v_import_produit_categories where idcategorie is null group by nomcategorie";
        dao.execute(connect, query);
    }
    public static int recupererPremierID(Connection connect, MyDAO dao) throws Exception{
        String query="select id from categories limit 1";
        int randomId=dao.count(connect, query);
        return randomId;
    }
}
