package com.app.makay.controllers.metier.barman;

import java.io.IOException;

import org.springframework.stereotype.Controller;

import com.app.makay.entites.Produit;
import com.app.makay.utilitaire.MyDAO;
import com.app.makay.utilitaire.MyFilter;

import handyman.HandyManUtils;

@Controller
public class BarmanController {
    private MyFilter filter;
    private MyDAO dao;
    private String ip;
    private Produit[] produits;
    public MyFilter getFilter() {
        return filter;
    }
    public void setFilter(MyFilter filter) {
        this.filter = filter;
    }
    public MyDAO getDao() {
        return dao;
    }
    public void setDao(MyDAO dao) {
        this.dao = dao;
    }
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public Produit[] getProduits() {
        return produits;
    }
    public void setProduits(Produit[] produits) {
        this.produits = produits;
    }
    public BarmanController() throws IOException {
        filter=new MyFilter();
        dao=new MyDAO();
        ip=HandyManUtils.getIP();
    }
    
    
}
