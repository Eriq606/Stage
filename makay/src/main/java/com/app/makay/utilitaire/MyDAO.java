package com.app.makay.utilitaire;

import veda.godao.DAO;

public class MyDAO extends DAO{
    public MyDAO(){
        setAllowKeyRetrieval(true);
        setDatabase("makay2");
        setDriver("org.postgresql.Driver");
        setHost("localhost");
        setPort("5432");
        setPwd("root");
        setServer("postgresql");
        setSgbd(veda.godao.utils.Constantes.PSQL_ID);
        setUseSSL(false);
        setUser("eriq");
    }
}
