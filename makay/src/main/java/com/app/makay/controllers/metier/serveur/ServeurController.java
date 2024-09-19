package com.app.makay.controllers.metier.serveur;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
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
import com.app.makay.entites.CommandeFilleEnCours;
import com.app.makay.entites.Produit;
import com.app.makay.entites.Role;
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
            Utilisateur utilisateur=new Utilisateur();
            Role role=new Role();
            role.setNumero(Constantes.ROLE_SERVEUR);
            role=dao.select(connect, Role.class, role)[0];
            utilisateur.setRole(role);
            produits=utilisateur.recupererProduitsCorrespondant(connect, dao);
            for(Produit p:produits){
                p.setAccompagnements(p.getAllAccompagnements(connect, dao));
            }
        }
    }

    @GetMapping("/serveur-passer-commande")
    public Object passerCommande(HttpServletRequest req, Model model){
        HttpSession session=req.getSession();
        Utilisateur utilisateur=(Utilisateur)session.getAttribute(Constantes.VAR_SESSIONUTILISATEUR);
        Object iris=filter.checkByRole(utilisateur, new String[]{Constantes.ROLE_SERVEUR, Constantes.ROLE_BAR, Constantes.ROLE_SUPERVISEUR}, "Makay - Passer une commande", "pages/serveur/prise-commande", "layout/layout", model);
        model.addAttribute(Constantes.VAR_PRODUITS, produits);
        model.addAttribute(Constantes.VAR_LINKS, utilisateur.getLinks());
        model.addAttribute(Constantes.VAR_IP, ip);
        model.addAttribute(Constantes.VAR_SESSIONUTILISATEUR, utilisateur);
        model.addAttribute(Constantes.VAR_SESSIONID, session.getId());
        String[] urls=utilisateur.getResetCacheAndNotify();
        model.addAttribute(Constantes.VAR_RESETCACHE, urls[0]);
        model.addAttribute(Constantes.VAR_RECEIVENOTIFY, urls[1]);
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
                response.setCode(Constantes.CODE_ERROR);
                response.setMessage(Constantes.MSG_UTILISATEUR_NON_AUTHENTIFIE);
                return response;
            }
            if(sessionUser[0].getExpiration().isBefore(LocalDateTime.now())){
                response.setCode(Constantes.CODE_ERROR);
                response.setMessage(Constantes.MSG_SESSION_EXPIREE);
                return response;
            }
            String[] authorized={Constantes.ROLE_SERVEUR, Constantes.ROLE_BAR};
            if(Arrays.asList(authorized).contains(sessionUser[0].getUtilisateur().getRole().getNumero())==false){
                response.setCode(Constantes.CODE_ERROR);
                response.setMessage(Constantes.MSG_NON_AUTHORISE);
                return response;
            }
            int idcommande=modifs.getUtilisateur().passerCommande(connect, dao, modifs.getCommande(), modifs.getCommandeFilles());
            connect.commit();
            response.setCode(Constantes.CODE_SUCCESS);
            response.setMessage(Constantes.MSG_SUCCES);
            response.addItem("idcommande", String.valueOf(idcommande));
            return response;
        }
    }
    @GetMapping("/liste-commande")
    public Object listeCommande(HttpServletRequest req, Model model, Integer indice_actu, String table) throws SQLException, Exception{
        HttpSession session=req.getSession();
        Utilisateur utilisateur=(Utilisateur)session.getAttribute(Constantes.VAR_SESSIONUTILISATEUR);
        Object iris=filter.checkByRole(utilisateur, new String[]{Constantes.ROLE_SERVEUR, Constantes.ROLE_BAR, Constantes.ROLE_SUPERVISEUR}, "Makay - Mes commandes", "pages/serveur/liste-commande", "layout/layout", model);
        if(utilisateur==null){
            return iris;
        }
        String queryCount="select count(*) from v_commandes where id in (select idcommande from v_commandefille_produits where idcategorie in (select idcategorie from v_role_categorie_produits_checkings where idrole=%s) group by idcommande)";
        queryCount=String.format(queryCount, utilisateur.getRole().getId());
        if(table!=null){
            table=table.trim();
            queryCount+=" and nom_place='%s'";
            queryCount=String.format(queryCount, table);
        }
        int indice_actu_controller=1;
        if(indice_actu!=null){
            indice_actu_controller=indice_actu;
        }
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            CommandeEnCours[] commandes=utilisateur.recupererCommandesCorrespondantes(connect, dao, (indice_actu_controller-1)*Constantes.PAGINATION_LIMIT, table);
            model.addAttribute(Constantes.VAR_COMMANDES, commandes);
            // put("indice_premier", indice_premier);
            // put("indice_precedent", indice_precedent);
            // put("indice_suivant", indice_suivant);
            // put("indice_dernier", indice_dernier);
            // put("bouton_precedent", bouton_precedent);
            // put("bouton_suivant", bouton_suivant);
            // response.put("indice_actu", indice_actu);
            HashMap<String, Object> pagination=dao.paginate(connect, queryCount, Constantes.PAGINATION_LIMIT, indice_actu_controller);
            for (Map.Entry<String, Object> entry : pagination.entrySet()) {
                model.addAttribute(entry.getKey(), entry.getValue());
            }
        }
        model.addAttribute(Constantes.VAR_INDICE_PAGINATION, indice_actu_controller);
        model.addAttribute(Constantes.VAR_TABLE, table);
        model.addAttribute(Constantes.VAR_LINKS, utilisateur.getLinks());
        model.addAttribute(Constantes.VAR_PLACES, utilisateur.getPlaces());
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
            Utilisateur utilisateur=new Utilisateur();
            Role role=new Role();
            role.setNumero(Constantes.ROLE_SERVEUR);
            role=dao.select(connect, Role.class, role)[0];
            utilisateur.setRole(role);
            produits=utilisateur.recupererProduitsCorrespondant(connect, dao);
            for(Produit p:produits){
                p.setAccompagnements(p.getAllAccompagnements(connect, dao));
            }
        }
        return resetCacheRoles(req);
    }

    @GetMapping("/modifier-commande")
    public Object modifierCommande(HttpServletRequest req, Model model, Integer idcommande, Integer indice_pagination, String table) throws Exception{
        HttpSession session=req.getSession();
        Utilisateur utilisateur=(Utilisateur)session.getAttribute(Constantes.VAR_SESSIONUTILISATEUR);
        Object iris=filter.checkByRole(utilisateur, new String[]{Constantes.ROLE_SERVEUR, Constantes.ROLE_BAR}, "Makay - Modification de commande", "pages/serveur/modifier-commande", "layout/layout", model);
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            CommandeEnCours where=new CommandeEnCours();
            where.setId(idcommande);
            CommandeEnCours commande=dao.select(connect, CommandeEnCours.class, where)[0];
            CommandeFilleEnCours[] commandeFilles=commande.recupererCommandeFillesWithoutSet(connect, dao, utilisateur.getRole());
            model.addAttribute(Constantes.VAR_COMMANDE, commande);
            model.addAttribute(Constantes.VAR_COMMANDESFILLES, commandeFilles);
            model.addAttribute(Constantes.VAR_LINKS, utilisateur.getLinks());
            model.addAttribute(Constantes.VAR_PRODUITS, produits);
            model.addAttribute(Constantes.VAR_SESSIONUTILISATEUR, utilisateur);
            model.addAttribute(Constantes.VAR_SESSIONID, session.getId());
            model.addAttribute(Constantes.VAR_IP, ip);
            model.addAttribute(Constantes.VAR_INDICE_PAGINATION, indice_pagination);
            model.addAttribute(Constantes.VAR_TABLE, table);
            return iris;
        }
    }
    @PostMapping("/modifier-commande")
    @ResponseBody
    public ReponseREST modifierCommande(@RequestBody RestData datas) throws SQLException, Exception{
        ReponseREST response=new ReponseREST();
        EnvoiCommandeREST modifs=HandyManUtils.fromJson(EnvoiCommandeREST.class, datas.getRestdata());
        SessionUtilisateur where=new SessionUtilisateur();
        where.setSessionId(modifs.getSessionid());
        where.setUtilisateur(modifs.getUtilisateur());
        where.setEstValide(Constantes.SESSION_ESTVALIDE);
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            SessionUtilisateur[] sessionUser=dao.select(connect, SessionUtilisateur.class, where);
            if(sessionUser.length!=1){
                response.setCode(Constantes.CODE_ERROR);
                response.setMessage(Constantes.MSG_UTILISATEUR_NON_AUTHENTIFIE);
                return response;
            }
            if(sessionUser[0].getExpiration().isBefore(LocalDateTime.now())){
                response.setCode(Constantes.CODE_ERROR);
                response.setMessage(Constantes.MSG_SESSION_EXPIREE);
                return response;
            }
            String[] authorized={Constantes.ROLE_SERVEUR, Constantes.ROLE_BAR};
            if(Arrays.asList(authorized).contains(sessionUser[0].getUtilisateur().getRole().getNumero())==false){
                response.setCode(Constantes.CODE_ERROR);
                response.setMessage(Constantes.MSG_NON_AUTHORISE);
                return response;
            }
            modifs.getUtilisateur().modifierCommande(connect, dao, modifs.getCommande(), modifs.getCommandeFilles());
            connect.commit();
            response.setCode(Constantes.CODE_SUCCESS);
            response.setMessage(Constantes.MSG_SUCCES);
            return response;
        }
    }
}
