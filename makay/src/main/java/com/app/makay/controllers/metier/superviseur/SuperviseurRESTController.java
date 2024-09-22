package com.app.makay.controllers.metier.superviseur;

import java.sql.Connection;
import java.sql.SQLException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.app.makay.entites.REST.ModificationRoleREST;
import com.app.makay.utilitaire.Constantes;
import com.app.makay.utilitaire.MyDAO;
import com.app.makay.utilitaire.MyFilter;
import com.app.makay.utilitaire.ReponseREST;
import com.app.makay.utilitaire.RestData;
import handyman.HandyManUtils;
import veda.godao.utils.DAOConnexion;

@RestController
@CrossOrigin(origins = {"*"})
public class SuperviseurRESTController {
    private MyDAO dao;
    private MyFilter filter;
    
    public SuperviseurRESTController() {
        dao=new MyDAO();
        filter=new MyFilter();
    }

    @PostMapping("/attribution-role-rest")
    public ReponseREST modifierAttributionsRole(@RequestBody RestData datas) throws SQLException, Exception{
        ReponseREST response=new ReponseREST();
        ModificationRoleREST modifs=HandyManUtils.fromJson(ModificationRoleREST.class, datas.getRestdata());
        try(Connection connect=DAOConnexion.getConnexion(dao)){
            response=filter.checkByRoleREST(modifs, connect, dao, new String[]{Constantes.ROLE_SUPERVISEUR});
            if(response.getCode()==Constantes.CODE_ERROR){
                return response;
            }
            modifs.getUtilisateur().mettreAJourAttributionsRoles(connect, dao, modifs.getAttributions());
            connect.commit();
            return response;
        }catch(Exception e){
            response.setCode(Constantes.CODE_ERROR);
            response.setMessage(e.getMessage());
            return response;
        }
    }
}
