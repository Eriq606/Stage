package com.app.makay.controllers.metier.caissier;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.app.makay.entites.CommandeEnCours;
import com.app.makay.entites.Place;
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
    private Place[] places;
    public CaissierController() throws SQLException, Exception {
        filter=new MyFilter();
        dao=new MyDAO();
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            Place where=new Place(0);
            places=dao.select(connect, Place.class, where);
        }
    }

    /*
     * Dans la demande d'addition, nous récupérons les commandes dont l'état est ADDITION (10)
     * La méthode à utiliser est la même que dans la liste commande, seulement, il n'y aura pas de contrainte d'utilisateur.
     * 
     * Nous allons d'abord implémenter la méthode sans prendre en compte le filtre par table.
     */
    @GetMapping("/demande-addition")
    public Object demandeAddition(HttpServletRequest req, Model model, Integer indice_actu, String table)throws Exception{
        HttpSession session=req.getSession();
        Utilisateur utilisateur=(Utilisateur)session.getAttribute(Constantes.VAR_SESSIONUTILISATEUR);
        Object iris=filter.checkByRole(utilisateur, new String[]{Constantes.ROLE_CAISSE}, "Makay - Demandes d'addition", "pages/caisse/demande-addition", "layout/layout", model);
        if(utilisateur==null){
            return iris;
        }
        int indiceActu=1;
        if(indice_actu!=null){
            indiceActu=indice_actu;
        }
        String tableFiltre="";
        if(table!=null){
            table=table.trim();
            tableFiltre=table;
        }
        CommandeEnCours where=new CommandeEnCours();
        where.setEtat(Constantes.COMMANDE_ADDITION);
        if(table!=null){
            where.setNomPlace(table);
        }
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            CommandeEnCours[] commandes=utilisateur.recupererDemandesAddition(connect, dao, (indiceActu-1)*Constantes.PAGINATION_LIMIT, tableFiltre);
            model.addAttribute(Constantes.VAR_COMMANDES, commandes);
            model.addAttribute(Constantes.VAR_LINKS, utilisateur.recupererLinks());
            model.addAttribute(Constantes.VAR_PLACES, places);
            HashMap<String, Object> pagination=dao.paginate(connect, CommandeEnCours.class, where, Constantes.PAGINATION_LIMIT, indiceActu);
            for(Map.Entry<String, Object> e:pagination.entrySet()){
                model.addAttribute(e.getKey(), e.getValue());
            }
            return iris;
        }
    }

    /*
     * Concernant le paiement: Une commande peut être payée en plusieurs fois.
     * Il y aura une colonne dénormalisée "reste_a_payer" tout en insérant les paiements dans la base.
     * A chaque création de commande, le reste a payer sera le montant.
     * A chaque modification de commande, le reste a payer sera mis a jour par rapport au nouveau montant.
     */
}
