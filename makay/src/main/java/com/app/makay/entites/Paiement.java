package com.app.makay.entites;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;

import com.app.makay.entites.statistiques.ChiffrePaiement;
import com.app.makay.utilitaire.Constantes;
import com.app.makay.utilitaire.MyDAO;

import handyman.HandyManUtils;
import veda.godao.annotations.Column;
import veda.godao.annotations.ForeignKey;
import veda.godao.annotations.PrimaryKey;
import veda.godao.annotations.Table;

@Table("paiements")
public class Paiement {
    @PrimaryKey
    @Column("id")
    private Integer id;
    @ForeignKey(recursive = true)
    @Column("idcommande")
    private Commande commande;
    @ForeignKey(recursive = true)
    @Column("idmodepaiement")
    private ModePaiement modePaiement;
    @Column("dateheure")
    private LocalDateTime dateheure;
    @Column("montant")
    private Double montant;
    @Column("etat")
    private Integer etat;
    @ForeignKey(recursive = true)
    @Column("idutilisateur")
    private Utilisateur utilisateur;
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
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
    public ModePaiement getModePaiement() {
        return modePaiement;
    }
    public void setModePaiement(ModePaiement modePaiement) {
        this.modePaiement = modePaiement;
    }
    public LocalDateTime getDateheure() {
        return dateheure;
    }
    public void setDateheure(LocalDateTime dateheure) {
        this.dateheure = dateheure;
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
    public String recupererHeure(){
        String reponse=LocalDate.now().isAfter(getDateheure().toLocalDate())?getDateheure().toLocalDate().toString()+" ":"";
        int heure=getDateheure().getHour();
        String heureStr=String.valueOf(heure);
        if(heure<10){
            heureStr="0"+heureStr;
        }
        int minute=getDateheure().getMinute();
        String minuteStr=String.valueOf(minute);
        if(minute<10){
            minuteStr="0"+minuteStr;
        }
        reponse+=heureStr+"h"+minuteStr;
        return reponse;
    }
    public String recupererHeureStringDocument(){
        String date=getDateheure().toLocalDate().toString();
        String heure=getDateheure().toLocalTime().minusNanos(getDateheure().toLocalTime().getNano()).toString();
        return date+" "+heure;
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
            <td style="padding: 10px; border: 1px solid;text-align:right">%s</td>
        </tr>
        """;
        html=String.format(html, recupererHeureStringDocument(), getUtilisateur().getNom(), getModePaiement().getNom(), recupererMontantString());
        return html;
    }
    /* 
     * select *, nommode from paiements join mode_paiements  
     * select nommode, sum(montant) from paiements group by nommode as v_paiements_somme
     * select * from v_paiements_somme order by somme desc limit 4
     * 
     * 
     */
    public static ChiffrePaiement chiffrePaiements(Connection connect, MyDAO dao, LocalDateTime dateDebut, LocalDateTime dateFin) throws SQLException{
        String queryChiffres="select nom_mode, coalesce(sum(montant),0) as somme from v_paiement_mode_paiements where etat=? and dateheure>=? and dateheure<? group by nom_mode order by somme desc limit ?";
        String queryTotal="select coalesce(sum(montant),0) as somme from paiements where etat=? and dateheure>=? and dateheure<?";
        ChiffrePaiement chiffre=new ChiffrePaiement();
        chiffre.setCouleurs(new String[]{
            Constantes.STAT_COULEUR_1,
            Constantes.STAT_COULEUR_2,
            Constantes.STAT_COULEUR_3,
            Constantes.STAT_COULEUR_4,
            Constantes.STAT_COULEUR_5
        });
        LinkedList<String> listeEtiquettes=new LinkedList<>();
        LinkedList<Double> listeDonnees=new LinkedList<>();
        double totalPaiements=0;
        double totalChiffres=0;
        try(PreparedStatement statement=connect.prepareStatement(queryChiffres);
        PreparedStatement statement2=connect.prepareStatement(queryTotal)){
            statement.setInt(1, Constantes.PAIEMENT_CREE);
            statement.setTimestamp(2, Timestamp.valueOf(dateDebut));
            statement.setTimestamp(3, Timestamp.valueOf(dateFin));
            statement.setInt(4, Constantes.LIMIT_CLASSEMENT_PAIEMENT);
            statement2.setInt(1, Constantes.PAIEMENT_CREE);
            statement2.setTimestamp(2, Timestamp.valueOf(dateDebut));
            statement2.setTimestamp(3, Timestamp.valueOf(dateFin));
            try(ResultSet result=statement.executeQuery();
            ResultSet result2=statement2.executeQuery()){
                if(result2.next()){
                    totalPaiements=result2.getDouble("somme");
                }
                while(result.next()){
                    listeEtiquettes.add(result.getString("nom_mode"));
                    listeDonnees.add(result.getDouble("somme"));
                    totalChiffres+=result.getDouble("somme");
                }
                listeEtiquettes.add("Autres");
                listeDonnees.add(totalPaiements-totalChiffres);
                chiffre.setDonnees(listeDonnees.toArray(new Double[listeDonnees.size()]));
                chiffre.setEtiquettes(listeEtiquettes.toArray(new String[listeEtiquettes.size()]));
                return chiffre;
            }
        }
    }
}
