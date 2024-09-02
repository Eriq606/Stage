package com.app.makay.entites;

import veda.godao.annotations.Column;
import veda.godao.annotations.ForeignKey;
import veda.godao.annotations.PrimaryKey;
import veda.godao.annotations.Table;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;

import com.app.makay.utilitaire.MyDAO;

@Table("historique_role_utilisateurs")
public class HistoriqueRoleUtilisateur {
    @PrimaryKey
    @Column("id")
    private Integer id;
    @ForeignKey(recursive = true)
    @Column("idutilisateur")
    private Utilisateur utilisateur;
    @ForeignKey(recursive = true)
    @Column("idrole")
    private Role role;
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
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
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
    public static HistoriqueRoleUtilisateur[] getRolesActuels(Connection connect, MyDAO dao) throws Exception{
        String query="select id, idutilisateur, idrole, dateheure, nom_utilisateur, nom_role from v_attribution_roles";
        HashMap<String, Object>[] objets=dao.select(connect, query);
        HistoriqueRoleUtilisateur[] attributions=new HistoriqueRoleUtilisateur[objets.length];
        Utilisateur utilisateur;
        Role role;
        for(int i=0;i<attributions.length;i++){
            attributions[i]=new HistoriqueRoleUtilisateur();
            attributions[i].setId((int)objets[i].get("id"));
            utilisateur=new Utilisateur();
            utilisateur.setId((int)objets[i].get("idutilisateur"));
            utilisateur.setNom((String)objets[i].get("nom_utilisateur"));
            attributions[i].setUtilisateur(utilisateur);
            role=new Role();
            role.setId((int)objets[i].get("idrole"));
            role.setNom((String)objets[i].get("nom_role"));
            attributions[i].setRole(role);
        }
        return attributions;
    }
}
