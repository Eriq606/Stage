package com.app.makay.entites;

import java.sql.Connection;
import java.time.LocalDateTime;

import com.app.makay.utilitaire.Constantes;
import com.app.makay.utilitaire.MyDAO;

import veda.godao.annotations.Column;
import veda.godao.annotations.ForeignKey;
import veda.godao.annotations.PrimaryKey;
import veda.godao.annotations.Table;

@Table("v_commandes")
public class CommandeEnCours{
    @PrimaryKey
    @Column("id")
    private Integer id;
    @ForeignKey(recursive = false)
    @Column("idutilisateur")
    private Utilisateur utilisateur;
    @ForeignKey(recursive = true)
    @Column("idplace")
    private Place place;
    @Column("dateheure_ouverture")
    private LocalDateTime ouverture;
    @Column("dateheure_fermeture")
    private LocalDateTime cloture;
    @Column("montant")
    private Double montant;
    @Column("etat")
    private Integer etat;
    private CommandeFilleEnCours[] commandeFilles;
    public String getPlaceLabel(){
        switch(getPlace().getTypePlace().getNumero()){
            case Constantes.PLACE_BAR:
                return Constantes.LABEL_BAR;
            case Constantes.PLACE_SALLE:
                return Constantes.LABEL_SALLE;
            case Constantes.PLACE_TERRASSE:
                return Constantes.LABEL_TERRASSE;
        }
        return "Place";
    }
    public String getHeure(){
        int heure=getOuverture().getHour();
        String heureStr=String.valueOf(heure);
        if(heure<10){
            heureStr="0"+heureStr;
        }
        int minute=getOuverture().getMinute();
        String minuteStr=String.valueOf(minute);
        if(minute<10){
            minuteStr="0"+minuteStr;
        }
        return heureStr+"h"+minuteStr;
    }
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
    public Place getPlace() {
        return place;
    }
    public void setPlace(Place place) {
        this.place = place;
    }
    public LocalDateTime getOuverture() {
        return ouverture;
    }
    public void setOuverture(LocalDateTime ouverture) {
        this.ouverture = ouverture;
    }
    public LocalDateTime getCloture() {
        return cloture;
    }
    public void setCloture(LocalDateTime cloture) {
        this.cloture = cloture;
    }
    public Double getMontant() {
        return montant;
    }
    public void setMontant(Double montant) {
        this.montant = montant;
    }
    public Integer getEtat() {
        return etat;
    }
    public void setEtat(Integer etat) {
        this.etat = etat;
    }
    
    public CommandeFilleEnCours[] recupererCommandeFilles(Connection connect, MyDAO dao) throws Exception{
        CommandeFilleEnCours where=new CommandeFilleEnCours();
        where.setCommande(this);
        CommandeFilleEnCours[] commandeFilles=dao.select(connect, CommandeFilleEnCours.class, where);
        setCommandeFilles(commandeFilles);
        return commandeFilles;
    }
    public CommandeFilleEnCours[] getCommandeFilles() {
        return commandeFilles;
    }
    public void setCommandeFilles(CommandeFilleEnCours[] commandeFilles) {
        this.commandeFilles = commandeFilles;
    }
}
