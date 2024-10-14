package com.app.makay.controllers.metier.superviseur;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import com.app.makay.entites.CommandeFille;
import com.app.makay.entites.HistoriqueRoleUtilisateur;
import com.app.makay.entites.Place;
import com.app.makay.entites.Rangee;
import com.app.makay.entites.RangeePlace;
import com.app.makay.entites.Role;
import com.app.makay.entites.Utilisateur;
import com.app.makay.entites.UtilisateurSafe;
import com.app.makay.entites.REST.ActionSuperviseurREST;
import com.app.makay.entites.REST.EnvoiCommandeREST;
import com.app.makay.entites.REST.ModificationDispatchREST;
import com.app.makay.utilitaire.Constantes;
import com.app.makay.utilitaire.MyDAO;
import com.app.makay.utilitaire.MyFilter;
import com.app.makay.utilitaire.ReponseREST;
import com.app.makay.utilitaire.RestData;
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
    
    public SuperviseurController() throws SQLException, Exception {
        filter=new MyFilter();
        dao=new MyDAO();
        ip=HandyManUtils.getIP();
        // ip=System.getenv("IP");
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            places=dao.select(connect, Place.class, new Place(0));
            for(Place p:places){
                p.setClasseHTML(p.getClasse());
            }
            rangees=Rangee.getRangees(connect, dao);
            for(Rangee r:rangees){
                r.getDispatchUtilisateursActuel(connect, dao);
                r.recupererPlaces(connect, dao);
            }
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
        if(utilisateur==null){
            return iris;
        }
        model.addAttribute(Constantes.VAR_LINKS, utilisateur.recupererLinks());
        model.addAttribute(Constantes.VAR_PLACES, places);
        model.addAttribute(Constantes.VAR_RANGEES, rangees);
        model.addAttribute(Constantes.VAR_RANGEEPLACES, rangeePlaces);
        model.addAttribute(Constantes.VAR_IP, ip);
        model.addAttribute(Constantes.VAR_INUTILISEEID, Constantes.INUTILISEE_ID);
        model.addAttribute(Constantes.VAR_OFFID, Constantes.OFF_ID);
        String[] urls=utilisateur.recupererResetCacheAndNotify();
        model.addAttribute(Constantes.VAR_RESETCACHE, urls[0]);
        model.addAttribute(Constantes.VAR_RECEIVENOTIFY, urls[1]);
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
            for(Rangee r:getRangees()){
                r.recupererPlaces(connect, dao);
            }
            return new RedirectView("/plan-de-table");
        }
    }
    @GetMapping("/attribution-de-roles")
    public Object attributionRole(HttpServletRequest req, Model model){
        HttpSession session=req.getSession();
        Utilisateur utilisateur=(Utilisateur)session.getAttribute(Constantes.VAR_SESSIONUTILISATEUR);
        Object iris=filter.checkByRole(utilisateur, Constantes.ROLE_SUPERVISEUR, "Makay - Attribution de role", "pages/superviseur/attribution-role", "layout/layout", model);
        if(utilisateur==null){
            return iris;
        }
        model.addAttribute(Constantes.VAR_UTILISATEURS, utilisateurs);
        model.addAttribute(Constantes.VAR_ROLES, roles);
        model.addAttribute(Constantes.VAR_ROLEUTILISATEURS, attributionRoles);
        String[] urls=utilisateur.recupererResetCacheAndNotify();
        model.addAttribute(Constantes.VAR_RESETCACHE, urls[0]);
        model.addAttribute(Constantes.VAR_RECEIVENOTIFY, urls[1]);
        model.addAttribute(Constantes.VAR_LINKS, utilisateur.recupererLinks());
        model.addAttribute(Constantes.VAR_IP, ip);
        model.addAttribute(Constantes.VAR_SESSIONUTILISATEUR, utilisateur);
        model.addAttribute(Constantes.VAR_SESSIONID, session.getId());
        model.addAttribute(Constantes.VAR_SUPERVISEURNUMERO, Constantes.ROLE_SUPERVISEUR);
        return iris;
    }

    @MessageMapping("/notify-redirect-superviseur")
    @SendTo("/notify/receive-notify-redirect-superviseur")
    public String notifierModifications(){
        return "reset cache";
    }
    @MessageMapping("/nouvelle-commande")
    @SendTo("/notify/recevoir-nouvelle-commande")
    public EnvoiCommandeREST nouvelleCommande(EnvoiCommandeREST commandes) throws Exception{
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            commandes.getCommande().setOuverture(LocalDateTime.now());
            commandes.getCommande().setUtilisateur(commandes.getUtilisateur());
            commandes.setCommandeFilles(commandes.getCommande().recupererCommandeFilles(connect, dao));
            commandes.getCommande().setPlace(dao.select(connect, Place.class, commandes.getCommande().getPlace())[0]);
        }
        return commandes;
    }

    @GetMapping("/reset-role-superviseur")
    public RedirectView resetRole(HttpServletRequest req) throws SQLException, Exception{
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            return filter.resetUserRole(req, connect, dao, new String[]{Constantes.ROLE_SUPERVISEUR});
        }
    }
    @GetMapping("/dispatch-tables-staff")
    public Object dispatchRangsStaff(HttpServletRequest req, Model model){
        HttpSession session=req.getSession();
        Utilisateur utilisateur=(Utilisateur)session.getAttribute(Constantes.VAR_SESSIONUTILISATEUR);
        Object iris=filter.checkByRole(utilisateur, Constantes.ROLE_SUPERVISEUR, "Makay - Dispatch des rangs de table au staff", "pages/superviseur/dispatch-tables-staff", "layout/layout", model);
        if(utilisateur==null){
            return iris;
        }
        model.addAttribute(Constantes.VAR_LINKS, utilisateur.recupererLinks());
        model.addAttribute(Constantes.VAR_RANGEES, rangees);
        model.addAttribute(Constantes.VAR_UTILISATEURS, utilisateurs);
        model.addAttribute(Constantes.VAR_SESSIONUTILISATEUR, utilisateur);
        model.addAttribute(Constantes.VAR_SESSIONID, session.getId());
        model.addAttribute(Constantes.VAR_IP, ip);
        model.addAttribute(Constantes.VAR_OFFID, Constantes.OFF_ID);
        String[] urls=utilisateur.recupererResetCacheAndNotify();
        model.addAttribute(Constantes.VAR_RESETCACHE, urls[0]);
        model.addAttribute(Constantes.VAR_RECEIVENOTIFY, urls[1]);
        return iris;
    }
    @PostMapping("/dispatch-tables-staff")
    @ResponseBody
    public ReponseREST mettreAJourDispatchStaff(@RequestBody RestData datas) throws Exception{
        ReponseREST response=new ReponseREST();
        ModificationDispatchREST modifs=HandyManUtils.fromJson(ModificationDispatchREST.class, datas.getRestdata());
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            response=filter.checkByRoleREST(modifs, connect, dao, new String[]{Constantes.ROLE_SUPERVISEUR});
            if(response.getCode()==Constantes.CODE_ERROR){
                return response;
            }
            modifs.getUtilisateur().mettreAJourDispatchStaff(connect, dao, modifs.getDispatchs());
            connect.commit();
            return response;
        }catch(Exception e){
            response.setCode(Constantes.CODE_ERROR);
            response.setMessage(e.getMessage());
            return response;
        }
    }
    @GetMapping("/monitoring-des-serveurs")
    public Object monitoringDesServeurs(HttpServletRequest req, Model model) throws Exception{
        HttpSession session=req.getSession();
        Utilisateur utilisateur=(Utilisateur)session.getAttribute(Constantes.VAR_SESSIONUTILISATEUR);
        Object iris=filter.checkByRole(utilisateur, Constantes.ROLE_SUPERVISEUR, "Makay - Monitoring", "pages/superviseur/monitoring-des-serveurs", "layout/layout", model);
        if(utilisateur==null){
            return iris;
        }
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            model.addAttribute(Constantes.VAR_SERVEURS, Utilisateur.recupererServeursEnCours(connect, dao));
        }
        model.addAttribute(Constantes.VAR_LINKS, utilisateur.recupererLinks());
        model.addAttribute(Constantes.VAR_IP, ip);
        String[] urls=utilisateur.recupererResetCacheAndNotify();
        model.addAttribute(Constantes.VAR_RESETCACHE, urls[0]);
        model.addAttribute(Constantes.VAR_RECEIVENOTIFY, urls[1]);
        model.addAttribute(Constantes.VAR_COMMANDEFILLE_OFFERT, Constantes.COMMANDEFILLE_OFFERT);
        model.addAttribute(Constantes.VAR_COMMANDEFILLE_ANNULEE, Constantes.COMMANDEFILLE_ANNULEE);
        model.addAttribute(Constantes.VAR_SESSIONUTILISATEUR, utilisateur);
        model.addAttribute(Constantes.VAR_SESSIONID, session.getId());
        return iris;
    }

    @GetMapping("/reset-cache-superviseur")
    public RedirectView resetCacheRangees(HttpServletRequest req) throws SQLException, Exception{
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            places=dao.select(connect, Place.class, new Place(0));
            for(Place p:places){
                p.setClasseHTML(p.getClasse());
            }
            rangees=Rangee.getRangees(connect, dao);
            for(Rangee r:rangees){
                r.getDispatchUtilisateursActuel(connect, dao);
                r.recupererPlaces(connect, dao);
            }
            rangeePlaces=RangeePlace.getArrangementActuel(connect, dao);
            utilisateurs=dao.select(connect, UtilisateurSafe.class);
            roles=dao.select(connect, Role.class, new Role(0));
            attributionRoles=HistoriqueRoleUtilisateur.getRolesActuels(connect, dao);
        }
        return resetRole(req);
    }
    @MessageMapping("/modifier-commande")
    @SendTo("/notify/recevoir-modifier-commande")
    public EnvoiCommandeREST modifierCommande(EnvoiCommandeREST commandes) throws Exception{
        return commandes;
    }
    @PostMapping("/action-superviseur")
    @ResponseBody
    public ReponseREST actionSuperviseur(@RequestBody RestData datas){
        ReponseREST response=new ReponseREST();
        ActionSuperviseurREST modifs=HandyManUtils.fromJson(ActionSuperviseurREST.class, datas.getRestdata());
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            response=filter.checkByRoleREST(modifs, connect, dao, new String[]{Constantes.ROLE_SUPERVISEUR});
            if(response.getCode()==Constantes.CODE_ERROR){
                return response;
            }
            Object[] nouvelEtat=modifs.getUtilisateur().actionSuperviseur(connect, dao, modifs.getActionSuperviseur());
            connect.commit();
            response.addItem("etat", ((CommandeFille)nouvelEtat[0]).recupererEtatString());
            response.addItem("couleur", ((CommandeFille)nouvelEtat[0]).recupererEtatCouleur());
            response.addItem("nouveauMontant", nouvelEtat[1]);
            return response;
        }catch(Exception e){
            response.setCode(Constantes.CODE_ERROR);
            response.setMessage(e.getMessage());
            return response;
        }
    }
}
