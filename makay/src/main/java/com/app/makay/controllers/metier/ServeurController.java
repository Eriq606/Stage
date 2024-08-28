package com.app.makay.controllers.metier;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// import com.app.makay.entites.Place;
import com.app.makay.entites.Produit;
import com.app.makay.entites.Utilisateur;
import com.app.makay.utilitaire.Constantes;
import com.app.makay.utilitaire.MyDAO;
import com.app.makay.utilitaire.MyFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import veda.godao.utils.DAOConnexion;

@Controller
public class ServeurController {
    private MyFilter filter;
    private Produit[] produits;
    // private Place[] places;
    private MyDAO dao;

    public ServeurController() throws SQLException, Exception{
        filter=new MyFilter();
        dao=new MyDAO();
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            produits=dao.select(connect, Produit.class);
            for(Produit p:produits){
                p.setAccompagnements(p.getAllAccompagnements(connect, dao));
            }
            // places=dao.select(connect, null)
        }
    }

    @GetMapping("/serveur-passer-commande")
    public Object passerCommande(HttpServletRequest req, Model model){
        HttpSession session=req.getSession();
        Utilisateur utilisateur=(Utilisateur)session.getAttribute(Constantes.VAR_SESSIONUTILISATEUR);
        Object iris=filter.checkByRole(utilisateur, Constantes.ROLE_SERVEUR, "Makay - Passer une commande", "pages/serveur/prise-commande", "layout/layout", model);
        model.addAttribute(Constantes.VAR_PRODUITS, produits);
        return iris;
    }
}
