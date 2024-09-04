package com.app.makay.entites;

import java.sql.Connection;
import java.util.HashMap;

import com.app.makay.utilitaire.MyDAO;

import veda.godao.annotations.Column;
import veda.godao.annotations.PrimaryKey;
import veda.godao.annotations.Table;

@Table("rangees")
public class Rangee {
    @PrimaryKey
    @Column("id")
    private Integer id;
    @Column("nom")
    private String nom;
    @Column("etat")
    private Integer etat;
    private Utilisateur[] utilisateurs;
    private Place[] places;
    
    
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
    public Rangee() {
    }
    public Rangee(Integer etat) {
        this.etat = etat;
    }
    public Utilisateur[] getUtilisateurs() {
        return utilisateurs;
    }
    public void setUtilisateurs(Utilisateur[] utilisateurs) {
        this.utilisateurs = utilisateurs;
    }
    public Place[] getPlaces() {
        return places;
    }
    public void setPlaces(Place[] places) {
        this.places = places;
    }
    public Utilisateur[] getDispatchUtilisateursActuel(Connection connect, MyDAO dao) throws Exception{
        String query="select idutilisateur, nom_utilisateur, email_utilisateur, contact_utilisateur from v_dispatch_staff where idrangee="+getId();
        HashMap<String, Object>[] objets=dao.select(connect, query);
        Utilisateur[] utilisateurs=new Utilisateur[objets.length];
        for(int i=0;i<utilisateurs.length;i++){
            utilisateurs[i]=new Utilisateur();
            utilisateurs[i].setId((int)objets[i].get("idutilisateur"));
            utilisateurs[i].setNom((String)objets[i].get("nom_utilisateur"));
            utilisateurs[i].setEmail((String)objets[i].get("email_utilisateur"));
            utilisateurs[i].setContact((String)objets[i].get("contact_utilisateur"));
        }
        setUtilisateurs(utilisateurs);
        return utilisateurs;
    }
    public Place[] recupererPlaces(Connection connect, MyDAO dao) throws Exception{
        String query="select idplace, nom_place, nom_type_place, numero_type_place from v_arrangement_place where idrangee="+getId();
        HashMap<String, Object>[] objets=dao.select(connect, query);
        Place[] places=new Place[objets.length];
        TypePlace type;
        for(int i=0;i<places.length;i++){
            places[i]=new Place();
            places[i].setId((int)objets[i].get("idplace"));
            places[i].setNom((String)objets[i].get("nom_place"));
            type=new TypePlace();
            type.setNom((String)objets[i].get("nom_type_place"));
            type.setNumero((String)objets[i].get("numero_type_place"));
            places[i].setTypePlace(type);
        }
        setPlaces(places);
        return places;
    }
}
