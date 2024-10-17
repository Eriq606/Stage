package com.app.makay.entites;

import java.sql.Connection;
import java.util.Arrays;

import com.app.makay.utilitaire.Constantes;
import com.app.makay.utilitaire.MyDAO;

import handyman.HandyManUtils;
import veda.godao.annotations.Column;
import veda.godao.annotations.ForeignKey;
import veda.godao.annotations.PrimaryKey;
import veda.godao.annotations.Table;

@Table("commande_filles")
public class CommandeFille {
    @PrimaryKey
    @Column("id")
    private Integer id;
    @ForeignKey(recursive = true)
    @Column("idcommande")
    private Commande commande;
    @ForeignKey(recursive = true)
    @Column("idproduit")
    private Produit produit;
    @Column("prix_unitaire")
    private Double pu;
    @Column("quantite")
    private Double quantite;
    @Column("montant")
    private Double montant;
    @Column("notes")
    private String notes;
    @Column("etat")
    private Integer etat;
    private Accompagnement[] accompagnements;
    @Column("quantite_restante")
    private Double quantiteRestante;
    
    public Double getQuantiteRestante() {
        return quantiteRestante;
    }
    public void setQuantiteRestante(Double quantiteRestante) {
        this.quantiteRestante = quantiteRestante;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Commande getCommande() {
        return commande;
    }
    public void setCommande(Commande commande) {
        this.commande = commande;
    }
    public Produit getProduit() {
        return produit;
    }
    public void setProduit(Produit produit) {
        this.produit = produit;
    }
    
    public Double getQuantite() {
        return quantite;
    }
    public void setQuantite(Double quantite) {
        this.quantite = quantite;
    }
    public Double getMontant() {
        return montant;
    }
    public void setMontant(Double montant) {
        this.montant = montant;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public Integer getEtat() {
        return etat;
    }
    public void setEtat(Integer etat) {
        this.etat = etat;
    }
    public Double getPu() {
        return pu;
    }
    public void setPu(Double pu) {
        this.pu = pu;
    }
    public Accompagnement[] getAccompagnements() {
        return accompagnements;
    }
    public void setAccompagnements(Accompagnement[] accompagnements) {
        this.accompagnements = accompagnements;
    }
    public Accompagnement[] recupererAccompagnements(Connection connect, MyDAO dao) throws Exception{
        String addOn="where id in(select idaccompagnement from accompagnement_commandes where idcommandefille=%s) and etat=0";
        addOn=String.format(addOn, getId());
        Accompagnement[] accompagnements=dao.select(connect, Accompagnement.class, addOn);
        setAccompagnements(accompagnements);
        return accompagnements;
    }
    @Override
    public String toString() {
        return "CommandeFille [id=" + id + ", commande=" + commande + ", produit=" + produit + ", pu=" + pu
                + ", quantite=" + quantite + ", montant=" + montant + ", notes=" + notes + ", etat=" + etat
                + ", accompagnements=" + Arrays.toString(accompagnements) + "]";
    }
    public String recupererPUString(){
        return HandyManUtils.number_format(getPu(), ' ', ',', 2)+" Ar";
    }
    public String recupererMontantString(){
        return HandyManUtils.number_format(getMontant(), ' ', ',', 2)+" Ar";
    }
    public String toHtml(){
        String html="""
        <tr class="details-travaux">
            <td style="padding: 10px; border: 1px solid;">%s</td>
            <td style="padding: 10px; border: 1px solid;">%s</td>
            <td style="padding: 10px; border: 1px solid;">%s</td>
            <td style="padding: 10px; border: 1px solid;">%s</td>
            <td style="padding: 10px; border: 1px solid;text-align: right;">%s</td>
            <td style="padding: 10px; border: 1px solid;text-align: right;font-weight:bold">%s</td>
        </tr>
        """;
        String htmlAccomps="<ul>";
        for(Accompagnement a:getAccompagnements()){
            htmlAccomps+=a.getHtml();
        }
        htmlAccomps+="</ul>";
        html=String.format(html, getQuantite(), getProduit().getNom(), getNotes(), htmlAccomps, recupererPUString(), recupererMontantString());
        return html;
    }
    public String recupererEtatString(){
        switch(getEtat()){
            case Constantes.COMMANDEFILLE_OFFERT:
                return Constantes.COMMANDEFILLE_OFFERT_LABEL;
            case Constantes.COMMANDEFILLE_ANNULEE:
                return Constantes.COMMANDEFILLE_ANNULEE_LABEL;
            case Constantes.COMMANDEFILLE_SUPPRIMEE:
                return Constantes.COMMANDEFILLE_SUPPRIMEE_LABEL;
            default:
                return Constantes.COMMANDEFILLE_CREEE_LABEL;
        }
    }
    public String recupererEtatCouleur(){
        switch(getEtat()){
            case Constantes.COMMANDEFILLE_OFFERT:
                return "green";
            case Constantes.COMMANDEFILLE_ANNULEE:
                return "crimson";
            default:
                return "";
        }
    }
}
