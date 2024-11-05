package com.app.makay.controllers.metier.analyste;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.app.makay.entites.Utilisateur;
import com.app.makay.utilitaire.Constantes;
import com.app.makay.utilitaire.MyDAO;
import com.app.makay.utilitaire.MyFilter;

import handyman.HandyManUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class AnalysteController {
    private MyFilter filter;
    private MyDAO dao;
    private String ip;
    
    public AnalysteController() throws IOException {
        filter=new MyFilter();
        dao=new MyDAO();
        ip=HandyManUtils.getIP();
    }
    @GetMapping("/tableau-bord")
    public Object tableauBord(HttpServletRequest req, Model model){
        HttpSession session=req.getSession();
        Utilisateur utilisateur=(Utilisateur)session.getAttribute(Constantes.VAR_SESSIONUTILISATEUR);
        Object iris=filter.checkByRole(utilisateur, new String[]{Constantes.ROLE_ANALYSTE}, "Makay - Tableau de bord", "pages/analyste/tableau-bord", "layout/layout", model);
        if(utilisateur==null){
            return iris;
        }
        model.addAttribute(Constantes.VAR_LINKS, utilisateur.recupererLinks());
        return iris;
    }
}
