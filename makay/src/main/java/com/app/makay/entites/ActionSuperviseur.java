package com.app.makay.entites;

import java.time.LocalDateTime;

import com.app.makay.utilitaire.Constantes;

import handyman.HandyManUtils;
import veda.godao.annotations.Column;
import veda.godao.annotations.ForeignKey;
import veda.godao.annotations.PrimaryKey;
import veda.godao.annotations.Table;

@Table("action_superviseurs")
public class ActionSuperviseur {
    @PrimaryKey
    @Column("id")
    private Integer id;
    @ForeignKey(recursive = true)
    @Column("idcommandefille")
    private CommandeFille commandeFille;
    @Column("quantite")
    private Double quantite;
    @Column("dateheure")
    private LocalDateTime dateheure;
    @Column("action")
    private Integer action;
    @Column("etat")
    private Integer etat;
    @Column("montant")
    private Double montant;
    @ForeignKey(recursive = true)
    @Column("idutilisateur")
    private Utilisateur utilisateur;
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
    public Double getMontant() {
        return montant;
    }
    public void setMontant(Double montant) {
        this.montant = montant;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public CommandeFille getCommandeFille() {
        return commandeFille;
    }
    public void setCommandeFille(CommandeFille commandeFille) {
        this.commandeFille = commandeFille;
    }
    public Double getQuantite() {
        return quantite;
    }
    public void setQuantite(Double quantite) {
        this.quantite = quantite;
    }
    public LocalDateTime getDateheure() {
        return dateheure;
    }
    public void setDateheure(LocalDateTime dateheure) {
        this.dateheure = dateheure;
    }
    public Integer getAction() {
        return action;
    }
    public void setAction(Integer action) {
        this.action = action;
    }
    public Integer getEtat() {
        return etat;
    }
    public void setEtat(Integer etat) {
        this.etat = etat;
    }
    public String recupererDateHeureString(){
        String date=getDateheure().toLocalDate().toString();
        int heure=getDateheure().toLocalTime().getHour();
        int minute=getDateheure().toLocalTime().getMinute();
        int secondes=getDateheure().toLocalTime().getSecond();
        return date+" "+heure+":"+minute+":"+secondes;
    }
    public String recupererCommandeLabel(){
        if(getCommandeFille().getAccompagnements().length==0){
            return getCommandeFille().getProduit().getNom()+" "+getCommandeFille().getNotes();
        }
        String accompagnements="(";
        for(Accompagnement a:getCommandeFille().getAccompagnements()){
            accompagnements+=a.getNom()+",";
        }
        accompagnements=accompagnements.substring(0, accompagnements.length()-1)+")";
        return getCommandeFille().getProduit().getNom()+" "+getCommandeFille().getNotes()+accompagnements;
    }
    public String recupererMontantString(){
        double montant=getQuantite()*getCommandeFille().getPu();
        String montantString=HandyManUtils.number_format(montant, ' ', ',', 2)+" Ar";
        return montantString;
    }
    public String recupererActionString(){
        switch(getAction()){
            case Constantes.COMMANDEFILLE_OFFERT:
                return Constantes.COMMANDEFILLE_OFFERT_LABEL;
            case Constantes.COMMANDEFILLE_ANNULEE:
                return Constantes.COMMANDEFILLE_ANNULEE_LABEL;
        }
        return null;
    }
    public String recupererCouleurString(){
        switch(getAction()){
            case Constantes.COMMANDEFILLE_OFFERT:
                return Constantes.COULEUR_SUCCES;
            case Constantes.COMMANDEFILLE_ANNULEE:
                return Constantes.COULEUR_CANCELED;
        }
        return null;
    }
    public String recupererHeureStringDocument(){
        String date=getDateheure().toLocalDate().toString();
        String heure=getDateheure().toLocalTime().minusNanos(getDateheure().toLocalTime().getNano()).toString();
        return date+" "+heure;
    }
    public String recupererCommandeDocument(){
        return getQuantite()+" "+getCommandeFille().getProduit().getNom()+" "+getCommandeFille().getNotes();
    }
    public String toHtml(){
        String html="""
        <tr class="details-travaux">
            <td style="padding: 10px; border: 1px solid;">%s</td>
            <td style="padding: 10px; border: 1px solid;">%s</td>
            <td style="padding: 10px; border: 1px solid;">%s</td>
            <td style="padding: 10px; border: 1px solid;">%s</td>
            <td style="padding: 10px; border: 1px solid;text-align:right">%s</td>
        </tr>
        """;
        html=String.format(html, recupererHeureStringDocument(), recupererCommandeDocument(), getUtilisateur().getNom(), recupererActionString(), recupererMontantString());
        return html;
    }
}
