package com.app.makay.controllers.metier.superviseur;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.app.makay.entites.REST.ModificationRoleREST;
import com.app.makay.utilitaire.Constantes;
import com.app.makay.utilitaire.MyDAO;
import com.app.makay.utilitaire.ReponseREST;
import com.app.makay.utilitaire.RestData;
import com.app.makay.utilitaire.SessionUtilisateur;

import handyman.HandyManUtils;
import veda.godao.utils.DAOConnexion;

@RestController
@CrossOrigin(origins = {"*"})
public class SuperviseurRESTController {
    private MyDAO dao;
    
    public SuperviseurRESTController() {
        dao=new MyDAO();
    }

    @PostMapping("/attribution-role-rest")
    public ReponseREST modifierAttributionsRole(@RequestBody RestData datas) throws SQLException, Exception{
        ReponseREST response=new ReponseREST();
        ModificationRoleREST modifs=HandyManUtils.fromJson(ModificationRoleREST.class, datas.getRestdata());
        SessionUtilisateur where=new SessionUtilisateur();
        where.setSessionId(modifs.getSessionid());
        where.setUtilisateur(modifs.getUtilisateur());
        where.setEstValide(Constantes.SESSION_ESTVALIDE);
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            SessionUtilisateur[] sessionUser=dao.select(connect, SessionUtilisateur.class, where);
            if(sessionUser.length!=1){
                response.setMessage(Constantes.MSG_UTILISATEUR_NON_AUTHENTIFIE);
                return response;
            }
            if(sessionUser[0].getExpiration().isBefore(LocalDateTime.now())){
                response.setMessage(Constantes.MSG_SESSION_EXPIREE);
                return response;
            }
            modifs.getUtilisateur().mettreAJourAttributionsRoles(connect, dao, modifs.getAttributions());
            connect.commit();
            response.setMessage(Constantes.MSG_SUCCES);
            return response;
        }
    }
}
