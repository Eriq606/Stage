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

    @GetMapping("/403")
    public Object error403(HttpServletRequest req, Model model){
        HttpSession session=req.getSession();
        Utilisateur utilisateur=(Utilisateur)session.getAttribute(Constantes.VAR_SESSIONUTILISATEUR);
        Object iris=filter.checkIfLoggedIn(utilisateur, "Makay - Accès non autorisé", "errors/403", "layout/layout", model);
        model.addAttribute(Constantes.VAR_LINKS, utilisateur.recupererLinks());
        return iris;
    }
}
