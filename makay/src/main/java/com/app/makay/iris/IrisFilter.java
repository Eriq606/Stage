package com.app.makay.iris;

import org.springframework.ui.Model;
import org.springframework.web.servlet.view.RedirectView;

public interface IrisFilter {
    public abstract Object checkByRole(IrisUser irisUser, String targetRole, String title, String viewpage, String targetView, Model model);
    public abstract Object checkByAuthorization(IrisUser irisUser, int minimumAuth, String title, String viewpage, String targetView, Model model);
    public abstract RedirectView distributeByRole(IrisUser irisUser);
    public abstract RedirectView distributeByAuthorization(IrisUser irisUser);
}
