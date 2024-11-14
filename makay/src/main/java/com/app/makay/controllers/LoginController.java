package com.app.makay.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

import com.app.makay.entites.Utilisateur;
import com.app.makay.utilitaire.Constantes;
import com.app.makay.utilitaire.MyDAO;
import com.app.makay.utilitaire.MyFilter;
import com.app.makay.utilitaire.SessionUtilisateur;

import handyman.HandyManUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import veda.godao.utils.DAOConnexion;

@Controller
public class LoginController {
    private MyDAO dao;
    private MyFilter filterChain=new MyFilter();
    
    public LoginController() throws IOException {
        dao=new MyDAO();
    }

    @GetMapping("/login")
    public String login(HttpServletRequest req, String message, Model model){
        HttpSession session=req.getSession();
        session.invalidate();
        session=req.getSession();
        String messageDecoded="";
        if(message!=null){
            messageDecoded=HandyManUtils.decodeURL_UTF8(message);
        }
        model.addAttribute(Constantes.VAR_TITLE, "Makay - Connexion");
        model.addAttribute(Constantes.VAR_MESSAGE, messageDecoded);
        return "login/login";
    }
    
    @PostMapping("/login")
    public RedirectView login(HttpServletRequest req, String email, String motDePasse) throws SQLException, Exception{
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            Utilisateur utilisateur=Utilisateur.seConnecter(dao, connect, email, motDePasse);
            if(utilisateur==null){
                String message=HandyManUtils.encodeURL_UTF8(Constantes.MSG_UTILISATEUR_NON_IDENTIFIE);
                return new RedirectView("/login?message="+message);
            }
            HttpSession session=req.getSession();
            SessionUtilisateur sessionUser=new SessionUtilisateur();
            sessionUser.setSessionId(session.getId());
            sessionUser.setExpiration(LocalDateTime.now().plusHours(Constantes.SESSION_EXPIRATION.longValue()));
            sessionUser.setUtilisateur(utilisateur);
            sessionUser.setEstValide(Constantes.SESSION_ESTVALIDE);
            sessionUser.enregistrer(connect, dao);
            connect.commit();
            session.setAttribute(Constantes.VAR_SESSIONUTILISATEUR, utilisateur);
            return filterChain.distributeByRole(utilisateur);
        }
    }
    @GetMapping("/logout")
    public RedirectView seDeconnecter(HttpServletRequest req, String message) throws SQLException, Exception{
        HttpSession session=req.getSession();
        Utilisateur utilisateur=(Utilisateur)session.getAttribute(Constantes.VAR_SESSIONUTILISATEUR);
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            if(utilisateur!=null){
                utilisateur.seDeconnecter(connect, dao, session);
                connect.commit();
            }
        }
        String messageDecoded="";
        if(message!=null){
            messageDecoded=message;
        }
        return new RedirectView("/login?message="+messageDecoded);
    }
    @GetMapping("/login-success")
    public String mock(){
        return "woohoo";
    }
    @GetMapping("/login-admin")
    public String loginAdmin(HttpServletRequest req, String message, Model model){
        HttpSession session=req.getSession();
        session.invalidate();
        session=req.getSession();
        String messageDecoded="";
        if(message!=null){
            messageDecoded=HandyManUtils.decodeURL_UTF8(message);
        }
        model.addAttribute(Constantes.VAR_TITLE, "Makay - Connexion");
        model.addAttribute(Constantes.VAR_MESSAGE, messageDecoded);
        return "login/login-admin";
    }
    @PostMapping("/login-admin")
    public RedirectView loginAdmin(HttpServletRequest req, String email, String motDePasse) throws SQLException, Exception{
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            Utilisateur utilisateur=Utilisateur.seConnecterAdmin(dao, connect, email, motDePasse);
            if(utilisateur==null){
                String message=HandyManUtils.encodeURL_UTF8(Constantes.MSG_UTILISATEUR_NON_IDENTIFIE);
                return new RedirectView("/login?message="+message);
            }
            HttpSession session=req.getSession();
            SessionUtilisateur sessionUser=new SessionUtilisateur();
            sessionUser.setSessionId(session.getId());
            sessionUser.setExpiration(LocalDateTime.now().plusHours(Constantes.SESSION_EXPIRATION.longValue()));
            sessionUser.setUtilisateur(utilisateur);
            sessionUser.setEstValide(Constantes.SESSION_ESTVALIDE);
            sessionUser.enregistrer(connect, dao);
            connect.commit();
            session.setAttribute(Constantes.VAR_SESSIONUTILISATEUR, utilisateur);
            return new RedirectView("/utilisateurs");
        }
    }
}
