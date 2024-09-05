package com.app.makay.controllers.metier.serveur;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

import com.app.makay.entites.Produit;
import com.app.makay.entites.Utilisateur;
import com.app.makay.utilitaire.Constantes;
import com.app.makay.utilitaire.MyDAO;
import com.app.makay.utilitaire.MyFilter;

import handyman.HandyManUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import veda.godao.utils.DAOConnexion;

@Controller
public class ServeurController {
    private MyFilter filter;
    private Produit[] produits;
    private String ip;
    private MyDAO dao;

    public ServeurController() throws SQLException, Exception{
        filter=new MyFilter();
        dao=new MyDAO();
        ip=HandyManUtils.getIP();
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            produits=dao.select(connect, Produit.class, new Produit(0));
            for(Produit p:produits){
                p.setAccompagnements(p.getAllAccompagnements(connect, dao));
            }
        }
    }

    @GetMapping("/serveur-passer-commande")
    public Object passerCommande(HttpServletRequest req, Model model){
        HttpSession session=req.getSession();
        Utilisateur utilisateur=(Utilisateur)session.getAttribute(Constantes.VAR_SESSIONUTILISATEUR);
        Object iris=filter.checkByRole(utilisateur, Constantes.ROLE_SERVEUR, "Makay - Passer une commande", "pages/serveur/prise-commande", "layout/layout", model);
        model.addAttribute(Constantes.VAR_PRODUITS, produits);
        model.addAttribute(Constantes.VAR_LINKS, Constantes.LINK_SERVEUR);
        model.addAttribute(Constantes.VAR_IP, ip);
        model.addAttribute(Constantes.VAR_SESSIONUTILISATEUR, utilisateur);
        model.addAttribute(Constantes.VAR_SESSIONID, session.getId());
        if(utilisateur!=null){
            model.addAttribute(Constantes.VAR_PLACES, utilisateur.getPlaces());
        }
        return iris;
    }

    @MessageMapping("/notify-redirect-serveur")
    @SendTo("/notify/receive-notify-redirect-serveur")
    public String notifierModifications(){
        return "reset cache";
    }

    @GetMapping("/reset-role-serveur")
    public RedirectView resetCacheRoles(HttpServletRequest req) throws Exception{
        return filter.resetUserRole(req, dao, Constantes.ROLE_SERVEUR);
    }

    @GetMapping("/reset-cache-serveur")
    public RedirectView resetCacheProduits(HttpServletRequest req) throws Exception{
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            produits=dao.select(connect, Produit.class, new Produit(0));
            for(Produit p:produits){
                p.setAccompagnements(p.getAllAccompagnements(connect, dao));
            }
        }
        return resetCacheRoles(req);
    }
}
