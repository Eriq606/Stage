package com.app.makay.utilitaire.exception;

import java.util.LinkedList;

public class StockException extends Exception{
    private LinkedList<Exception> exceptions;

    public LinkedList<Exception> getExceptions() {
        return exceptions;
    }

    public void setExceptions(LinkedList<Exception> exceptions) {
        this.exceptions = exceptions;
    }

    public StockException(LinkedList<Exception> exceptions) {
        this.exceptions = exceptions;
    }
    
}
