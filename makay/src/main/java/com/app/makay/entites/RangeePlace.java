package com.app.makay.entites;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;

import com.app.makay.utilitaire.MyDAO;

import veda.godao.annotations.Column;
import veda.godao.annotations.ForeignKey;
import veda.godao.annotations.PrimaryKey;
import veda.godao.annotations.Table;

@Table("rangee_places")
public class RangeePlace {
    @PrimaryKey
    @Column("id")
    private Integer id;
    @ForeignKey(recursive = true)
    @Column("idrangee")
    private Rangee rangee;
    @ForeignKey(recursive = true)
    @Column("idplace")
    private Place place;
    @Column("dateheure")
    private LocalDateTime dateheure;
    @Column("etat")
    private Integer etat;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Rangee getRangee() {
        return rangee;
    }
    public void setRangee(Rangee rangee) {
        this.rangee = rangee;
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
    public Integer getEtat() {
        return etat;
    }
    public void setEtat(Integer etat) {
        this.etat = etat;
    }
    
    public static RangeePlace[] getArrangementActuel(Connection connect, MyDAO dao) throws Exception{
        String query="select id, idrangee, idplace, nom_rangee, nom_place from v_arrangement_place";
        HashMap<String, Object>[] objets=dao.select(connect, query);
        RangeePlace[] arrangements=new RangeePlace[objets.length];
        Place place;
        Rangee rangee;
        for(int i=0;i<arrangements.length;i++){
            arrangements[i]=new RangeePlace();
            arrangements[i].setId((int)objets[i].get("id"));
            rangee=new Rangee();
            rangee.setId((int)objets[i].get("idrangee"));
            rangee.setNom((String)objets[i].get("nom_rangee"));
            arrangements[i].setRangee(rangee);
            place=new Place();
            place.setId((int)objets[i].get("idplace"));
            place.setNom((String)objets[i].get("nom_place"));
            arrangements[i].setPlace(place);
        }
        return arrangements;
    }
}
