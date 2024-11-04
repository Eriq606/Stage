package display;

import java.awt.Dimension;

import javax.swing.JFrame;

public class MaFenetre extends JFrame{
    public MaFenetre(String ip){
        super(ip);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(500, 480));
    }
}
