package com.app.makay.controllers.metier.analyste;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.app.makay.entites.Categorie;
import com.app.makay.entites.Commande;
import com.app.makay.entites.Paiement;
import com.app.makay.entites.Utilisateur;
import com.app.makay.entites.statistiques.ChiffrePaiement;
// import com.app.makay.entites.statistiques.ChiffrePlace;
import com.app.makay.entites.statistiques.ChiffreProduit;
import com.app.makay.entites.statistiques.ChiffreSemaine;
import com.app.makay.utilitaire.Constantes;
import com.app.makay.utilitaire.MyDAO;
import com.app.makay.utilitaire.MyFilter;

import handyman.HandyManUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import veda.godao.utils.DAOConnexion;

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
    /*
     * select min(dateheure_ouverture) as datedebut from commandes where etat=20
     */
    @GetMapping("/tableau-bord")
    public Object tableauBord(HttpServletRequest req, Model model, String dateDebut, String dateFin, String idcategorie) throws SQLException, Exception{
        HttpSession session=req.getSession();
        Utilisateur utilisateur=(Utilisateur)session.getAttribute(Constantes.VAR_SESSIONUTILISATEUR);
        Object iris=filter.checkByRole(utilisateur, new String[]{Constantes.ROLE_ANALYSTE}, "Makay - Tableau de bord", "pages/analyste/tableau-bord", "layout/layout", model);
        if(utilisateur==null){
            return iris;
        }
        String dateDebut_controller=dateDebut;
        String dateFin_controller=dateFin;
        String idcategorie_controller=idcategorie;
        LocalDateTime dateDebutTime, dateFinTime;
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            if(dateDebut==null||dateDebut.isEmpty()||dateDebut.isBlank()){
                dateDebutTime=Commande.recupererPremiereDate(connect, dao);
            }else{
                dateDebutTime=LocalDateTime.parse(dateDebut_controller);
            }
            if(dateFin==null||dateFin.isEmpty()||dateFin.isBlank()){
                dateFinTime=LocalDateTime.now();
            }else{
                dateFinTime=LocalDateTime.parse(dateFin_controller);
            }
            if(idcategorie==null||idcategorie.isEmpty()||idcategorie.isBlank()){
                idcategorie_controller=String.valueOf(Categorie.recupererPremierID(connect, dao));
            }
            Object[] chiffres=Commande.statistiques(connect, dao, dateDebutTime, dateFinTime, idcategorie_controller);
            double[] chiffresTotales=Commande.chiffresTotales(connect, dao, dateDebutTime, dateFinTime);
            String[] totalsFormat=new String[chiffresTotales.length];
            for(int i=0;i<totalsFormat.length;i++){
                totalsFormat[i]=HandyManUtils.number_format(chiffresTotales[i], ' ', ',', 2)+" Ar";
            }
            model.addAttribute(Constantes.VAR_CHIFFRE_SEMAINE, (ChiffreSemaine[])chiffres[0]);
            model.addAttribute(Constantes.VAR_CHIFFRE_PRODUIT, (ChiffreProduit[])chiffres[1]);
            // model.addAttribute(Constantes.VAR_CHIFFRE_PLACE_VISITEES, (ChiffrePlace[])chiffres[2]);
            // model.addAttribute(Constantes.VAR_CHIFFRE_PLACE_NON_VISITEES, (ChiffrePlace[])chiffres[3]);
            model.addAttribute(Constantes.VAR_CHIFFRE_TOTAUX, totalsFormat);
            Categorie[] categories=dao.select(connect, Categorie.class);
            model.addAttribute(Constantes.VAR_CATEGORIES, categories);

            ChiffrePaiement chiffrePaiement=Paiement.chiffrePaiements(connect, dao, dateDebutTime, dateFinTime);
            model.addAttribute(Constantes.VAR_CHIFFRE_PAIEMENTS, chiffrePaiement);
        }
        model.addAttribute(Constantes.VAR_JOURS_SEMAINE, Constantes.JOURS_SEMAINE);
        model.addAttribute(Constantes.VAR_LINKS, utilisateur.recupererLinks());
        model.addAttribute(Constantes.VAR_IP, ip);
        return iris;
    }
}
