package com.app.makay.controllers.metier.barman;

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
import com.app.makay.entites.Produit;
import com.app.makay.entites.Role;
import com.app.makay.entites.Utilisateur;
import com.app.makay.entites.REST.CheckCommandeFilleREST;
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
public class BarmanController {
    private MyFilter filter;
    private MyDAO dao;
    private String ip;
    private Produit[] produits;
    public MyFilter getFilter() {
        return filter;
    }
    public void setFilter(MyFilter filter) {
        this.filter = filter;
    }
    public MyDAO getDao() {
        return dao;
    }
    public void setDao(MyDAO dao) {
        this.dao = dao;
    }
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public Produit[] getProduits() {
        return produits;
    }
    public void setProduits(Produit[] produits) {
        this.produits = produits;
    }
    public BarmanController() throws SQLException, Exception {
        filter=new MyFilter();
        dao=new MyDAO();
        // ip=HandyManUtils.getIP();
        ip=System.getenv("IP");
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            Utilisateur utilisateur=new Utilisateur();
            Role role=new Role();
            role.setNumero(Constantes.ROLE_BAR);
            role=dao.select(connect, Role.class, role)[0];
            utilisateur.setRole(role);
            produits=utilisateur.recupererProduitsCorrespondant(connect, dao);
            for(Produit p:produits){
                p.setAccompagnements(p.getAllAccompagnements(connect, dao));
            }
        }
    }
    @GetMapping("/commandes-en-cours-barman")
    public Object commandeEnCours(HttpServletRequest req, Model model, Integer indice_actu, String table) throws SQLException, Exception{
        HttpSession session=req.getSession();
        Utilisateur utilisateur=(Utilisateur)session.getAttribute(Constantes.VAR_SESSIONUTILISATEUR);
        Object iris=filter.checkByRole(utilisateur, new String[]{Constantes.ROLE_BAR, Constantes.ROLE_CUISINIER, Constantes.ROLE_SUPERVISEUR}, "Makay - Commandes en cours de préparation", "pages/barman/commande-en-cours", "layout/layout", model);
        if(utilisateur==null){
            return iris;
        }
        String queryCount="select count(*) from v_commandes where id in (select idcommande from v_commandefille_produits where idcategorie in (select idcategorie from v_role_categorie_produits_checkings where idrole=%s) group by idcommande) and etat<20";
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
            CommandeEnCours[] commandes=utilisateur.recupererCommandesChecking(connect, dao, (indice_actu_controller-1)*Constantes.PAGINATION_LIMIT, table);
            model.addAttribute(Constantes.VAR_COMMANDES, commandes);
            HashMap<String, Object> pagination=dao.paginate(connect, queryCount, Constantes.PAGINATION_LIMIT, indice_actu_controller);
            for (Map.Entry<String, Object> entry : pagination.entrySet()) {
                model.addAttribute(entry.getKey(), entry.getValue());
            }
        }
        model.addAttribute(Constantes.VAR_INDICE_PAGINATION, indice_actu_controller);
        model.addAttribute(Constantes.VAR_TABLE, table);
        model.addAttribute(Constantes.VAR_LINKS, Constantes.LINK_BARMAN);
        model.addAttribute(Constantes.VAR_PLACES, utilisateur.getPlaces());
        model.addAttribute(Constantes.VAR_SESSIONUTILISATEUR, utilisateur);
        model.addAttribute(Constantes.VAR_SESSIONID, session.getId());
        model.addAttribute(Constantes.VAR_IP, ip);
        return iris;
    }
    @PostMapping("/terminer-commande-fille")
    @ResponseBody
    public ReponseREST checkCommandeFille(@RequestBody RestData datas){
        ReponseREST response=new ReponseREST();
        CheckCommandeFilleREST modifs=HandyManUtils.fromJson(CheckCommandeFilleREST.class, datas.getRestdata());
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            response=filter.checkByRoleREST(modifs, connect, dao, new String[]{Constantes.ROLE_BAR, Constantes.ROLE_CUISINIER});
            if(response.getCode()==Constantes.CODE_ERROR){
                return response;
            }
            modifs.getUtilisateur().checkCommandeFille(connect, dao, modifs.getCommandeFille());
            connect.commit();
            return response;
        }catch(Exception e){
            response.setCode(Constantes.CODE_ERROR);
            response.setMessage(e.getMessage());
            return response;
        }
    }

    @MessageMapping("/notify-redirect-barman")
    @SendTo("/notify/receive-notify-redirect-barman")
    public String notifierModifications(){
        return "reset cache";
    }

    @GetMapping("/reset-role-barman")
    public RedirectView resetCacheRoles(HttpServletRequest req) throws Exception{
        return filter.resetUserRole(req, dao, Constantes.ROLE_BAR);
    }

    @GetMapping("/reset-cache-barman")
    public RedirectView resetCacheProduits(HttpServletRequest req) throws Exception{
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            Utilisateur utilisateur=new Utilisateur();
            Role role=new Role();
            role.setNumero(Constantes.ROLE_BAR);
            role=dao.select(connect, Role.class, role)[0];
            utilisateur.setRole(role);
            produits=utilisateur.recupererProduitsCorrespondant(connect, dao);
            for(Produit p:produits){
                p.setAccompagnements(p.getAllAccompagnements(connect, dao));
            }
        }
        return resetCacheRoles(req);
    }
}
