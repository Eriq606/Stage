package com.app.makay.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.app.makay.entites.Utilisateur;
import com.app.makay.utilitaire.Constantes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class ErrorController {
    @GetMapping("/403")
    public String error403(HttpServletRequest req, Model model){
        HttpSession session=req.getSession();
        Utilisateur utilisateur=(Utilisateur)session.getAttribute(Constantes.VAR_SESSIONUTILISATEUR);
        model.addAttribute(Constantes.VAR_CURRENTUSER, utilisateur.getNom());
        model.addAttribute(Constantes.VAR_TITLE, "Accès non autorisé");
        model.addAttribute(Constantes.VAR_LOGINURL, "/login");
        model.addAttribute(Constantes.VAR_VIEWPAGE, "errors/403");
        return "layout/layout";
    }
}
