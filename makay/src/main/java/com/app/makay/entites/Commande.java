package com.app.makay.entites;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import com.app.makay.utilitaire.Constantes;
import com.app.makay.utilitaire.MyDAO;

import handyman.HandyManUtils;
import veda.godao.annotations.Column;
import veda.godao.annotations.ForeignKey;
import veda.godao.annotations.PrimaryKey;
import veda.godao.annotations.Table;

@Table("commandes")
public class Commande {
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
    @Column("dateheure_cloture")
    private LocalDateTime cloture;
    @Column("montant")
    private Double montant;
    @Column("etat")
    private Integer etat;
    private CommandeFille[] commandeFilles;
    @Column("reste_a_payer")
    private Double resteAPayer;
    public Double getResteAPayer() {
        return resteAPayer;
    }
    public void setResteAPayer(Double resteAPayer) {
        this.resteAPayer = resteAPayer;
    }
    public String getPlaceLabel(){
        String label="Place : ";
        switch(getPlace().getTypePlace().getNumero()){
            case Constantes.PLACE_BAR:
                label=Constantes.LABEL_BAR+" : ";
                break;
            case Constantes.PLACE_SALLE:
                label=Constantes.LABEL_SALLE+" : ";
                break;
            case Constantes.PLACE_TERRASSE:
                label=Constantes.LABEL_TERRASSE+" : ";
        }
        label+=getPlace().getNom();
        return label;
    }
    public String getHeure(){
        String reponse=LocalDate.now().isAfter(getOuverture().toLocalDate())?getOuverture().toLocalDate().toString()+" ":"";
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
        reponse+=heureStr+"h"+minuteStr;
        return reponse;
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
    public CommandeFille[] getCommandeFilles() {
        return commandeFilles;
    }
    public void setCommandeFilles(CommandeFille[] commandeFilles) {
        this.commandeFilles = commandeFilles;
    }
    public CommandeFille[] recupererCommandeFilles(Connection connect, MyDAO dao) throws Exception{
        CommandeFille where=new CommandeFille();
        where.setCommande(this);
        where.setEtat(0);
        CommandeFille[] commandeFilles=dao.select(connect, CommandeFille.class, where);
        for(int i=0;i<commandeFilles.length;i++){
            commandeFilles[i].recupererAccompagnements(connect, dao);
        }
        setCommandeFilles(commandeFilles);
        return commandeFilles;
    }
    @Override
    public String toString() {
        return "Commande [id=" + id + ", utilisateur=" + utilisateur + ", place=" + place + ", ouverture=" + ouverture
                + ", cloture=" + cloture + ", montant=" + montant + ", etat=" + etat + ", commandeFilles="
                + Arrays.toString(commandeFilles) + "]";
    }
    public String getResteAPayerString(){
        return HandyManUtils.number_format(getResteAPayer(), ' ', ',', 2)+" Ar";
    }
}
