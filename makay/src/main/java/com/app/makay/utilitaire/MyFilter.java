package com.app.makay.utilitaire;

import org.springframework.web.servlet.view.RedirectView;

import com.app.makay.iris.IrisFilter;
import com.app.makay.iris.IrisUser;

import jakarta.servlet.http.HttpSession;

public class MyFilter implements IrisFilter{

    @Override
    public Object checkByRole(HttpSession session, IrisUser irisUser) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'checkByRole'");
    }

    @Override
    public Object checkByAuthorization(HttpSession session, IrisUser irisUser) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'checkByAuthorization'");
    }

    @Override
    public RedirectView distributeByRole(IrisUser irisUser) {
        return new RedirectView("/login-success");
    }

    @Override
    public RedirectView distributeByAuthorization(IrisUser irisUser) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'distributeByAuthorization'");
    }
    
}
