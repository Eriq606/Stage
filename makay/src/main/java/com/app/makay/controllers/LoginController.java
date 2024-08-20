package com.app.makay.controllers;

import java.sql.Connection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.makay.entites.Utilisateur;
import com.app.makay.utilitaire.Constantes;
import com.app.makay.utilitaire.MyDAO;
import com.app.makay.utilitaire.ReponseREST;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import veda.godao.utils.DAOConnexion;

@RestController
public class LoginController {
    private MyDAO dao;
    @Autowired
    public LoginController(MyDAO dao){
        this.dao=dao;
    }
    @PostMapping("/login")
    public ReponseREST login(HttpServletRequest req, @RequestParam("email") String email, @RequestParam("motdepasse") String motDePasse) throws Exception{
        Connection connect=DAOConnexion.getConnexion(dao);
        Object[] reponseConnexion=Utilisateur.seConnecter(dao, connect, email, motDePasse);
        ReponseREST reponse=new ReponseREST();
        if((int)reponseConnexion[0]==0){
            reponse.setCode(Constantes.CODE_UTILISATEUR_NON_IDENTIFIE);
            reponse.setMessage(Constantes.MSG_UTILISATEUR_NON_IDENTIFIE);
            return reponse;
        }
        HttpSession session=req.getSession();
        session.setAttribute("idUtilisateur", reponseConnexion[0]);
        session.setAttribute("nomUtilisateur", reponseConnexion[1]);
        session.setAttribute("roleUtilisateur", reponseConnexion[2]);
        reponse.setCode(Constantes.CODE_SUCCES);
        reponse.setMessage(Constantes.MSG_SUCCES);
        return reponse;
    }
    @GetMapping("/logout")
    public ReponseREST logout(HttpServletRequest req){
        HttpSession session=req.getSession();
        session.invalidate();
        ReponseREST reponse=new ReponseREST();
        reponse.setCode(Constantes.CODE_SUCCES);
        reponse.setMessage(Constantes.MSG_SUCCES);
        return reponse;
    }
}
