package com.app.makay.iris;

import org.springframework.web.servlet.view.RedirectView;

import jakarta.servlet.http.HttpSession;

public interface IrisFilter {
    public abstract Object checkByRole(HttpSession session, IrisUser irisUser);
    public abstract Object checkByAuthorization(HttpSession session, IrisUser irisUser);
    public abstract RedirectView distributeByRole(IrisUser irisUser);
    public abstract RedirectView distributeByAuthorization(IrisUser irisUser);
}
