package com.app.makay.utilitaire;

import java.io.IOException;

import handyman.HandyManUtils;
import veda.godao.DAO;

public class MyDAO extends DAO{
    public MyDAO() throws IOException{
        String credFile=HandyManUtils.getFileContent("database.json");
        DatabaseCredentials creds=HandyManUtils.fromJson(DatabaseCredentials.class, credFile);
        setAllowKeyRetrieval(true);
        setDatabase(creds.getDatabase());
        setDriver("org.postgresql.Driver");
        setHost(creds.getHost());
        setPort(creds.getPort());
        setPwd(creds.getPassword());
        setServer("postgresql");
        setSgbd(veda.godao.utils.Constantes.PSQL_ID);
        setUseSSL(false);
        setUser(creds.getUser());
    }
}
