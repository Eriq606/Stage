package com.app.makay.entites.REST;

import java.util.Arrays;

import com.app.makay.entites.Commande;
import com.app.makay.entites.CommandeFille;

public class EnvoiCommandeREST extends ObjectREST{
    private Commande commande;
    private CommandeFille[] commandeFilles;
    public Commande getCommande() {
        return commande;
    }
    public void setCommande(Commande commande) {
        this.commande = commande;
    }
    public CommandeFille[] getCommandeFilles() {
        return commandeFilles;
    }
    public void setCommandeFilles(CommandeFille[] commandeFilles) {
        this.commandeFilles = commandeFilles;
    }
    @Override
    public String toString() {
        return "EnvoiCommandeREST [commande=" + commande + ", commandeFilles=" + Arrays.toString(commandeFilles) + "]";
    }
    
}
