package com.app.makay.iris;

import java.sql.Connection;

import org.springframework.ui.Model;
import org.springframework.web.servlet.view.RedirectView;

import com.app.makay.entites.REST.ObjectREST;

import veda.godao.DAO;

public interface IrisFilter {
    public abstract IrisRESTResponse checkByRoleREST(ObjectREST modifs, Connection connect, DAO dao, String[] roles) throws Exception;
    public abstract Object checkIfLoggedIn(IrisUser irisUser, String title, String viewpage, String targetView, Model model);
    public abstract Object checkByRole(IrisUser irisUser, String targetRole, String title, String viewpage, String targetView, Model model);
    public abstract Object checkByRole(IrisUser irisUser, String[] targetRole, String title, String viewpage, String targetView, Model model);
    public abstract Object checkByAuthorization(IrisUser irisUser, int minimumAuth, String title, String viewpage, String targetView, Model model);
    public abstract RedirectView checkIfLoggedInPOST(IrisUser irisUser);
    public abstract RedirectView checkByRolePOST(IrisUser irisUser, String targetRole);
    public abstract RedirectView checkByRolePOST(IrisUser irisUser, String[] targetRole);
    public abstract RedirectView checkByAuthorizationPOST(IrisUser irisUser, int minimumAuth);
    public abstract RedirectView distributeByRole(IrisUser irisUser);
    public abstract RedirectView distributeByRoleToResetCache(IrisUser irisUser);
    public abstract RedirectView distributeByAuthorization(IrisUser irisUser);
}
