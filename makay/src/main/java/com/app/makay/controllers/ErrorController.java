package com.app.makay.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.app.makay.entites.Utilisateur;
import com.app.makay.utilitaire.Constantes;
import com.app.makay.utilitaire.MyFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class ErrorController {
    private MyFilter filter;
    
    public ErrorController() {
        filter=new MyFilter();
    }

    @GetMapping("/error")
    public Object errorerror(HttpServletRequest req, Model model, String code){
        HttpSession session=req.getSession();
        Utilisateur utilisateur=(Utilisateur)session.getAttribute(Constantes.VAR_SESSIONUTILISATEUR);
        Object iris=filter.checkIfLoggedIn(utilisateur, "Makay - "+Constantes.ERREURS.get(code), "errors/error", "layout/layout", model);
        model.addAttribute(Constantes.VAR_LINKS, utilisateur.recupererLinks());
        model.addAttribute(Constantes.VAR_CODE_ERREUR, code);
        model.addAttribute(Constantes.VAR_MSG_ERREUR, Constantes.ERREURS.get(code));
        return iris;
    }
}
