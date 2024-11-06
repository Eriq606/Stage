package com.app.makay.controllers.metier.serveur;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import com.app.makay.entites.Commande;
import com.app.makay.entites.CommandeEnCours;
import com.app.makay.entites.CommandeFilleEnCours;
import com.app.makay.entites.ModePaiement;
import com.app.makay.entites.Place;
import com.app.makay.entites.Produit;
import com.app.makay.entites.Utilisateur;
import com.app.makay.entites.REST.CheckCommandeFilleREST;
import com.app.makay.entites.REST.DemandeAdditionREST;
import com.app.makay.entites.REST.EnvoiCommandeREST;
import com.app.makay.entites.REST.RechercheProduitREST;
import com.app.makay.utilitaire.Constantes;
import com.app.makay.utilitaire.MyDAO;
import com.app.makay.utilitaire.MyFilter;
import com.app.makay.utilitaire.ReponseREST;
import com.app.makay.utilitaire.RestData;
import com.app.makay.utilitaire.exception.StockException;

import handyman.HandyManUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import veda.godao.utils.DAOConnexion;

@Controller
public class ServeurController {
    private MyFilter filter;
    private Produit[] produits;
    private String ip;
    private MyDAO dao;
    private Place[] places;
    @Autowired
    private ResourceLoader loader;
    public ServeurController() throws SQLException, Exception{
        filter=new MyFilter();
        dao=new MyDAO();
        ip=HandyManUtils.getIP();
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            Produit whereProduit=new Produit(0);
            produits=dao.select(connect, Produit.class, whereProduit);
            for(Produit p:produits){
                p.setAccompagnements(p.recupererAllAccompagnements(connect, dao));
            }
            Place where=new Place();
            where.setEtat(0);
            places=dao.select(connect, Place.class, where);
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
        }catch(StockException se){
            response.setCode(Constantes.CODE_ERROR);
            String message="";
            for(Exception e:se.getExceptions()){
                message+="<p>"+e.getMessage()+"</p>";
            }
            response.setMessage(message);
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
        String queryCount="select count(*) from v_commandes where idutilisateur=%s and etat<10";
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
        model.addAttribute(Constantes.VAR_NOTIF_PATH, Constantes.SND_NOTIFICATION);
        model.addAttribute(Constantes.VAR_INDICE_PAGINATION, indice_actu_controller);
        model.addAttribute(Constantes.VAR_TABLE, table);
        model.addAttribute(Constantes.VAR_LINKS, utilisateur.recupererLinks());
        model.addAttribute(Constantes.VAR_PLACES, places);
        model.addAttribute(Constantes.VAR_IP, ip);
        model.addAttribute(Constantes.VAR_SESSIONUTILISATEUR, utilisateur);
        model.addAttribute(Constantes.VAR_SESSIONID, session.getId());
        String[] urls=utilisateur.recupererResetCacheAndNotify();
        model.addAttribute(Constantes.VAR_RESETCACHE, urls[0]);
        model.addAttribute(Constantes.VAR_RECEIVENOTIFY, urls[1]);
        return iris;
    }

    @MessageMapping("/notify-redirect-serveur")
    @SendTo("/notify/receive-notify-redirect-serveur")
    public String notifierModifications(){
        return "reset cache";
    }

