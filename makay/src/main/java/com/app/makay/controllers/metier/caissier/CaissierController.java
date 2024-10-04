package com.app.makay.controllers.metier.caissier;

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
import com.app.makay.entites.ModePaiement;
import com.app.makay.entites.Place;
import com.app.makay.entites.Utilisateur;
import com.app.makay.entites.REST.PayerCommandeREST;
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
public class CaissierController {
    private MyFilter filter;
    private MyDAO dao;
    private String ip;
    private Place[] places;
    private ModePaiement[] modePaiements;
    public CaissierController() throws SQLException, Exception {
        filter=new MyFilter();
        dao=new MyDAO();
        ip=HandyManUtils.getIP();
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            Place where=new Place(0);
            places=dao.select(connect, Place.class, where);
            ModePaiement wherePaiement=new ModePaiement();
            wherePaiement.setEtat(0);
            modePaiements=dao.select(connect, ModePaiement.class, wherePaiement);
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
        String countQuery="select count(*) from v_commandes where etat=10 and reste_a_payer>0";
        if(table!=null){
            countQuery+=" and nom_table='%s'";
            countQuery=String.format(countQuery, table);
        }
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            CommandeEnCours[] commandes=utilisateur.recupererDemandesAddition(connect, dao, (indiceActu-1)*Constantes.PAGINATION_LIMIT, tableFiltre);
            model.addAttribute(Constantes.VAR_COMMANDES, commandes);
            model.addAttribute(Constantes.VAR_LINKS, utilisateur.recupererLinks());
            model.addAttribute(Constantes.VAR_PLACES, places);
            model.addAttribute(Constantes.VAR_MODEPAIEMENTS, modePaiements);
            model.addAttribute(Constantes.VAR_IP, ip);
            model.addAttribute(Constantes.VAR_SESSIONUTILISATEUR, utilisateur);
            model.addAttribute(Constantes.VAR_SESSIONID, session.getId());
            String[] urls=utilisateur.recupererResetCacheAndNotify();
            model.addAttribute(Constantes.VAR_RESETCACHE, urls[0]);
            model.addAttribute(Constantes.VAR_RECEIVENOTIFY, urls[1]);
            HashMap<String, Object> pagination=dao.paginate(connect, countQuery, Constantes.PAGINATION_LIMIT, indiceActu);
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
    @PostMapping("/payer")
    @ResponseBody
    public ReponseREST payer(@RequestBody RestData datas) throws SQLException, Exception{
        ReponseREST response=new ReponseREST();
        PayerCommandeREST modifs=HandyManUtils.fromJson(PayerCommandeREST.class, datas.getRestdata());
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            response=filter.checkByRoleREST(modifs, connect, dao, new String[]{Constantes.ROLE_CAISSE});
            if(response.getCode()==Constantes.CODE_ERROR){
                return response;
            }
            modifs.getUtilisateur().payer(connect, dao, modifs.getPaiement());
            connect.commit();
            return response;
        }catch(Exception e){
            response.setCode(Constantes.CODE_ERROR);
            response.setMessage(e.getMessage());
            return response;
        }
    }

    @MessageMapping("/notify-redirect-caissier")
    @SendTo("/notify/receive-notify-redirect-caissier")
    public String notifierModifications(){
        return "reset cache";
    }

    @GetMapping("/reset-cache-caissier")
    public RedirectView resetCache(HttpServletRequest req) throws Exception{
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            Place where=new Place(0);
            places=dao.select(connect, Place.class, where);
            ModePaiement wherePaiement=new ModePaiement();
            wherePaiement.setEtat(0);
            modePaiements=dao.select(connect, ModePaiement.class, wherePaiement);
        }
        return filter.resetUserRole(req, dao, new String[]{Constantes.ROLE_CAISSE});
    }
}
