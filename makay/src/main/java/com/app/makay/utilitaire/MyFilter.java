package com.app.makay.utilitaire;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.springframework.ui.Model;
import org.springframework.web.servlet.view.RedirectView;

import com.app.makay.entites.Role;
import com.app.makay.entites.Utilisateur;
import com.app.makay.entites.REST.ObjectREST;
import com.app.makay.iris.IrisFilter;
import com.app.makay.iris.IrisUser;

import handyman.HandyManUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import veda.godao.DAO;

public class MyFilter implements IrisFilter{
    
    @Override
    public Object checkByRole(IrisUser irisUser, String[] targetRole, String title, String viewpage, String targetView,
            Model model) {
        if(irisUser==null){
            String message=HandyManUtils.encodeURL_UTF8(Constantes.MSG_UTILISATEUR_NON_AUTHENTIFIE);
            return new RedirectView("/logout?message="+message);
        }
        if(Arrays.asList(targetRole).contains(irisUser.getIrisRole())==false){
            return new RedirectView("/erreur?code=403");
        }
        model.addAttribute(Constantes.VAR_BRAND, Constantes.BRAND);
        model.addAttribute(Constantes.VAR_TITLE, title);
        model.addAttribute(Constantes.VAR_VIEWPAGE, viewpage);
        model.addAttribute(Constantes.VAR_LOGINURL, "/logout");
        model.addAttribute(Constantes.VAR_CURRENTUSER, ((Utilisateur)irisUser).getNom());
        return targetView;
    }

    @Override
    public RedirectView checkByRolePOST(IrisUser irisUser, String[] targetRole) {
        if(irisUser==null){
            String message=HandyManUtils.encodeURL_UTF8(Constantes.MSG_UTILISATEUR_NON_AUTHENTIFIE);
            return new RedirectView("/logout?message="+message);
        }
        if(Arrays.asList(targetRole).contains(irisUser.getIrisRole())==false){
            return new RedirectView("/erreur?code=403");
        }
        return null;
    }

    public RedirectView resetUserRole(HttpServletRequest req, Connection connect, MyDAO dao, String[] targetRole) throws SQLException, Exception{
        HttpSession session=req.getSession();
        Utilisateur utilisateur=(Utilisateur)session.getAttribute(Constantes.VAR_SESSIONUTILISATEUR);
        RedirectView iris=checkByRolePOST(utilisateur, targetRole);
        if(iris!=null){
            return iris;
        }
        utilisateur.getRoleActuel(connect, dao);
        utilisateur.getPlacesActuels(connect, dao);
        session.setAttribute(Constantes.VAR_SESSIONUTILISATEUR, utilisateur);
        return distributeByRole(utilisateur);
    }

    @Override
    public RedirectView checkByAuthorizationPOST(IrisUser irisUser, int minimumAuth) {
        if(irisUser==null){
            String message=HandyManUtils.encodeURL_UTF8(Constantes.MSG_UTILISATEUR_NON_AUTHENTIFIE);
            return new RedirectView("/logout?message="+message);
        }
        if(irisUser.getIrisAuthorization()<minimumAuth==false){
            return new RedirectView("/erreur?code=403");
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
            return new RedirectView("/erreur?code=403");
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
            return new RedirectView("/erreur?code=403");
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
            return new RedirectView("/erreur?code=403");
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
                return new RedirectView("/monitoring-des-serveurs");
            case Constantes.ROLE_BAR:
                return new RedirectView("/commandes-en-cours");
            case Constantes.ROLE_CAISSE:
                return new RedirectView("/demande-addition");
            case Constantes.ROLE_CUISINIER:
                return new RedirectView("/commandes-en-cours");
            case Constantes.ROLE_ANALYSTE:
                return new RedirectView("/tableau-bord");
        }
        return new RedirectView("/logout");
    }

    @Override
    public RedirectView distributeByRoleToResetCache(IrisUser irisUser) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RedirectView distributeByAuthorization(IrisUser irisUser) {
        throw new UnsupportedOperationException("Unimplemented method 'distributeByAuthorization'");
    }

    @Override
    public ReponseREST checkByRoleREST(ObjectREST modifs, Connection connect, DAO dao, String[] roles) throws Exception {
        ReponseREST response=new ReponseREST();
        SessionUtilisateur where=new SessionUtilisateur();
        where.setSessionId(modifs.getSessionid());
        where.setUtilisateur(modifs.getUtilisateur());
        where.setEstValide(Constantes.SESSION_ESTVALIDE);
        Role roleActuel=modifs.getUtilisateur().getRoleActuel(connect, dao);
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
        String[] authorized=roles;
        if(Arrays.asList(authorized).contains(roleActuel.getNumero())==false){
            response.setCode(Constantes.CODE_ERROR);
            response.setMessage(Constantes.MSG_NON_AUTHORISE);
            return response;
        }
        response.setCode(Constantes.CODE_SUCCESS);
        response.setMessage(Constantes.MSG_SUCCES);
        return response;
    }
    
}
