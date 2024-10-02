package com.app.makay.controllers.metier.caissier;

import java.sql.Connection;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.app.makay.entites.DemandeAddition;
import com.app.makay.entites.Utilisateur;
import com.app.makay.utilitaire.Constantes;
import com.app.makay.utilitaire.MyDAO;
import com.app.makay.utilitaire.MyFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import veda.godao.utils.DAOConnexion;

@Controller
public class CaissierController {
    private MyFilter filter;
    private MyDAO dao;
    public CaissierController() {
        filter=new MyFilter();
        dao=new MyDAO();
    }

    public Object demandeAddition(HttpServletRequest req, Model model, Integer indice)throws Exception{
        HttpSession session=req.getSession();
        Utilisateur utilisateur=(Utilisateur)session.getAttribute(Constantes.VAR_SESSIONUTILISATEUR);
        Object iris=filter.checkByRole(utilisateur, new String[]{Constantes.ROLE_CAISSE}, "Makay - Demandes d'addition", "pages/caisse/demande-addition", "layout/layout", model);
        if(utilisateur==null){
            return iris;
        }
        int indiceActu=1;
        if(indice!=null){
            indiceActu=indice;
        }
        DemandeAddition where=new DemandeAddition();
        where.setEtat(0);
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            DemandeAddition[] demandes=dao.select(connect, DemandeAddition.class, where);
            // model.addAttribute(null, demandes)
            model.addAttribute(Constantes.VAR_LINKS, utilisateur.recupererLinks());
            return iris;
        }
    }
}
