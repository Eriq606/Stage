package com.app.makay.controllers.metier.superviseur;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

import com.app.makay.entites.HistoriqueRoleUtilisateur;
import com.app.makay.entites.Place;
import com.app.makay.entites.Rangee;
import com.app.makay.entites.RangeePlace;
import com.app.makay.entites.Role;
import com.app.makay.entites.Utilisateur;
import com.app.makay.entites.UtilisateurSafe;
import com.app.makay.utilitaire.Constantes;
import com.app.makay.utilitaire.MyDAO;
import com.app.makay.utilitaire.MyFilter;

import handyman.HandyManUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import veda.godao.utils.DAOConnexion;

@Controller
public class SuperviseurController {
    private MyFilter filter;
    private Place[] places;
    private Rangee[] rangees;
    private RangeePlace[] rangeePlaces;
    private UtilisateurSafe[] utilisateurs;
    private Role[] roles;
    private HistoriqueRoleUtilisateur[] attributionRoles;
    private MyDAO dao;
    private String ip;
    
    public SuperviseurController() throws SQLException, Exception {
        filter=new MyFilter();
        dao=new MyDAO();
        ip=HandyManUtils.getIP();
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            places=dao.select(connect, Place.class, new Place(0));
            for(Place p:places){
                p.setClasseHTML(p.getClasse());
            }
            rangees=dao.select(connect, Rangee.class, new Rangee(0));
            rangeePlaces=RangeePlace.getArrangementActuel(connect, dao);
            utilisateurs=dao.select(connect, UtilisateurSafe.class);
            roles=dao.select(connect, Role.class, new Role(0));
            attributionRoles=HistoriqueRoleUtilisateur.getRolesActuels(connect, dao);
        }
    }

    @GetMapping("/plan-de-table")
    public Object planTable(HttpServletRequest req, Model model){
        HttpSession session=req.getSession();
        Utilisateur utilisateur=(Utilisateur)session.getAttribute(Constantes.VAR_SESSIONUTILISATEUR);
        Object iris=filter.checkByRole(utilisateur, Constantes.ROLE_SUPERVISEUR, "Makay - Plan de table", "pages/superviseur/plan-table", "layout/layout", model);
        model.addAttribute(Constantes.VAR_LINKS, Constantes.LINK_SUPERVISEUR);
        model.addAttribute(Constantes.VAR_PLACES, places);
        model.addAttribute(Constantes.VAR_RANGEES, rangees);
        model.addAttribute(Constantes.VAR_RANGEEPLACES, rangeePlaces);
        return iris;
    }
    @PostMapping("/plan-de-table")
    public RedirectView planTable(HttpServletRequest req, String arrangements) throws SQLException, Exception{
        HttpSession session=req.getSession();
        Utilisateur utilisateur=(Utilisateur)session.getAttribute(Constantes.VAR_SESSIONUTILISATEUR);
        RedirectView iris=filter.checkByRolePOST(utilisateur, Constantes.ROLE_SUPERVISEUR);
        if(iris!=null){
            return iris;
        }
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            utilisateur.mettreAJourPlanTable(connect, dao, arrangements);
            connect.commit();
            setRangeePlaces(RangeePlace.getArrangementActuel(connect, dao));
            return new RedirectView("/plan-de-table");
        }
    }
    @GetMapping("/attribution-de-roles")
    public Object attributionRole(HttpServletRequest req, Model model){
        HttpSession session=req.getSession();
        Utilisateur utilisateur=(Utilisateur)session.getAttribute(Constantes.VAR_SESSIONUTILISATEUR);
        Object iris=filter.checkByRole(utilisateur, Constantes.ROLE_SUPERVISEUR, "Makay - Attribution de role", "pages/superviseur/attribution-role", "layout/layout", model);
        model.addAttribute(Constantes.VAR_UTILISATEURS, utilisateurs);
        model.addAttribute(Constantes.VAR_ROLES, roles);
        model.addAttribute(Constantes.VAR_ROLEUTILISATEURS, attributionRoles);
        model.addAttribute(Constantes.VAR_LINKS, Constantes.LINK_SUPERVISEUR);
        model.addAttribute(Constantes.VAR_IP, ip);
        model.addAttribute(Constantes.VAR_SESSIONUTILISATEUR, utilisateur);
        return iris;
    }

    @MessageMapping("/notify-redirect")
    @SendTo("/receive-notify-redirect")
    public void notifierModifications(){

    }

    @GetMapping("/reset-cache-superviseur")
    public RedirectView resetCacheRoles(HttpServletRequest req) throws Exception{
        HttpSession session=req.getSession();
        Utilisateur utilisateur=(Utilisateur)req.getAttribute(Constantes.VAR_SESSIONUTILISATEUR);
        RedirectView iris=filter.checkByRolePOST(utilisateur, Constantes.ROLE_SUPERVISEUR);
        if(iris!=null){
            return iris;
        }
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            HistoriqueRoleUtilisateur[] rolesActuels=HistoriqueRoleUtilisateur.getRolesActuels(connect, dao);
            setAttributionRoles(rolesActuels);
            for(HistoriqueRoleUtilisateur h:rolesActuels){
                if(h.getUtilisateur().getId()==utilisateur.getId()){
                    utilisateur.setRole(h.getRole());
                    session.setAttribute(Constantes.VAR_SESSIONUTILISATEUR, utilisateur);
                    break;
                }
            }
        }
        return filter.distributeByRole(utilisateur);
    }

    public MyFilter getFilter() {
        return filter;
    }

    public void setFilter(MyFilter filter) {
        this.filter = filter;
    }

    public Place[] getPlaces() {
        return places;
    }

    public void setPlaces(Place[] places) {
        this.places = places;
    }

    public Rangee[] getRangees() {
        return rangees;
    }

    public void setRangees(Rangee[] rangees) {
        this.rangees = rangees;
    }

    public RangeePlace[] getRangeePlaces() {
        return rangeePlaces;
    }

    public void setRangeePlaces(RangeePlace[] rangeePlaces) {
        this.rangeePlaces = rangeePlaces;
    }

    public MyDAO getDao() {
        return dao;
    }

    public void setDao(MyDAO dao) {
        this.dao = dao;
    }

    public UtilisateurSafe[] getUtilisateurs() {
        return utilisateurs;
    }

    public void setUtilisateurs(UtilisateurSafe[] utilisateurs) {
        this.utilisateurs = utilisateurs;
    }

    public Role[] getRoles() {
        return roles;
    }

    public void setRoles(Role[] roles) {
        this.roles = roles;
    }

    public HistoriqueRoleUtilisateur[] getAttributionRoles() {
        return attributionRoles;
    }

    public void setAttributionRoles(HistoriqueRoleUtilisateur[] attributionRoles) {
        this.attributionRoles = attributionRoles;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
