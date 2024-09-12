package com.app.makay.controllers.metier.barman;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

import com.app.makay.entites.Produit;
import com.app.makay.entites.Role;
import com.app.makay.entites.Utilisateur;
import com.app.makay.utilitaire.Constantes;
import com.app.makay.utilitaire.MyDAO;
import com.app.makay.utilitaire.MyFilter;

import handyman.HandyManUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import veda.godao.utils.DAOConnexion;

@Controller
public class BarmanController {
    private MyFilter filter;
    private MyDAO dao;
    private String ip;
    private Produit[] produits;
    public MyFilter getFilter() {
        return filter;
    }
    public void setFilter(MyFilter filter) {
        this.filter = filter;
    }
    public MyDAO getDao() {
        return dao;
    }
    public void setDao(MyDAO dao) {
        this.dao = dao;
    }
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public Produit[] getProduits() {
        return produits;
    }
    public void setProduits(Produit[] produits) {
        this.produits = produits;
    }
    public BarmanController() throws SQLException, Exception {
        filter=new MyFilter();
        dao=new MyDAO();
        ip=HandyManUtils.getIP();
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            Utilisateur utilisateur=new Utilisateur();
            Role role=new Role();
            role.setNumero(Constantes.ROLE_BAR);
            role=dao.select(connect, Role.class, role)[0];
            utilisateur.setRole(role);
            produits=utilisateur.recupererProduitsCorrespondant(connect, dao);
            for(Produit p:produits){
                p.setAccompagnements(p.getAllAccompagnements(connect, dao));
            }
        }
    }
    @GetMapping("/barman-passer-commande")
    public Object passerCommande(HttpServletRequest req, Model model){
        HttpSession session=req.getSession();
        Utilisateur utilisateur=(Utilisateur)session.getAttribute(Constantes.VAR_SESSIONUTILISATEUR);
        Object iris=filter.checkByRole(utilisateur, new String[]{Constantes.ROLE_BAR, Constantes.ROLE_SUPERVISEUR}, "Makay - Passer une commande", "pages/serveur/prise-commande", "layout/layout", model);
        model.addAttribute(Constantes.VAR_LINKS, Constantes.LINK_BARMAN);
        model.addAttribute(Constantes.VAR_PRODUITS, produits);
        model.addAttribute(Constantes.VAR_IP, ip);
        model.addAttribute(Constantes.VAR_SESSIONUTILISATEUR, utilisateur);
        model.addAttribute(Constantes.VAR_SESSIONID, session.getId());
        model.addAttribute(Constantes.VAR_RESETCACHE, Constantes.URL_RESET_CACHE_BARMAN);
        model.addAttribute(Constantes.VAR_RECEIVENOTIFY, Constantes.URL_RECEIVE_NOTIFY_BARMAN);
        if(utilisateur!=null){
            model.addAttribute(Constantes.VAR_PLACES, utilisateur.getPlaces());
        }
        return iris;
    }

    @MessageMapping("/notify-redirect-barman")
    @SendTo("/notify/receive-notify-redirect-barman")
    public String notifierModifications(){
        return "reset cache";
    }

    @GetMapping("/reset-role-barman")
    public RedirectView resetCacheRoles(HttpServletRequest req) throws Exception{
        return filter.resetUserRole(req, dao, Constantes.ROLE_BAR);
    }

    @GetMapping("/reset-cache-barman")
    public RedirectView resetCacheProduits(HttpServletRequest req) throws Exception{
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            Utilisateur utilisateur=new Utilisateur();
            Role role=new Role();
            role.setNumero(Constantes.ROLE_BAR);
            role=dao.select(connect, Role.class, role)[0];
            utilisateur.setRole(role);
            produits=utilisateur.recupererProduitsCorrespondant(connect, dao);
            for(Produit p:produits){
                p.setAccompagnements(p.getAllAccompagnements(connect, dao));
            }
        }
        return resetCacheRoles(req);
    }
}
