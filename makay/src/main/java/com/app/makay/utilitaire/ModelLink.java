package com.app.makay.utilitaire;

public class ModelLink {
    private String link;
    private String icon;
    private String text;
    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }
    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public ModelLink(String link, String icon, String text) {
        this.link = link;
        this.icon = icon;
        this.text = text;
    }
    
}
