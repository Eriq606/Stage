package com.app.makay.controllers.metier;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.app.makay.entites.Place;
import com.app.makay.entites.Rangee;
import com.app.makay.entites.RangeePlace;
import com.app.makay.entites.Utilisateur;
import com.app.makay.utilitaire.Constantes;
import com.app.makay.utilitaire.MyDAO;
import com.app.makay.utilitaire.MyFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import veda.godao.utils.DAOConnexion;

@Controller
public class SuperviseurController {
    private MyFilter filter;
    private Place[] places;
    private Rangee[] rangees;
    private RangeePlace[] rangeePlaces;
    private MyDAO dao;
    
    public SuperviseurController() throws SQLException, Exception {
        filter=new MyFilter();
        dao=new MyDAO();
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            places=dao.select(connect, Place.class);
            rangees=dao.select(connect, Rangee.class);
            rangeePlaces=RangeePlace.getArrangementActuel(connect, dao);
        }
    }

    @GetMapping("/plan-de-table")
    public Object planTable(HttpServletRequest req, Model model){
        HttpSession session=req.getSession();
        Utilisateur utilisateur=(Utilisateur)session.getAttribute(Constantes.VAR_SESSIONUTILISATEUR);
        Object iris=filter.checkByRole(utilisateur, Constantes.ROLE_SUPERVISEUR, "Makay - Plan de table", "pages/superviseur/plan-table", "layout/layout", model);
        model.addAttribute(Constantes.VAR_PLACES, places);
        model.addAttribute(Constantes.VAR_RANGEES, rangees);
        model.addAttribute(Constantes.VAR_RANGEEPLACES, rangeePlaces);
        return iris;
    }
}
