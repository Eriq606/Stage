package com.app.makay.utilitaire;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.ui.Model;
import org.springframework.web.servlet.view.RedirectView;

import com.app.makay.entites.Utilisateur;
import com.app.makay.iris.IrisFilter;
import com.app.makay.iris.IrisUser;

import handyman.HandyManUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import veda.godao.utils.DAOConnexion;

public class MyFilter implements IrisFilter{
    public RedirectView resetUserRole(HttpServletRequest req, MyDAO dao, String targetRole) throws SQLException, Exception{
        HttpSession session=req.getSession();
        Utilisateur utilisateur=(Utilisateur)session.getAttribute(Constantes.VAR_SESSIONUTILISATEUR);
        RedirectView iris=checkByRolePOST(utilisateur, targetRole);
        if(iris!=null){
            return iris;
        }
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            utilisateur.getRoleActuel(connect, dao);
            session.setAttribute(Constantes.VAR_SESSIONUTILISATEUR, utilisateur);
        }
        return distributeByRole(utilisateur);
    }

    @Override
    public RedirectView checkByAuthorizationPOST(IrisUser irisUser, int minimumAuth) {
        if(irisUser==null){
            String message=HandyManUtils.encodeURL_UTF8(Constantes.MSG_UTILISATEUR_NON_AUTHENTIFIE);
            return new RedirectView("/logout?message="+message);
        }
        if(irisUser.getIrisAuthorization()<minimumAuth==false){
            return new RedirectView("/403");
        }
        return null;
    }

    @Override
    public RedirectView checkByRolePOST(IrisUser irisUser, String targetRole) {
        if(irisUser==null){
            String message=HandyManUtils.encodeURL_UTF8(Constantes.MSG_UTILISATEUR_NON_AUTHENTIFIE);
            return new RedirectView("/logout?message="+message);
        }
        if(irisUser.getIrisRole().equals(targetRole)==false&&irisUser.getIrisRole().equals(Constantes.ROLE_SUPERVISEUR)==false){
            return new RedirectView("/403");
        }
        return null;
    }

    @Override
    public RedirectView checkIfLoggedInPOST(IrisUser irisUser) {
        if(irisUser==null){
            String message=HandyManUtils.encodeURL_UTF8(Constantes.MSG_UTILISATEUR_NON_AUTHENTIFIE);
            return new RedirectView("/logout?message="+message);
        }
        return null;
    }

    @Override
    public Object checkIfLoggedIn(IrisUser irisUser, String title, String viewpage, String targetView, Model model) {
        if(irisUser==null){
            String message=HandyManUtils.encodeURL_UTF8(Constantes.MSG_UTILISATEUR_NON_AUTHENTIFIE);
            return new RedirectView("/logout?message="+message);
        }
        model.addAttribute(Constantes.VAR_BRAND, Constantes.BRAND);
        model.addAttribute(Constantes.VAR_TITLE, title);
        model.addAttribute(Constantes.VAR_VIEWPAGE, viewpage);
        model.addAttribute(Constantes.VAR_LOGINURL, "/logout");
        model.addAttribute(Constantes.VAR_CURRENTUSER, ((Utilisateur)irisUser).getNom());
        return targetView;
    }

    @Override
    public Object checkByRole(IrisUser irisUser, String targetRole, String title, String viewpage, String targetView, Model model) {
        if(irisUser==null){
            String message=HandyManUtils.encodeURL_UTF8(Constantes.MSG_UTILISATEUR_NON_AUTHENTIFIE);
            return new RedirectView("/logout?message="+message);
        }
        if(irisUser.getIrisRole().equals(targetRole)==false&&irisUser.getIrisRole().equals(Constantes.ROLE_SUPERVISEUR)==false){
            return new RedirectView("/403");
        }
        model.addAttribute(Constantes.VAR_BRAND, Constantes.BRAND);
        model.addAttribute(Constantes.VAR_TITLE, title);
        model.addAttribute(Constantes.VAR_VIEWPAGE, viewpage);
        model.addAttribute(Constantes.VAR_LOGINURL, "/logout");
        model.addAttribute(Constantes.VAR_CURRENTUSER, ((Utilisateur)irisUser).getNom());
        return targetView;
    }

    @Override
    public Object checkByAuthorization(IrisUser irisUser, int minimumAuth, String title, String viewpage, String targetView, Model model) {
        if(irisUser==null){
            String message=HandyManUtils.encodeURL_UTF8(Constantes.MSG_UTILISATEUR_NON_AUTHENTIFIE);
            return new RedirectView("/logout?message="+message);
        }
        if(irisUser.getIrisAuthorization()<minimumAuth==false){
            return new RedirectView("/403");
        }
        model.addAttribute(Constantes.VAR_BRAND, Constantes.BRAND);
        model.addAttribute(Constantes.VAR_TITLE, title);
        model.addAttribute(Constantes.VAR_VIEWPAGE, viewpage);
        model.addAttribute(Constantes.VAR_LOGINURL, "/logout");
        model.addAttribute(Constantes.VAR_CURRENTUSER, ((Utilisateur)irisUser).getNom());
        return targetView;
    }

    @Override
    public RedirectView distributeByRole(IrisUser irisUser) {
        switch(irisUser.getIrisRole()){
            case Constantes.ROLE_SERVEUR:
                return new RedirectView("/serveur-passer-commande");
            case Constantes.ROLE_SUPERVISEUR:
                return new RedirectView("/plan-de-table");
        }
        return new RedirectView("/logout");
    }

    @Override
    public RedirectView distributeByRoleToResetCache(IrisUser irisUser) {
        switch(irisUser.getIrisRole()){
            case Constantes.ROLE_SERVEUR:
                return new RedirectView("/reset-cache-serveur");
            case Constantes.ROLE_SUPERVISEUR:
                return new RedirectView("/reset-cache-superviseur");
        }
        return new RedirectView("/logout");
    }

    @Override
    public RedirectView distributeByAuthorization(IrisUser irisUser) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'distributeByAuthorization'");
    }
    
}
