package com.app.makay.entites;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;

import com.app.makay.utilitaire.Constantes;
import com.app.makay.utilitaire.MyDAO;

import handyman.HandyManUtils;
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
    @Column("nom_place")
    private String nomPlace;
    @Column("dateheure_ouverture")
    private LocalDateTime ouverture;
    @Column("dateheure_cloture")
    private LocalDateTime cloture;
    @Column("montant")
    private Double montant;
    @Column("etat")
    private Integer etat;
    private CommandeFilleEnCours[] commandeFilles;
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
    
    public CommandeFilleEnCours[] recupererCommandeFillesWithoutSet(Connection connect, MyDAO dao) throws Exception{
        String query="select * from v_commandefille_produits where idcommande=%s";
        query=String.format(query, getId());
        HashMap<String, Object>[] objets=dao.select(connect, query);
        CommandeFilleEnCours[] commandeFilles=new CommandeFilleEnCours[objets.length];
        Produit produit;
        Categorie categorie;
        for(int i=0;i<commandeFilles.length;i++){
            commandeFilles[i]=new CommandeFilleEnCours();
            commandeFilles[i].setId((int)objets[i].get("id"));
            commandeFilles[i].setCommande(this);
            produit=new Produit();
            produit.setId((int)objets[i].get("idproduit"));
            produit.setNom((String)objets[i].get("nom"));
            produit.setPrix(((BigDecimal)objets[i].get("prix")).doubleValue());
            categorie=new Categorie();
            categorie.setId((int)objets[i].get("idcategorie"));
            produit.setCategorie(categorie);
            commandeFilles[i].setProduit(produit);
            commandeFilles[i].setPu(((BigDecimal)objets[i].get("prix_unitaire")).doubleValue());
            commandeFilles[i].setQuantite(((BigDecimal)objets[i].get("quantite")).doubleValue());
            commandeFilles[i].setMontant(((BigDecimal)objets[i].get("montant")).doubleValue());
            commandeFilles[i].setNotes((String)objets[i].get("notes"));
            commandeFilles[i].setEstTermine((int)objets[i].get("est_termine"));
            commandeFilles[i].recupererAccompagnements(connect, dao);
        }
        // setCommandeFilles(commandeFilles);
        return commandeFilles;
    }
    public CommandeFilleEnCours[] recupererCommandeFilles(Connection connect, MyDAO dao) throws Exception{
        setCommandeFilles(recupererCommandeFillesWithoutSet(connect, dao));
        return commandeFilles;
    }
    public CommandeFilleEnCours[] recupererCommandeFillesChecking(Connection connect, MyDAO dao, Role role) throws Exception{
        String query="select * from v_commandefille_produits where idcommande=%s and idcategorie in (select idcategorie from v_role_categorie_produits_checkings where idrole=%s)";
        query=String.format(query, getId(), role.getId());
        HashMap<String, Object>[] objets=dao.select(connect, query);
        CommandeFilleEnCours[] commandeFilles=new CommandeFilleEnCours[objets.length];
        Produit produit;
        Categorie categorie;
        for(int i=0;i<commandeFilles.length;i++){
            commandeFilles[i]=new CommandeFilleEnCours();
            commandeFilles[i].setId((int)objets[i].get("id"));
            commandeFilles[i].setCommande(this);
            produit=new Produit();
            produit.setId((int)objets[i].get("idproduit"));
            produit.setNom((String)objets[i].get("nom"));
            produit.setPrix(((BigDecimal)objets[i].get("prix")).doubleValue());
            categorie=new Categorie();
            categorie.setId((int)objets[i].get("idcategorie"));
            produit.setCategorie(categorie);
            commandeFilles[i].setProduit(produit);
            commandeFilles[i].setPu(((BigDecimal)objets[i].get("prix_unitaire")).doubleValue());
            commandeFilles[i].setQuantite(((BigDecimal)objets[i].get("quantite")).doubleValue());
            commandeFilles[i].setMontant(((BigDecimal)objets[i].get("montant")).doubleValue());
            commandeFilles[i].setNotes((String)objets[i].get("notes"));
            commandeFilles[i].setEstTermine((int)objets[i].get("est_termine"));
            commandeFilles[i].recupererAccompagnements(connect, dao);
        }
        setCommandeFilles(commandeFilles);
        return commandeFilles;
    }
    public CommandeFilleEnCours[] getCommandeFilles() {
        return commandeFilles;
    }
    public void setCommandeFilles(CommandeFilleEnCours[] commandeFilles) {
        this.commandeFilles = commandeFilles;
    }

    public String getNomPlace() {
        return nomPlace;
    }

    public void setNomPlace(String nomPlace) {
        this.nomPlace = nomPlace;
    }
    public String getMontantString(){
        String montantString=HandyManUtils.number_format(getMontant(), ' ', ',', 2);
        montantString+=" Ar";
        return montantString;
    }
    public String getResteAPayerString(){
        return HandyManUtils.number_format(getResteAPayer(), ' ', ',', 2)+" Ar";
    }
}
