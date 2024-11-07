package com.app.makay.controllers.metier.admin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.app.makay.entites.Categorie;
import com.app.makay.entites.ModePaiement;
import com.app.makay.entites.Place;
import com.app.makay.entites.Produit;
import com.app.makay.entites.Rangee;
import com.app.makay.entites.Role;
import com.app.makay.entites.TypePlace;
import com.app.makay.entites.Utilisateur;
import com.app.makay.entites.REST.EnvoiREST;
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
public class AdminController {
    private MyFilter filter;
    private MyDAO dao;
    private String ip;
    
    public AdminController() throws IOException {
        filter=new MyFilter();
        dao=new MyDAO();
        ip=HandyManUtils.getIP();
    }

    @GetMapping("/utilisateurs")
    public Object utilisateurs(HttpServletRequest req, Model model, Integer indiceListe, Integer indiceSuppr) throws SQLException, Exception{
        HttpSession session=req.getSession();
        Utilisateur utilisateur=(Utilisateur)session.getAttribute(Constantes.VAR_SESSIONUTILISATEUR);
        Object iris=filter.checkByRole(utilisateur, new String[]{Constantes.ROLE_ADMIN}, "Makay - Gestion des utilisateurs", "pages/admin/utilisateurs", "layout/layout", model);
        if(utilisateur==null){
            return iris;
        }
        int indiceListe_controller=1;
        int indiceSuppr_controller=1;
        if(indiceListe!=null){
            indiceListe_controller=indiceListe;
        }
        if(indiceSuppr!=null){
            indiceSuppr_controller=indiceSuppr;
        }
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            String addOn="where etat=0 and id>0 limit %s offset %s";
            addOn=String.format(addOn, Constantes.PAGINATION_LIMIT, (indiceListe_controller-1)*Constantes.PAGINATION_LIMIT);
            Utilisateur[] utilisateurs=dao.select(connect, Utilisateur.class, addOn);
            model.addAttribute(Constantes.VAR_UTILISATEURS, utilisateurs);
            Utilisateur where=new Utilisateur();
            where.setEtat(Constantes.UTILISATEUR_SUPPRIME);
            Utilisateur[] supprimes=dao.select(connect, Utilisateur.class, where, Constantes.PAGINATION_LIMIT, (indiceSuppr_controller-1)*Constantes.PAGINATION_LIMIT);
            model.addAttribute(Constantes.VAR_UTILISATEURS_SUPPR, supprimes);

            String query="select count(*) from utilisateurs where etat=0 and id>0";
            HashMap<String, Object> paginationListe=dao.paginate(connect, query, Constantes.PAGINATION_LIMIT, indiceListe_controller);
            for(Map.Entry<String, Object> e:paginationListe.entrySet()){
                model.addAttribute(e.getKey()+"_liste", e.getValue());
            }
            HashMap<String, Object> paginationSuppr=dao.paginate(connect, Utilisateur.class, where, Constantes.PAGINATION_LIMIT, indiceSuppr_controller);
            for(Map.Entry<String, Object> e:paginationSuppr.entrySet()){
                model.addAttribute(e.getKey()+"_suppr", e.getValue());
            }
            Role[] roles=dao.select(connect, Role.class);
            model.addAttribute(Constantes.VAR_ROLES, roles);
        }
        model.addAttribute(Constantes.VAR_IP, ip);
        model.addAttribute(Constantes.VAR_LINKS, utilisateur.recupererLinks());
        model.addAttribute(Constantes.VAR_SESSIONUTILISATEUR, utilisateur);
        model.addAttribute(Constantes.VAR_SESSIONID, session.getId());
        return iris;
    }
    @PostMapping("/supprimer-utilisateur")
    @ResponseBody
    public ReponseREST supprimerUtilisateur(@RequestBody RestData datas) throws SQLException, Exception{
        ReponseREST response=new ReponseREST();
        EnvoiREST modifs=HandyManUtils.fromJson(EnvoiREST.class, datas.getRestdata());
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            response=filter.checkByRoleREST(modifs, connect, dao, new String[]{Constantes.ROLE_ADMIN});
            if(response.getCode()==Constantes.CODE_ERROR){
                return response;
            }
            modifs.getUtilisateur().supprimerUtilisateur(connect, dao, modifs.getDonnees().get("idutilisateur"));
            connect.commit();
            return response;
        }catch(Exception e){
            response.setCode(Constantes.CODE_ERROR);
            response.setMessage(e.getMessage());
            return response;
        }
    }
    @PostMapping("/restaurer-utilisateur")
    @ResponseBody
    public ReponseREST restaurerUtilisateur(@RequestBody RestData datas) throws SQLException, Exception{
        ReponseREST response=new ReponseREST();
        EnvoiREST modifs=HandyManUtils.fromJson(EnvoiREST.class, datas.getRestdata());
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            response=filter.checkByRoleREST(modifs, connect, dao, new String[]{Constantes.ROLE_ADMIN});
            if(response.getCode()==Constantes.CODE_ERROR){
                return response;
            }
            modifs.getUtilisateur().restaurerUtilisateur(connect, dao, modifs.getDonnees().get("idutilisateur"));
            connect.commit();
            return response;
        }catch(Exception e){
            response.setCode(Constantes.CODE_ERROR);
            response.setMessage(e.getMessage());
            return response;
        }
    }
    @PostMapping("/ajouter-utilisateur")
    @ResponseBody
    public ReponseREST ajouterUtilisateur(@RequestBody RestData datas) throws SQLException, Exception{
        ReponseREST response=new ReponseREST();
        EnvoiREST modifs=HandyManUtils.fromJson(EnvoiREST.class, datas.getRestdata());
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            response=filter.checkByRoleREST(modifs, connect, dao, new String[]{Constantes.ROLE_ADMIN});
            if(response.getCode()==Constantes.CODE_ERROR){
                return response;
            }
            modifs.getUtilisateur().ajouterUtilisateur(connect, dao, modifs.getDonnees().get("nom"), modifs.getDonnees().get("motdepasse"), modifs.getDonnees().get("contact"), modifs.getDonnees().get("idrole"));
            connect.commit();
            return response;
        }catch(Exception e){
            response.setCode(Constantes.CODE_ERROR);
            response.setMessage(e.getMessage());
            return response;
        }
    }
    @GetMapping("/produits")
    public Object produits(HttpServletRequest req, Model model, Integer indiceListe, Integer indiceSuppr) throws SQLException, Exception{
        HttpSession session=req.getSession();
        Utilisateur utilisateur=(Utilisateur)session.getAttribute(Constantes.VAR_SESSIONUTILISATEUR);
        Object iris=filter.checkByRole(utilisateur, new String[]{Constantes.ROLE_ADMIN}, "Makay - Gestion des produits", "pages/admin/produits", "layout/layout", model);
        if(utilisateur==null){
            return iris;
        }
        int indiceListe_controller=1;
        int indiceSuppr_controller=1;
        if(indiceListe!=null){
            indiceListe_controller=indiceListe;
        }
        if(indiceSuppr!=null){
            indiceSuppr_controller=indiceSuppr;
        }
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            Produit where=new Produit();
            where.setEtat(Constantes.PRODUIT_CREE);
            Produit[] produits=dao.select(connect, Produit.class, where, Constantes.PAGINATION_LIMIT, (indiceListe_controller-1)*Constantes.PAGINATION_LIMIT);
            for(Produit p:produits){
                p.setAccompagnements(p.recupererAllAccompagnements(connect, dao));
            }
            HashMap<String, Object> paginationListe=dao.paginate(connect, Produit.class, where, Constantes.PAGINATION_LIMIT, indiceListe_controller);
            for(Map.Entry<String, Object> e:paginationListe.entrySet()){
                model.addAttribute(e.getKey()+"_liste", e.getValue());
            }
            where.setEtat(Constantes.PRODUIT_SUPPRIME);
            Produit[] produitSuppr=dao.select(connect, Produit.class, where, Constantes.PAGINATION_LIMIT, (indiceSuppr_controller-1)*Constantes.PAGINATION_LIMIT);
            for(Produit p:produitSuppr){
                p.setAccompagnements(p.recupererAllAccompagnements(connect, dao));
            }
            HashMap<String, Object> paginationSuppr=dao.paginate(connect, Produit.class, where, Constantes.PAGINATION_LIMIT, indiceSuppr_controller);
            for(Map.Entry<String, Object> e:paginationSuppr.entrySet()){
                model.addAttribute(e.getKey()+"_suppr", e.getValue());
            }
            Categorie whereCategorie=new Categorie();
            whereCategorie.setEtat(Constantes.CATEGORIE_CREE);
            Categorie[] categories=dao.select(connect, Categorie.class, whereCategorie);
            model.addAttribute(Constantes.VAR_PRODUITS, produits);
            model.addAttribute(Constantes.VAR_PRODUITS_SUPPR, produitSuppr);
            model.addAttribute(Constantes.VAR_CATEGORIES, categories);
        }
        model.addAttribute(Constantes.VAR_LINKS, utilisateur.recupererLinks());
        model.addAttribute(Constantes.VAR_IP, ip);
        model.addAttribute(Constantes.VAR_SESSIONUTILISATEUR, utilisateur);
        model.addAttribute(Constantes.VAR_SESSIONID, session.getId());
        model.addAttribute(Constantes.VAR_CODECRUD_SUPPR, Constantes.CRUD_SUPPRESSION);
        model.addAttribute(Constantes.VAR_CODECRUD_RESTAURER, Constantes.CRUD_RESTAURATION);
        model.addAttribute(Constantes.VAR_CODECRUD_AJOUT, Constantes.CRUD_AJOUT);
        model.addAttribute(Constantes.VAR_CODECRUD_MODIF, Constantes.CRUD_MODIFICATION);
        return iris;
    }
    @PostMapping("/action-produit")
    @ResponseBody
    public ReponseREST actionProduit(@RequestBody RestData datas){
        ReponseREST response=new ReponseREST();
        EnvoiREST modifs=HandyManUtils.fromJson(EnvoiREST.class, datas.getRestdata());
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            response=filter.checkByRoleREST(modifs, connect, dao, new String[]{Constantes.ROLE_ADMIN});
            if(response.getCode()==Constantes.CODE_ERROR){
                return response;
            }
            int action=Integer.parseInt(modifs.getDonnees().get("action"));
            switch(action){
                case Constantes.CRUD_SUPPRESSION:
                    modifs.getUtilisateur().supprimerProduit(connect, dao, modifs.getDonnees().get("idproduit"));
                    break;
                case Constantes.CRUD_RESTAURATION:
                    modifs.getUtilisateur().restaurerProduit(connect, dao, modifs.getDonnees().get("idproduit"));
                    break;
                case Constantes.CRUD_AJOUT:
                    modifs.getUtilisateur().ajouterProduit(connect, dao, modifs.getDonnees().get("nom"), modifs.getDonnees().get("categorie"), modifs.getDonnees().get("prix"), modifs.getDonnees().get("accompagnement"));
                    break;
                case Constantes.CRUD_MODIFICATION:
                    modifs.getUtilisateur().modifierProduit(connect, dao, modifs.getDonnees().get("idproduit"), modifs.getDonnees().get("prix"), modifs.getDonnees().get("accomps"));
                    break;
            }
            connect.commit();
            return response;
        }catch(Exception e){
            response.setCode(Constantes.CODE_ERROR);
            response.setMessage(e.getMessage());
            return response;
        }
    }
    @GetMapping("/categories")
    public Object categories(HttpServletRequest req, Model model, Integer indiceListe, Integer indiceSuppr) throws SQLException, Exception{
        HttpSession session=req.getSession();
        Utilisateur utilisateur=(Utilisateur)session.getAttribute(Constantes.VAR_SESSIONUTILISATEUR);
        Object iris=filter.checkByRole(utilisateur, new String[]{Constantes.ROLE_ADMIN}, "Makay - Gestion des categories", "pages/admin/categories", "layout/layout", model);
        if(utilisateur==null){
            return iris;
        }
        int indiceListe_controller=1;
        int indiceSuppr_controller=1;
        if(indiceListe!=null){
            indiceListe_controller=indiceListe;
        }
        if(indiceSuppr!=null){
            indiceSuppr_controller=indiceSuppr;
        }
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            Categorie where=new Categorie();
            where.setEtat(Constantes.CATEGORIE_CREE);
            Categorie[] categories=dao.select(connect, Categorie.class, where, Constantes.PAGINATION_LIMIT, (indiceListe_controller-1)*Constantes.PAGINATION_LIMIT);
            for(Categorie c:categories){
                c.recupererRoles(connect, dao);
            }
            HashMap<String, Object> paginationListe=dao.paginate(connect, Categorie.class, where, Constantes.PAGINATION_LIMIT, indiceListe_controller);
            for(Map.Entry<String, Object> e:paginationListe.entrySet()){
                model.addAttribute(e.getKey()+"_liste", e.getValue());
            }
            where.setEtat(Constantes.CATEGORIE_SUPPRIME);
            Categorie[] categorieSuppr=dao.select(connect, Categorie.class, where, Constantes.PAGINATION_LIMIT, (indiceSuppr_controller-1)*Constantes.PAGINATION_LIMIT);
            HashMap<String, Object> paginationSuppr=dao.paginate(connect, Categorie.class, where, Constantes.PAGINATION_LIMIT, indiceSuppr_controller);
            for(Map.Entry<String, Object> e:paginationSuppr.entrySet()){
                model.addAttribute(e.getKey()+"_suppr", e.getValue());
            }
            model.addAttribute(Constantes.VAR_CATEGORIES, categories);
            model.addAttribute(Constantes.VAR_CATEGORIES_SUPPR, categorieSuppr);
            model.addAttribute(Constantes.VAR_ROLES, Constantes.ROLES_AUTORISES);
        }
        model.addAttribute(Constantes.VAR_LINKS, utilisateur.recupererLinks());
        model.addAttribute(Constantes.VAR_IP, ip);
        model.addAttribute(Constantes.VAR_SESSIONUTILISATEUR, utilisateur);
        model.addAttribute(Constantes.VAR_SESSIONID, session.getId());
        model.addAttribute(Constantes.VAR_CODECRUD_SUPPR, Constantes.CRUD_SUPPRESSION);
        model.addAttribute(Constantes.VAR_CODECRUD_RESTAURER, Constantes.CRUD_RESTAURATION);
        model.addAttribute(Constantes.VAR_CODECRUD_AJOUT, Constantes.CRUD_AJOUT);
        model.addAttribute(Constantes.VAR_CODECRUD_MODIF, Constantes.CRUD_MODIFICATION);
        return iris;
    }
    @PostMapping("/action-categorie")
    @ResponseBody
    public ReponseREST actionCategorie(@RequestBody RestData datas){
        ReponseREST response=new ReponseREST();
        EnvoiREST modifs=HandyManUtils.fromJson(EnvoiREST.class, datas.getRestdata());
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            response=filter.checkByRoleREST(modifs, connect, dao, new String[]{Constantes.ROLE_ADMIN});
            if(response.getCode()==Constantes.CODE_ERROR){
                return response;
            }
            int action=Integer.parseInt(modifs.getDonnees().get("action"));
            switch(action){
                case Constantes.CRUD_SUPPRESSION:
                    modifs.getUtilisateur().supprimerCategories(connect, dao, modifs.getDonnees().get("idcategorie"));
                    break;
                case Constantes.CRUD_RESTAURATION:
                    modifs.getUtilisateur().restaurerCategorie(connect, dao, modifs.getDonnees().get("idcategorie"));
                    break;
                case Constantes.CRUD_AJOUT:
                    modifs.getUtilisateur().ajouterCategorie(connect, dao, modifs.getDonnees().get("nom"));
                    break;
                case Constantes.CRUD_MODIFICATION:
                    modifs.getUtilisateur().modifierCategorie(connect, dao, modifs.getDonnees().get("idcategorie"), modifs.getDonnees().get("roles"));
                    break;
            }
            connect.commit();
            return response;
        }catch(Exception e){
            response.setCode(Constantes.CODE_ERROR);
            response.setMessage(e.getMessage());
            return response;
        }
    }
    @GetMapping("/rangees")
    public Object rangees(HttpServletRequest req, Model model, Integer indiceListe, Integer indiceSuppr) throws SQLException, Exception{
        HttpSession session=req.getSession();
        Utilisateur utilisateur=(Utilisateur)session.getAttribute(Constantes.VAR_SESSIONUTILISATEUR);
        Object iris=filter.checkByRole(utilisateur, new String[]{Constantes.ROLE_ADMIN}, "Makay - Gestion des rangees", "pages/admin/rangees", "layout/layout", model);
        if(utilisateur==null){
            return iris;
        }
        int indiceListe_controller=1;
        int indiceSuppr_controller=1;
        if(indiceListe!=null){
            indiceListe_controller=indiceListe;
        }
        if(indiceSuppr!=null){
            indiceSuppr_controller=indiceSuppr;
        }
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            String addOn="where etat=0 and id>0 limit %s offset %s";
            addOn=String.format(addOn, Constantes.PAGINATION_LIMIT, (indiceListe_controller-1)*Constantes.PAGINATION_LIMIT);
            Rangee[] rangees=dao.select(connect, Rangee.class, addOn);
            String count="select count(*) from rangees where etat=0 and id>0";
            HashMap<String, Object> paginationListe=dao.paginate(connect, count, Constantes.PAGINATION_LIMIT, indiceListe_controller);
            for(Map.Entry<String, Object> e:paginationListe.entrySet()){
                model.addAttribute(e.getKey()+"_liste", e.getValue());
            }
            Rangee where=new Rangee();
            where.setEtat(Constantes.RANGEE_SUPPRIME);
            Rangee[] rangeeSuppr=dao.select(connect, Rangee.class, where, Constantes.PAGINATION_LIMIT, (indiceSuppr_controller-1)*Constantes.PAGINATION_LIMIT);
            HashMap<String, Object> paginationSuppr=dao.paginate(connect, Rangee.class, where, Constantes.PAGINATION_LIMIT, indiceSuppr_controller);
            for(Map.Entry<String, Object> e:paginationSuppr.entrySet()){
                model.addAttribute(e.getKey()+"_suppr", e.getValue());
            }
            model.addAttribute(Constantes.VAR_RANGEES, rangees);
            model.addAttribute(Constantes.VAR_RANGEES_SUPPR, rangeeSuppr);
        }
        model.addAttribute(Constantes.VAR_LINKS, utilisateur.recupererLinks());
        model.addAttribute(Constantes.VAR_IP, ip);
        model.addAttribute(Constantes.VAR_SESSIONUTILISATEUR, utilisateur);
        model.addAttribute(Constantes.VAR_SESSIONID, session.getId());
        model.addAttribute(Constantes.VAR_CODECRUD_SUPPR, Constantes.CRUD_SUPPRESSION);
        model.addAttribute(Constantes.VAR_CODECRUD_RESTAURER, Constantes.CRUD_RESTAURATION);
        model.addAttribute(Constantes.VAR_CODECRUD_AJOUT, Constantes.CRUD_AJOUT);
        return iris;
    }
    @PostMapping("/action-rangee")
    @ResponseBody
    public ReponseREST actionRangee(@RequestBody RestData datas){
        ReponseREST response=new ReponseREST();
        EnvoiREST modifs=HandyManUtils.fromJson(EnvoiREST.class, datas.getRestdata());
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            response=filter.checkByRoleREST(modifs, connect, dao, new String[]{Constantes.ROLE_ADMIN});
            if(response.getCode()==Constantes.CODE_ERROR){
                return response;
            }
            int action=Integer.parseInt(modifs.getDonnees().get("action"));
            switch(action){
                case Constantes.CRUD_SUPPRESSION:
                    modifs.getUtilisateur().supprimerRangees(connect, dao, modifs.getDonnees().get("idrangee"));
                    break;
                case Constantes.CRUD_RESTAURATION:
                    modifs.getUtilisateur().restaurerRangee(connect, dao, modifs.getDonnees().get("idrangee"));
                    break;
                case Constantes.CRUD_AJOUT:
                    modifs.getUtilisateur().ajouterRangee(connect, dao, modifs.getDonnees().get("nom"));
                    break;
            }
            connect.commit();
            return response;
        }catch(Exception e){
            response.setCode(Constantes.CODE_ERROR);
            response.setMessage(e.getMessage());
            return response;
        }
    }
    @GetMapping("/places")
    public Object places(HttpServletRequest req, Model model, Integer indiceListe, Integer indiceSuppr) throws SQLException, Exception{
        HttpSession session=req.getSession();
        Utilisateur utilisateur=(Utilisateur)session.getAttribute(Constantes.VAR_SESSIONUTILISATEUR);
        Object iris=filter.checkByRole(utilisateur, new String[]{Constantes.ROLE_ADMIN}, "Makay - Gestion des places", "pages/admin/places", "layout/layout", model);
        if(utilisateur==null){
            return iris;
        }
        int indiceListe_controller=1;
        int indiceSuppr_controller=1;
        if(indiceListe!=null){
            indiceListe_controller=indiceListe;
        }
        if(indiceSuppr!=null){
            indiceSuppr_controller=indiceSuppr;
        }
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            Place where=new Place();
            where.setEtat(Constantes.PLACE_CREE);
            Place[] places=dao.select(connect, Place.class, where, Constantes.PAGINATION_LIMIT, (indiceListe_controller-1)*Constantes.PAGINATION_LIMIT);
            HashMap<String, Object> paginationListe=dao.paginate(connect, Place.class, where, Constantes.PAGINATION_LIMIT, indiceListe_controller);
            for(Map.Entry<String, Object> e:paginationListe.entrySet()){
                model.addAttribute(e.getKey()+"_liste", e.getValue());
            }
            where.setEtat(Constantes.PLACE_SUPPRIME);
            Place[] placeSuppr=dao.select(connect, Place.class, where, Constantes.PAGINATION_LIMIT, (indiceSuppr_controller-1)*Constantes.PAGINATION_LIMIT);
            HashMap<String, Object> paginationSuppr=dao.paginate(connect, Place.class, where, Constantes.PAGINATION_LIMIT, indiceSuppr_controller);
            for(Map.Entry<String, Object> e:paginationSuppr.entrySet()){
                model.addAttribute(e.getKey()+"_suppr", e.getValue());
            }
            model.addAttribute(Constantes.VAR_PLACES, places);
            model.addAttribute(Constantes.VAR_PLACES_SUPPR, placeSuppr);
            TypePlace whereTypePlace=new TypePlace();
            whereTypePlace.setEtat(Constantes.TYPEPLACE_CREE);
            TypePlace[] types=dao.select(connect, TypePlace.class, whereTypePlace);
            model.addAttribute(Constantes.VAR_TYPEPLACES, types);
        }
        model.addAttribute(Constantes.VAR_LINKS, utilisateur.recupererLinks());
        model.addAttribute(Constantes.VAR_IP, ip);
        model.addAttribute(Constantes.VAR_SESSIONUTILISATEUR, utilisateur);
        model.addAttribute(Constantes.VAR_SESSIONID, session.getId());
        model.addAttribute(Constantes.VAR_CODECRUD_SUPPR, Constantes.CRUD_SUPPRESSION);
        model.addAttribute(Constantes.VAR_CODECRUD_RESTAURER, Constantes.CRUD_RESTAURATION);
        model.addAttribute(Constantes.VAR_CODECRUD_AJOUT, Constantes.CRUD_AJOUT);
        return iris;
    }
    @PostMapping("/action-place")
    @ResponseBody
    public ReponseREST actionPlace(@RequestBody RestData datas){
        ReponseREST response=new ReponseREST();
        EnvoiREST modifs=HandyManUtils.fromJson(EnvoiREST.class, datas.getRestdata());
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            response=filter.checkByRoleREST(modifs, connect, dao, new String[]{Constantes.ROLE_ADMIN});
            if(response.getCode()==Constantes.CODE_ERROR){
                return response;
            }
            int action=Integer.parseInt(modifs.getDonnees().get("action"));
            switch(action){
                case Constantes.CRUD_SUPPRESSION:
                    modifs.getUtilisateur().supprimerPlaces(connect, dao, modifs.getDonnees().get("idplace"));
                    break;
                case Constantes.CRUD_RESTAURATION:
                    modifs.getUtilisateur().restaurerPlace(connect, dao, modifs.getDonnees().get("idplace"));
                    break;
                case Constantes.CRUD_AJOUT:
                    modifs.getUtilisateur().ajouterPlace(connect, dao, modifs.getDonnees().get("nom"), modifs.getDonnees().get("typePlace"));
                    break;
            }
            connect.commit();
            return response;
        }catch(Exception e){
            response.setCode(Constantes.CODE_ERROR);
            response.setMessage(e.getMessage());
            return response;
        }
    }
    @GetMapping("/modes-paiement")
    public Object modesPaiement(HttpServletRequest req, Model model, Integer indiceListe, Integer indiceSuppr) throws SQLException, Exception{
        HttpSession session=req.getSession();
        Utilisateur utilisateur=(Utilisateur)session.getAttribute(Constantes.VAR_SESSIONUTILISATEUR);
        Object iris=filter.checkByRole(utilisateur, new String[]{Constantes.ROLE_ADMIN}, "Makay - Gestion des modes de paiement", "pages/admin/modes-paiement", "layout/layout", model);
        if(utilisateur==null){
            return iris;
        }
        int indiceListe_controller=1;
        int indiceSuppr_controller=1;
        if(indiceListe!=null){
            indiceListe_controller=indiceListe;
        }
        if(indiceSuppr!=null){
            indiceSuppr_controller=indiceSuppr;
        }
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            String addOn="where etat=0 and id>0 limit %s offset %s";
            addOn=String.format(addOn, Constantes.PAGINATION_LIMIT, (indiceListe_controller-1)*Constantes.PAGINATION_LIMIT);
            ModePaiement[] modePaiements=dao.select(connect, ModePaiement.class, addOn);
            String count="select count(*) from mode_paiements where etat=0 and id>0";
            HashMap<String, Object> paginationListe=dao.paginate(connect, count, Constantes.PAGINATION_LIMIT, indiceListe_controller);
            for(Map.Entry<String, Object> e:paginationListe.entrySet()){
                model.addAttribute(e.getKey()+"_liste", e.getValue());
            }
            ModePaiement where=new ModePaiement();
            where.setEtat(Constantes.MODEPAIEMENT_SUPPRIME);
            ModePaiement[] modePaiementSuppr=dao.select(connect, ModePaiement.class, where, Constantes.PAGINATION_LIMIT, (indiceSuppr_controller-1)*Constantes.PAGINATION_LIMIT);
            HashMap<String, Object> paginationSuppr=dao.paginate(connect, ModePaiement.class, where, Constantes.PAGINATION_LIMIT, indiceSuppr_controller);
            for(Map.Entry<String, Object> e:paginationSuppr.entrySet()){
                model.addAttribute(e.getKey()+"_suppr", e.getValue());
            }
            model.addAttribute(Constantes.VAR_MODEPAIEMENTS, modePaiements);
            model.addAttribute(Constantes.VAR_MODESPAIEMENT_SUPPR, modePaiementSuppr);
        }
        model.addAttribute(Constantes.VAR_LINKS, utilisateur.recupererLinks());
        model.addAttribute(Constantes.VAR_IP, ip);
        model.addAttribute(Constantes.VAR_SESSIONUTILISATEUR, utilisateur);
        model.addAttribute(Constantes.VAR_SESSIONID, session.getId());
        model.addAttribute(Constantes.VAR_CODECRUD_SUPPR, Constantes.CRUD_SUPPRESSION);
        model.addAttribute(Constantes.VAR_CODECRUD_RESTAURER, Constantes.CRUD_RESTAURATION);
        model.addAttribute(Constantes.VAR_CODECRUD_AJOUT, Constantes.CRUD_AJOUT);
        return iris;
    }
    @PostMapping("/action-mode")
    @ResponseBody
    public ReponseREST actionMode(@RequestBody RestData datas){
        ReponseREST response=new ReponseREST();
        EnvoiREST modifs=HandyManUtils.fromJson(EnvoiREST.class, datas.getRestdata());
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            response=filter.checkByRoleREST(modifs, connect, dao, new String[]{Constantes.ROLE_ADMIN});
            if(response.getCode()==Constantes.CODE_ERROR){
                return response;
            }
            int action=Integer.parseInt(modifs.getDonnees().get("action"));
            switch(action){
                case Constantes.CRUD_SUPPRESSION:
                    modifs.getUtilisateur().supprimerModes(connect, dao, modifs.getDonnees().get("idmode"));
                    break;
                case Constantes.CRUD_RESTAURATION:
                    modifs.getUtilisateur().restaurerMode(connect, dao, modifs.getDonnees().get("idmode"));
                    break;
                case Constantes.CRUD_AJOUT:
                    modifs.getUtilisateur().ajouterMode(connect, dao, modifs.getDonnees().get("nom"));
                    break;
            }
            connect.commit();
            return response;
        }catch(Exception e){
            response.setCode(Constantes.CODE_ERROR);
            response.setMessage(e.getMessage());
            return response;
        }
    }
    /*
     * - File produits=new File(fichierProduits)
     * - HashMap mapProduits=readCSV(produits)
     * - ImportProduit[] imports=new ImportProduits[mapProduits]
     * - loop(imports, insert())
     * - call_insert_categories()
     * - call_insert_produits()
     */
    @PostMapping("import-produits")
    @ResponseBody
    public ReponseREST importProduits(MultipartFile fichierProduits, String utilisateur, String sessionid){
        ReponseREST response=new ReponseREST();
        EnvoiREST modifs=new EnvoiREST();
        modifs.setUtilisateur(HandyManUtils.fromJson(Utilisateur.class, utilisateur));
        modifs.setSessionid(sessionid);
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            response=filter.checkByRoleREST(modifs, connect, dao, new String[]{Constantes.ROLE_ADMIN});
            if(response.getCode()==Constantes.CODE_ERROR){
                return response;
            }
            modifs.getUtilisateur().importProduits(connect, dao, fichierProduits);
            connect.commit();
            return response;
        }catch(Exception e){
            response.setCode(Constantes.CODE_ERROR);
            response.setMessage(e.getMessage());
            return response;
        }
    }
}
