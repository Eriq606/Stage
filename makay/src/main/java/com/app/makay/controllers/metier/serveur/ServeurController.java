package com.app.makay.controllers.metier.serveur;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import com.app.makay.entites.CommandeEnCours;
import com.app.makay.entites.Produit;
import com.app.makay.entites.Utilisateur;
import com.app.makay.entites.REST.EnvoiCommandeREST;
import com.app.makay.utilitaire.Constantes;
import com.app.makay.utilitaire.MyDAO;
import com.app.makay.utilitaire.MyFilter;
import com.app.makay.utilitaire.ReponseREST;
import com.app.makay.utilitaire.RestData;
import com.app.makay.utilitaire.SessionUtilisateur;

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
    @PostMapping("/serveur-passer-commande")
    @ResponseBody
    public ReponseREST passerCommande(@RequestBody RestData datas) throws SQLException, Exception{
        ReponseREST response=new ReponseREST();
        EnvoiCommandeREST modifs=HandyManUtils.fromJson(EnvoiCommandeREST.class, datas.getRestdata());
        SessionUtilisateur where=new SessionUtilisateur();
        where.setSessionId(modifs.getSessionid());
        where.setUtilisateur(modifs.getUtilisateur());
        where.setEstValide(Constantes.SESSION_ESTVALIDE);
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            SessionUtilisateur[] sessionUser=dao.select(connect, SessionUtilisateur.class, where);
            if(sessionUser.length!=1){
                response.setMessage(Constantes.MSG_UTILISATEUR_NON_AUTHENTIFIE);
                return response;
            }
            if(sessionUser[0].getExpiration().isBefore(LocalDateTime.now())){
                response.setMessage(Constantes.MSG_SESSION_EXPIREE);
                return response;
            }
            modifs.getUtilisateur().passerCommande(connect, dao, modifs.getCommande(), modifs.getCommandeFilles());
            connect.commit();
            response.setMessage(Constantes.MSG_SUCCES);
            return response;
        }
    }
    @GetMapping("/liste-commande")
    public Object listeCommande(HttpServletRequest req, Model model, Integer indice_actu) throws SQLException, Exception{
        HttpSession session=req.getSession();
        Utilisateur utilisateur=(Utilisateur)session.getAttribute(Constantes.VAR_SESSIONUTILISATEUR);
        Object iris=filter.checkByRole(utilisateur, Constantes.ROLE_SERVEUR, "Makay - Liste des commandes", "pages/serveur/liste-commande", "layout/layout", model);
        CommandeEnCours where=new CommandeEnCours();
        where.setUtilisateur(utilisateur);
        int indice_actu_controller=1;
        if(indice_actu!=null){
            indice_actu_controller=indice_actu;
        }
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            CommandeEnCours[] commandes=utilisateur.getCommandesEnCours(connect, dao, (indice_actu_controller-1)*Constantes.PAGINATION_LIMIT);
            model.addAttribute(Constantes.VAR_COMMANDES, commandes);
            //     put("indice_premier", indice_premier);
            //     put("indice_precedent", indice_precedent);
            //     put("indice_suivant", indice_suivant);
            //     put("indice_dernier", indice_dernier);
            //     put("bouton_precedent", bouton_precedent);
            //     put("bouton_suivant", bouton_suivant);
            // }};
            // response.put("indice_actu", indice_actu);
            HashMap<String, Object> pagination=dao.paginate(connect, CommandeEnCours.class, where, Constantes.PAGINATION_LIMIT, indice_actu);
            for (Map.Entry<String, Object> entry : pagination.entrySet()) {
                model.addAttribute(entry.getKey(), entry.getValue());
            }
        }
        model.addAttribute(Constantes.VAR_LINKS, Constantes.LINK_SERVEUR);
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
