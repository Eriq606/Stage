package com.app.makay.controllers;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

import com.app.makay.entites.Utilisateur;
import com.app.makay.utilitaire.Constantes;
import com.app.makay.utilitaire.MyDAO;
import com.app.makay.utilitaire.MyFilter;
import handyman.HandyManUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import veda.godao.utils.DAOConnexion;

@Controller
public class LoginController {
    private MyDAO dao=new MyDAO();
    private MyFilter filterChain=new MyFilter();

    @GetMapping("/login")
    public String login(HttpServletRequest req, String message, Model model){
        HttpSession session=req.getSession();
        session.removeAttribute(Constantes.VAR_SESSIONUTILISATEUR);
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
            session.setAttribute(Constantes.VAR_SESSIONUTILISATEUR, utilisateur);
            return filterChain.distributeByRole(utilisateur);
        }
    }
    @GetMapping("/login-success")
    public String mock(){
        return "woohoo";
    }
}
