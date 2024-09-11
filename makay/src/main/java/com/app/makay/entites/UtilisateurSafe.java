package com.app.makay.entites;

import java.sql.Connection;
import java.util.HashMap;

import com.app.makay.utilitaire.MyDAO;

import veda.godao.annotations.Column;
import veda.godao.annotations.ForeignKey;
import veda.godao.annotations.PrimaryKey;
import veda.godao.annotations.Table;

@Table("v_utilisateurs")
public class UtilisateurSafe {
    @PrimaryKey
    @Column("id")
    private Integer id;
    @Column("nom")
    private String nom;
    @Column("email")
    private String email;
    @Column("contact")
    private String contact;
    @ForeignKey(recursive = true)
    @Column("idrole")
    private Role role;
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
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getContact() {
        return contact;
    }
    public void setContact(String contact) {
        this.contact = contact;
    }
    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }
    public static UtilisateurSafe[] recupererServeursEnCours(Connection connect, MyDAO dao) throws Exception{
        String query="select * from v_serveurs_encours";
        HashMap<String, Object>[] objets=dao.select(connect, query);
        UtilisateurSafe[] utilisateurSafes=new UtilisateurSafe[objets.length];
        Role role;
        for(int i=0;i<utilisateurSafes.length;i++){
            utilisateurSafes[i]=new UtilisateurSafe();
            utilisateurSafes[i].setId((int)objets[i].get("id"));
            utilisateurSafes[i].setNom((String)objets[i].get("nom"));
            utilisateurSafes[i].setEmail((String)objets[i].get("email"));
            utilisateurSafes[i].setContact((String)objets[i].get("contact"));
            role=new Role();
            role.setId((int)objets[i].get("idrole"));
            role.setNom((String)objets[i].get("nom_role"));
            role.setNumero((String)objets[i].get("numero_role"));
            utilisateurSafes[i].setRole(role);
        }
        return utilisateurSafes;
    }
}
