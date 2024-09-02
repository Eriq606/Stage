package com.app.makay.controllers.metier.superviseur;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.app.makay.entites.ModificationRoleREST;
import com.app.makay.utilitaire.MyDAO;
import com.app.makay.utilitaire.RestData;

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
    public void modifierAttributionsRole(@RequestBody RestData datas) throws SQLException, Exception{
        ModificationRoleREST modifs=HandyManUtils.fromJson(ModificationRoleREST.class, datas.getRestdata());
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            modifs.getUtilisateur().mettreAJourAttributionsRoles(connect, dao, modifs.getAttributions());
            connect.commit();
        }
    }
}