    @GetMapping("/reset-role-serveur")
    public RedirectView resetCacheRoles(HttpServletRequest req) throws Exception{
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            return filter.resetUserRole(req, connect, dao, new String[]{Constantes.ROLE_SERVEUR, Constantes.ROLE_BAR, Constantes.ROLE_CAISSE, Constantes.ROLE_CUISINIER, Constantes.ROLE_OFF, Constantes.ROLE_SUPERVISEUR});
        }
    }

    @GetMapping("/reset-cache-serveur")
    public RedirectView resetCacheProduits(HttpServletRequest req) throws Exception{
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            Produit whereProduit=new Produit(0);
            produits=dao.select(connect, Produit.class, whereProduit);
            for(Produit p:produits){
                p.setAccompagnements(p.recupererAllAccompagnements(connect, dao));
            }
            Place where=new Place();
            where.setEtat(0);
            places=dao.select(connect, Place.class, where);
        }
        return resetCacheRoles(req);
    }
    @GetMapping("/actualiser-stock")
    public RedirectView actualiserEtatStock(HttpServletRequest req) throws SQLException, Exception{
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            double stock;
            for(Produit p:produits){
                stock=p.recupererStockRestant(connect, dao);
                p.setDernierStock(stock);
            }
            return filter.resetUserRole(req, connect, dao, new String[]{Constantes.ROLE_BAR, Constantes.ROLE_CUISINIER, Constantes.ROLE_SERVEUR, Constantes.ROLE_SUPERVISEUR});
        }
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
            String[] urls=utilisateur.recupererResetCacheAndNotify();
            model.addAttribute(Constantes.VAR_RESETCACHE, urls[0]);
            model.addAttribute(Constantes.VAR_RECEIVENOTIFY, urls[1]);
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
            Integer[] ids=modifs.getUtilisateur().modifierCommande(connect, dao, modifs.getCommande(), modifs.getCommandeFilles());
            connect.commit();
            response.addItem("ids", ids);
            return response;
        }catch(StockException se){
            response.setCode(Constantes.CODE_ERROR);
            String message="";
            for(Exception e:se.getExceptions()){
                message+="<p>"+e.getMessage()+"</p>";
            }
            response.setMessage(message);
            return response;
        }catch(Exception e){
            response.setCode(Constantes.CODE_ERROR);
            response.setMessage(e.getMessage());
            return response;
        }
    }
    @GetMapping("/historique-de-commande")
    public Object historiqueCommandes(HttpServletRequest req, Model model, Integer indice_actu, String serveur, String table, String ouvertureDebut, String ouvertureFin, String clotureDebut, String clotureFin,
                                      String montantDebut, String montantFin, String montantOffertDebut, String montantOffertFin, String montantAnnuleDebut, String montantAnnuleFin
                                      , String[] modepaiement, String produit, String accompagnement, String notes) throws SQLException, Exception{
        HttpSession session=req.getSession();
        Utilisateur utilisateur=(Utilisateur)session.getAttribute(Constantes.VAR_SESSIONUTILISATEUR);
        Object iris=filter.checkByRole(utilisateur,
                                        new String[]{Constantes.ROLE_SERVEUR,Constantes.ROLE_BAR,Constantes.ROLE_CUISINIER,Constantes.ROLE_SUPERVISEUR,Constantes.ROLE_CAISSE,Constantes.ROLE_ANALYSTE},
                                        "Makay - Historique des commandes", "pages/serveur/historique-des-commandes", "layout/layout", model);
        if(utilisateur==null){
            return iris;
        }
        int indice_actu_controller=1;
        if(indice_actu!=null){
            indice_actu_controller=indice_actu;
        }
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            CommandeEnCours[] commandes=utilisateur.recupererHistoriqueCommande(
                connect, dao, indice_actu_controller, serveur, table!=null?table.trim():table, ouvertureDebut, ouvertureFin, clotureDebut, clotureFin,
                montantDebut!=null?montantDebut.trim():montantDebut, montantFin!=null?montantFin.trim():montantFin, montantOffertDebut!=null?montantOffertDebut.trim():montantOffertDebut,
                montantOffertFin!=null?montantOffertFin.trim():montantOffertFin, montantAnnuleDebut!=null?montantAnnuleDebut.trim():montantAnnuleDebut,
                montantAnnuleFin!=null?montantAnnuleFin.trim():montantAnnuleFin,
                modepaiement, produit!=null?produit.trim():produit, accompagnement!=null?accompagnement.trim():accompagnement, notes!=null?notes.trim():notes);
            int countCommandes=utilisateur.recupererCountHistoriqueCommande(
                connect, dao, indice_actu_controller, serveur, table!=null?table.trim():table, ouvertureDebut, ouvertureFin, clotureDebut, clotureFin,
                montantDebut!=null?montantDebut.trim():montantDebut, montantFin!=null?montantFin.trim():montantFin, montantOffertDebut!=null?montantOffertDebut.trim():montantOffertDebut,
                montantOffertFin!=null?montantOffertFin.trim():montantOffertFin, montantAnnuleDebut!=null?montantAnnuleDebut.trim():montantAnnuleDebut,
                montantAnnuleFin!=null?montantAnnuleFin.trim():montantAnnuleFin,
                modepaiement, produit!=null?produit.trim():produit, accompagnement!=null?accompagnement.trim():accompagnement, notes!=null?notes.trim():notes);
            ModePaiement[] modePaiements=dao.select(connect, ModePaiement.class);
            Utilisateur[] serveurs=Utilisateur.recupererServeursHistorique(connect, dao);
            HashMap<String, Object> pagination=HandyManUtils.paginate(countCommandes, Constantes.PAGINATION_LIMIT, indice_actu_controller);
            for(Map.Entry<String, Object> m:pagination.entrySet()){
                model.addAttribute(m.getKey(), m.getValue());
            }
            model.addAttribute(Constantes.VAR_UTILISATEURS, serveurs);
            model.addAttribute(Constantes.VAR_MODEPAIEMENTS, modePaiements);
            model.addAttribute(Constantes.VAR_MODEPAIEMENTS_CHOISIS, modepaiement!=null?modepaiement:new String[0]);
            model.addAttribute(Constantes.VAR_COMMANDES, commandes);
        }
        String queryString="";
        if(req.getQueryString()!=null){
            queryString=req.getQueryString();
            queryString=queryString.replaceAll("indice_actu=\\d&", "");
        }
        model.addAttribute(Constantes.VAR_QUERYSTRING, queryString);
        model.addAttribute(Constantes.VAR_LINKS, utilisateur.recupererLinks());
        model.addAttribute(Constantes.VAR_IP, ip);
        String[] urls=utilisateur.recupererResetCacheAndNotify();
        model.addAttribute(Constantes.VAR_RESETCACHE, urls[0]);
        model.addAttribute(Constantes.VAR_RECEIVENOTIFY, urls[1]);
        return iris;
    }

    /*
     * Pour demander l'addition d'une commande, on change l'état de la commande en 10 (ADDITION)
     * On envoie donc l'id de la commande par méthode POST
     * 
     * demandeAddition(idcommande, utilisateur, idsession){
     *      verifierSessionExiste(idsession, utilisateur.id)
     *      verifierSessionNonExpiree(idsession, utilisateur.id)
     *      verifierSiAutorise(utilisateur)
     *      update(etat: 10, where: idcommande=[idcommande])
     * }
     */
    @PostMapping("/demande-addition")
    @ResponseBody
    public ReponseREST demandeAddition(@RequestBody RestData datas){
        ReponseREST response=new ReponseREST();
        DemandeAdditionREST modifs=HandyManUtils.fromJson(DemandeAdditionREST.class, datas.getRestdata());
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            response=filter.checkByRoleREST(modifs, connect, dao, new String[]{Constantes.ROLE_SERVEUR, Constantes.ROLE_BAR});
            if(response.getCode()==Constantes.CODE_ERROR){
                return response;
            }
            modifs.getUtilisateur().demandeAddition(connect, dao, modifs.getCommande());
            connect.commit();
            return response;
        }catch(Exception e){
            response.setCode(Constantes.CODE_ERROR);
            response.setMessage(e.getMessage());
            return response;
        }
    }
    @GetMapping("/exporter")
    public void exporter(String idcommande, HttpServletResponse response) throws SQLException, Exception{
        Resource htmlresource=loader.getResource("classpath:static/htmlpdf/commande.html");
        Resource fauxresource=loader.getResource("classpath:static/htmlpdf/faux.html");
        File html=htmlresource.getFile();
        File faux=fauxresource.getFile();
        Commande where=new Commande();
        where.setId(Integer.parseInt(idcommande));
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            Commande commande=dao.select(connect, Commande.class, where)[0];
            commande.recupererCommandeFilles(connect, dao);
            commande.recupererPaiements(connect, dao);
            commande.recupererActionsSuperviseurs(connect, dao);
            commande.recupererActionsTotales();
            commande.recupererPaiementTotal();
            commande.recupererRemises(connect, dao);
            String newHTML=commande.formatterHTML(connect, dao, html);
            HandyManUtils.overwriteFileContent(faux, newHTML);
            File pdf=HandyManUtils.generatePDF(faux, "commande-"+commande.getOuverture().toString().replace("T", "--").replace(":", "-").replace(".", "-")+".pdf");
            response.setHeader("Content-Disposition", "attachment; filename=" + pdf.getName());
            try(InputStream is=new FileInputStream(pdf);OutputStream os=response.getOutputStream()){
                os.write(is.readAllBytes());
            }
        }
    }
    @PostMapping("/recherche-produit")
    @ResponseBody
    public ReponseREST rechercheProduits(@RequestBody RestData datas){
        ReponseREST reponse=new ReponseREST();
        RechercheProduitREST recherche=HandyManUtils.fromJson(RechercheProduitREST.class, datas.getRestdata());
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            reponse=filter.checkByRoleREST(recherche, connect, dao, new String[]{Constantes.ROLE_BAR, Constantes.ROLE_CUISINIER, Constantes.ROLE_SUPERVISEUR});
            if(reponse.getCode()==Constantes.CODE_ERROR){
                return reponse;
            }
            Produit[] produits=recherche.getUtilisateur().rechercheProduit(connect, dao, recherche.getProduit().getNom());
            reponse.addItem("produits", produits);
            return reponse;
        }catch(Exception e){
            reponse.setCode(Constantes.CODE_ERROR);
            reponse.setMessage(e.getMessage());
            return reponse;
        }
    }
    @MessageMapping("/check-commandefille")
    @SendTo("/notify/recevoir-check-commandefille")
    public CheckCommandeFilleREST notifierCheck(CheckCommandeFilleREST check){
        return check;
    }
    @MessageMapping("/demande-addition")
    @SendTo("/notify/recevoir-demande-addition")
    public DemandeAdditionREST demandeAddition(DemandeAdditionREST addition) throws SQLException, Exception{
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            addition.setCommande(dao.select(connect, Commande.class, addition.getCommande())[0]);
            addition.getCommande().recupererCommandeFilles(connect, dao);
        }
        return addition;
    }
    @GetMapping("/actualiser-cache-serveur")
    public void actualiserCache() throws SQLException, Exception{
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            Produit whereProduit=new Produit(0);
            produits=dao.select(connect, Produit.class, whereProduit);
            for(Produit p:produits){
                p.setAccompagnements(p.recupererAllAccompagnements(connect, dao));
            }
            Place where=new Place();
            where.setEtat(0);
            places=dao.select(connect, Place.class, where);
        }
    }
}
