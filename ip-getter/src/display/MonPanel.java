package display;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class MonPanel extends JPanel{
    private BufferedImage image;
    private JTextField lien;

    public JTextField getLien() {
        return lien;
    }

    public void setLien(JTextField lien) {
        this.lien = lien;
    }

    public MonPanel(BufferedImage image, String link){
        super();
        setImage(image);
        setLien(new JTextField(link));
        getLien().setBorder(null);
        getLien().setEditable(false);
        getLien().setForeground(UIManager.getColor("Label.foreground"));
        getLien().setFont(UIManager.getFont("Label.font"));
        add(getLien());
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.drawImage(image, 0, getLien().getHeight()*2, this);
    }
    
}
