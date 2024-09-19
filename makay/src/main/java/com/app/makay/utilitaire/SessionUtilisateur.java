package com.app.makay.utilitaire;

import java.sql.Connection;
import java.time.LocalDateTime;

import com.app.makay.entites.Utilisateur;

import veda.godao.annotations.Column;
import veda.godao.annotations.ForeignKey;
import veda.godao.annotations.PrimaryKey;
import veda.godao.annotations.Table;

@Table("session_utilisateurs")
public class SessionUtilisateur {
    @PrimaryKey
    @Column("id")
    private Integer id;
    @Column("sessionid")
    private String sessionId;
    @Column("expiration")
    private LocalDateTime expiration;
    @ForeignKey(recursive = true)
    @Column("idutilisateur")
    private Utilisateur utilisateur;
    @Column("estvalide")
    private Integer estValide;
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getSessionId() {
        return sessionId;
    }
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    public LocalDateTime getExpiration() {
        return expiration;
    }
    public void setExpiration(LocalDateTime expiration) {
        this.expiration = expiration;
    }
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
    public Integer getEstValide() {
        return estValide;
    }
    public void setEstValide(Integer estValide) {
        this.estValide = estValide;
    }
    public void enregistrer(Connection connect, MyDAO dao) throws Exception{
        try{
            dao.insertWithoutPrimaryKey(connect, this);
        }catch(Exception e){
            connect.rollback();
            throw e;
        }
    }
}
