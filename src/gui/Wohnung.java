package gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Wohnung extends JPanel implements ActionListener {

    private JLabel jname;
    private JLabel jzimmer;
    private JLabel jsize;
    private JLabel jprice;
    private JLabel jstadt;
    private JLabel jsterne;
    private JButton jbuchen;

    private String name;
    private String zimmer;
    private String wsize;
    private String price;
    private String stadt;
    private String sterne;
    private String fID;

    private SwingGui gui;

    private FlowLayout flowLayout = new FlowLayout();


    public Wohnung(String fID, String name, String zimmer,String wsize,String price,String stadt,String sterne,SwingGui gui){
        this.name = name;
        this.zimmer = "Anzahl Zimmer: " + zimmer;
        this.wsize = "Größe: " + wsize;
        this.price = "Preis pro Nacht: " + price+" €";
        this.stadt = "in: " + stadt;
        if(sterne.equals("-1")) {
            this.sterne = "Sterne: -";
        }else{
            this.sterne = "Sterne: " + sterne;
        }


        this.fID = fID;
        this.gui = gui;


        jname = new JLabel(this.name);
        jname.setFont(new Font("Helvetica", Font.BOLD, 16));
        jzimmer = new JLabel(this.zimmer);
        jsize = new JLabel(this.wsize);
        jprice = new JLabel(this.price);
        jstadt = new JLabel(this.stadt);
        jsterne = new JLabel(this.sterne);
        jbuchen = new JButton("buchen");
        jbuchen.addActionListener(this);

        jbuchen.setBackground(new Color(255, 130, 0));

        jname.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
        jzimmer.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
        jsize.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
        jprice.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
        jstadt.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
        jsterne.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
        jbuchen.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));

        this.add(jname);
        this.add(jzimmer);
        this.add(jzimmer);
        this.add(jprice);
        this.add(jstadt);
        this.add(jsterne);
        this.add(jbuchen);

        this.setLayout(flowLayout);
        this.setVisible(true);
        this.setBackground(new Color(0, 205, 255));
        this.setBorder(BorderFactory.createLineBorder(new Color(0, 78, 146),10));
        this.setMaximumSize(new Dimension(1080,150));
        this.setMinimumSize(new Dimension(1080,150));

        jbuchen.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
        setVisible(true);
    }

    private void updateBuchen(boolean input) {
        jbuchen.setVisible(input);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == jbuchen)
            gui.buchen(fID);
            gui.updateSearchGlobal();
    }
}
