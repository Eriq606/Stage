package com.app.makay.utilitaire;

import org.springframework.ui.Model;
import org.springframework.web.servlet.view.RedirectView;

import com.app.makay.entites.Utilisateur;
import com.app.makay.iris.IrisFilter;
import com.app.makay.iris.IrisUser;

import handyman.HandyManUtils;

public class MyFilter implements IrisFilter{
    

    @Override
    public Object checkIfLoggedIn(IrisUser irisUser, String title, String viewpage, String targetView, Model model) {
        if(irisUser==null){
            String message=HandyManUtils.encodeURL_UTF8(Constantes.MSG_UTILISATEUR_NON_AUTHENTIFIE);
            return new RedirectView("/login?message="+message);
        }
        model.addAttribute(Constantes.VAR_BRAND, Constantes.BRAND);
        model.addAttribute(Constantes.VAR_TITLE, title);
        model.addAttribute(Constantes.VAR_VIEWPAGE, viewpage);
        model.addAttribute(Constantes.VAR_LOGINURL, "/login");
        model.addAttribute(Constantes.VAR_CURRENTUSER, ((Utilisateur)irisUser).getNom());
        return targetView;
    }

    @Override
    public Object checkByRole(IrisUser irisUser, String targetRole, String title, String viewpage, String targetView, Model model) {
        if(irisUser==null){
            String message=HandyManUtils.encodeURL_UTF8(Constantes.MSG_UTILISATEUR_NON_AUTHENTIFIE);
            return new RedirectView("/login?message="+message);
        }
        if(irisUser.getIrisRole().equals(targetRole)==false&&irisUser.getIrisRole().equals(Constantes.ROLE_SUPERVISEUR)==false){
            return new RedirectView("/403");
        }
        model.addAttribute(Constantes.VAR_BRAND, Constantes.BRAND);
        model.addAttribute(Constantes.VAR_TITLE, title);
        model.addAttribute(Constantes.VAR_VIEWPAGE, viewpage);
        model.addAttribute(Constantes.VAR_LOGINURL, "/login");
        model.addAttribute(Constantes.VAR_CURRENTUSER, ((Utilisateur)irisUser).getNom());
        return targetView;
    }

    @Override
    public Object checkByAuthorization(IrisUser irisUser, int minimumAuth, String title, String viewpage, String targetView, Model model) {
        if(irisUser==null){
            String message=HandyManUtils.encodeURL_UTF8(Constantes.MSG_UTILISATEUR_NON_AUTHENTIFIE);
            return new RedirectView("/login?message="+message);
        }
        if(irisUser.getIrisAuthorization()<minimumAuth==false){
            return new RedirectView("/403");
        }
        model.addAttribute(Constantes.VAR_BRAND, Constantes.BRAND);
        model.addAttribute(Constantes.VAR_TITLE, title);
        model.addAttribute(Constantes.VAR_VIEWPAGE, viewpage);
        model.addAttribute(Constantes.VAR_LOGINURL, "/login");
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
        return new RedirectView("/login-success");
    }

    @Override
    public RedirectView distributeByAuthorization(IrisUser irisUser) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'distributeByAuthorization'");
    }
    
}
