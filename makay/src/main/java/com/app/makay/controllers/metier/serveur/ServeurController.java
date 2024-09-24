package com.app.makay.controllers.metier.serveur;

import java.sql.Connection;
import java.sql.SQLException;
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
import com.app.makay.entites.ModePaiement;
import com.app.makay.entites.Produit;
import com.app.makay.entites.Role;
import com.app.makay.entites.Utilisateur;
import com.app.makay.entites.REST.EnvoiCommandeREST;
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
public class ServeurController {
    private MyFilter filter;
    private Produit[] produits;
    private String ip;
    private MyDAO dao;

    public ServeurController() throws SQLException, Exception{
        filter=new MyFilter();
        dao=new MyDAO();
        // ip=HandyManUtils.getIP();
        ip=System.getenv("IP");
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
        if(utilisateur==null){
            return iris;
        }
        model.addAttribute(Constantes.VAR_PRODUITS, produits);
        model.addAttribute(Constantes.VAR_LINKS, utilisateur.recupererLinks());
        model.addAttribute(Constantes.VAR_IP, ip);
        model.addAttribute(Constantes.VAR_SESSIONUTILISATEUR, utilisateur);
        model.addAttribute(Constantes.VAR_SESSIONID, session.getId());
        String[] urls=utilisateur.recupererResetCacheAndNotify();
        model.addAttribute(Constantes.VAR_RESETCACHE, urls[0]);
        model.addAttribute(Constantes.VAR_RECEIVENOTIFY, urls[1]);
        model.addAttribute(Constantes.VAR_PLACES, utilisateur.getPlaces());
        return iris;
    }
    @PostMapping("/serveur-passer-commande")
    @ResponseBody
    public ReponseREST passerCommande(@RequestBody RestData datas) throws SQLException, Exception{
        ReponseREST response=new ReponseREST();
        EnvoiCommandeREST modifs=HandyManUtils.fromJson(EnvoiCommandeREST.class, datas.getRestdata());
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            response=filter.checkByRoleREST(modifs, connect, dao, new String[]{Constantes.ROLE_SERVEUR, Constantes.ROLE_BAR});
            if(response.getCode()==Constantes.CODE_ERROR){
                return response;
            }
            int idcommande=modifs.getUtilisateur().passerCommande(connect, dao, modifs.getCommande(), modifs.getCommandeFilles());
            connect.commit();
            response.addItem("idcommande", String.valueOf(idcommande));
            return response;
        }catch(Exception e){
            response.setCode(Constantes.CODE_ERROR);
            response.setMessage(e.getMessage());
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
        String queryCount="select count(*) from v_commandes where idutilisateur=%s and etat<20";
        queryCount=String.format(queryCount, utilisateur.getId());
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
            HashMap<String, Object> pagination=dao.paginate(connect, queryCount, Constantes.PAGINATION_LIMIT, indice_actu_controller);
            for (Map.Entry<String, Object> entry : pagination.entrySet()) {
                model.addAttribute(entry.getKey(), entry.getValue());
            }
        }
        model.addAttribute(Constantes.VAR_INDICE_PAGINATION, indice_actu_controller);
        model.addAttribute(Constantes.VAR_TABLE, table);
        model.addAttribute(Constantes.VAR_LINKS, utilisateur.recupererLinks());
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
        if(utilisateur==null){
            return iris;
        }
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            CommandeEnCours where=new CommandeEnCours();
            where.setId(idcommande);
            CommandeEnCours commande=dao.select(connect, CommandeEnCours.class, where)[0];
            CommandeFilleEnCours[] commandeFilles=commande.recupererCommandeFillesWithoutSet(connect, dao);
            model.addAttribute(Constantes.VAR_COMMANDE, commande);
            model.addAttribute(Constantes.VAR_COMMANDESFILLES, commandeFilles);
            model.addAttribute(Constantes.VAR_LINKS, utilisateur.recupererLinks());
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
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            response=filter.checkByRoleREST(modifs, connect, dao, new String[]{Constantes.ROLE_SERVEUR, Constantes.ROLE_BAR});
            if(response.getCode()==Constantes.CODE_ERROR){
                return response;
            }
            modifs.getUtilisateur().modifierCommande(connect, dao, modifs.getCommande(), modifs.getCommandeFilles());
            connect.commit();
            return response;
        }catch(Exception e){
            response.setCode(Constantes.CODE_ERROR);
            response.setMessage(e.getMessage());
            return response;
        }
    }
    @GetMapping("/historique-de-commande")
    public Object historiqueCommandes(HttpServletRequest req, Model model, Integer indice_actu) throws SQLException, Exception{
        HttpSession session=req.getSession();
        Utilisateur utilisateur=(Utilisateur)session.getAttribute(Constantes.VAR_SESSIONUTILISATEUR);
        Object iris=filter.checkByRole(utilisateur,
                                        new String[]{Constantes.ROLE_SERVEUR,Constantes.ROLE_BAR,Constantes.ROLE_CUISINIER,Constantes.ROLE_SUPERVISEUR,Constantes.ROLE_CAISSE},
                                        "Makay - Historique des commandes", "pages/serveur/historique-des-commandes", "layout/layout", model);
        if(utilisateur==null){
            return iris;
        }
        int indice_actu_controller=1;
        if(indice_actu!=null){
            indice_actu_controller=indice_actu;
        }
        String query="select count(*) from v_commandes where etat>10 and etat<40 limit %s";
        query=String.format(query, Constantes.PAGINATION_LIMIT);
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            CommandeEnCours[] commandes=utilisateur.recupererHistoriqueCommande(connect, dao, indice_actu_controller);
            ModePaiement[] modePaiements=dao.select(connect, ModePaiement.class);
            HashMap<String, Object> pagination=dao.paginate(connect, query, Constantes.PAGINATION_LIMIT, indice_actu_controller);
            for(Map.Entry<String, Object> m:pagination.entrySet()){
                model.addAttribute(m.getKey(), m.getValue());
            }
            model.addAttribute(Constantes.VAR_MODEPAIEMENTS, modePaiements);
            model.addAttribute(Constantes.VAR_COMMANDES, commandes);
        }
        model.addAttribute(Constantes.VAR_LINKS, utilisateur.recupererLinks());
        return iris;
    }
}
