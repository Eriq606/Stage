package com.app.makay.entites;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedList;

import com.app.makay.entites.statistiques.ChiffrePlace;
import com.app.makay.utilitaire.Constantes;
import com.app.makay.utilitaire.MyDAO;

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
        if(typePlace!=null){
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
        if(getTypePlace()!=null){
            switch(getTypePlace().getNumero()){
                case Constantes.PLACE_BAR:
                    return Constantes.CLASSE_BAR;
                case Constantes.PLACE_SALLE:
                    return Constantes.CLASSE_SALLE;
                case Constantes.PLACE_TERRASSE:
                    return Constantes.CLASSE_TERRASSE;
            }
        }
        return null;
    }
    public Place() {
    }
    public Place(Integer etat) {
        this.etat = etat;
    }
    public static ChiffrePlace[] chiffrePlacesVisitees(Connection connect, MyDAO dao, LocalDateTime dateDebut, LocalDateTime dateFin) throws Exception{
        String query="""
            select idplace, count(*) as nbcommandes from commandes where etat=? and dateheure_ouverture>=? and dateheure_ouverture<? group by idplace order by nbcommandes desc
        """;
        LinkedList<ChiffrePlace> liste=new LinkedList<>();
        ChiffrePlace chiffre;
        Place where=new Place(), place;
        try(PreparedStatement statement=connect.prepareStatement(query)){
            statement.setInt(1, Constantes.COMMANDE_PAYEE);
            statement.setTimestamp(2, Timestamp.valueOf(dateDebut));
            statement.setTimestamp(3, Timestamp.valueOf(dateFin));
            try(ResultSet result=statement.executeQuery()){
                while(result.next()){
                    where.setId(result.getInt("idplace"));
                    place=dao.select(connect, Place.class, where)[0];
                    chiffre=new ChiffrePlace();
                    chiffre.setPlace(place);
                    chiffre.setCommandes(result.getDouble("nbcommandes"));
                    liste.add(chiffre);
                }
            }
        }
        ChiffrePlace[] chiffres=liste.toArray(new ChiffrePlace[liste.size()]);
        return chiffres;
    }
    public static ChiffrePlace[] chiffrePlacesNonVisitees(Connection connect, MyDAO dao, LocalDateTime dateDebut, LocalDateTime dateFin) throws Exception{
        String query="""
            select idplace, count(*) as nbcommandes from commandes where etat=? and dateheure_ouverture>=? and dateheure_ouverture<? group by idplace order by nbcommandes
        """;
        LinkedList<ChiffrePlace> liste=new LinkedList<>();
        ChiffrePlace chiffre;
        Place where=new Place(), place;
        try(PreparedStatement statement=connect.prepareStatement(query)){
            statement.setInt(1, Constantes.COMMANDE_PAYEE);
            statement.setTimestamp(2, Timestamp.valueOf(dateDebut));
            statement.setTimestamp(3, Timestamp.valueOf(dateFin));
            try(ResultSet result=statement.executeQuery()){
                while(result.next()){
                    where.setId(result.getInt("idplace"));
                    place=dao.select(connect, Place.class, where)[0];
                    chiffre=new ChiffrePlace();
                    chiffre.setPlace(place);
                    chiffre.setCommandes(result.getDouble("nbcommandes"));
                    liste.add(chiffre);
                }
            }
        }
        ChiffrePlace[] chiffres=liste.toArray(new ChiffrePlace[liste.size()]);
        return chiffres;
    }
}
