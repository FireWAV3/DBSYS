package gui;
import controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;


//Tom.Gaertner@temp.com
//sesh#123
public class SwingGui extends JFrame implements ActionListener {


    public static void main(String[] args) throws Exception {

        try {
            new SwingGui(new Controller("dbsys43", "dbsys43#"));
        }catch (Exception e){
            System.out.println(e.getMessage()+" in Controller");
        }
    }

    private  Controller c;

    //div Panel
    private JPanel mainPanel;
    private JPanel login;
    private JPanel options;
    private JPanel results;
    private JScrollPane resultsscroll;

    //layouts
    private FlowLayout flowLayoutlogin = new FlowLayout();
    private FlowLayout flowLayoutoption = new FlowLayout();



    //login Components
    private TextField jEmail;
    private JPasswordField jpasswd;
    private JButton jloginbutton;
    private JLabel jlogininfo;

    //options Components
    private JComboBox jland;
    private JTextField jarrival;
    private JTextField jdeparture;
    private JComboBox jausstattung;
    private JButton jsearch;

    private boolean looged = false;
    private String logEmail = "";



    public SwingGui(Controller pC) throws SQLException {
        c = pC;
        c.setGui(this);
        this.setTitle("DBSYS Datenbank");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        login = new JPanel();
        options = new JPanel();
        results = new JPanel();
        resultsscroll = new JScrollPane();


        jEmail = new TextField("Tom.Gaertner@temp.com");
        jEmail.setMinimumSize(new Dimension(250,jEmail.getSize().height ));
        jpasswd = new JPasswordField("sesh#123");
        jpasswd.setMinimumSize(new Dimension( 200,jpasswd.getSize().height));
        jpasswd.setSize(200,jpasswd.getSize().height);
        jloginbutton = new JButton("login");
        jloginbutton.addActionListener(this);
        jlogininfo = new JLabel("not logged in");

        login.add(jEmail);
        login.add(jpasswd);
        login.add(jloginbutton);
        login.add(jlogininfo);
        login.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
        login.setLayout(flowLayoutlogin);
        login.setMaximumSize(new Dimension(800,100));
        login.setMinimumSize(new Dimension(800,100));

        jland = new JComboBox(c.getLand());
        jarrival = new JTextField("01-11-2021");
        jarrival.setMinimumSize(new Dimension( 300,jarrival.getSize().height));
        jdeparture = new JTextField("21-11-2021");
        jdeparture.setMinimumSize(new Dimension(300,jdeparture.getSize().height));
        jausstattung = new JComboBox(c.getAusstattung());
        jsearch = new JButton("Search");
        jsearch.addActionListener(this);


        options.add(jland);
        options.add(jarrival);
        options.add(jdeparture);
        options.add(jausstattung);
        options.add(jsearch);
        options.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
        options.setLayout(flowLayoutoption);
        options.setMaximumSize(new Dimension(800,100));
        options.setMinimumSize(new Dimension(800,100));

        results.setBackground(new Color(166, 251, 255));
        results.setSize(400,400);
        results.setMinimumSize(new Dimension(400,400));

        results.setLayout(new BoxLayout(results,BoxLayout.Y_AXIS));
        results.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
        resultsscroll.setViewportView (results);

        mainPanel = new JPanel();

        mainPanel.add(login);
        mainPanel.add(options);
        mainPanel.add(resultsscroll);

        BoxLayout boxLayout = new BoxLayout(mainPanel,BoxLayout.Y_AXIS);
        mainPanel.setLayout(boxLayout);

        setContentPane(mainPanel);
        this.pack();
        this.setVisible(true);
        this.setSize(1100,800);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (jloginbutton.equals(source)) {
            if(c.testLogin(jEmail.getText(),jpasswd.getPassword())){
                logEmail = jEmail.getText();
                looged = true;
                jlogininfo.setText("logged in as:"+logEmail);
            }else {
                JOptionPane.showMessageDialog(this, "Login Failed");
            }
        } else if (jsearch.equals(source)) {
            updateSearchGlobal();
        }
    }

    public void updateSearch(String pName,int pZimmer,int pSize, int pPrice,String pStadt,String pFID,String pSterne){
        System.out.println("udate gui:"+pName+" "+pZimmer+" "+pSize+" "+pPrice+" "+pStadt+" "+pSterne+" ID:"+pFID);

        results.add(new Wohnung(pFID,
                pName,
                Integer.toString(pZimmer),
                Integer.toString(pSize),
                Integer.toString(pPrice),
                pStadt,
                pSterne,this));

    }

    public void buchen(String fID) {
        if(looged){
            c.setBoocking(logEmail,fID,jarrival.getText(),jdeparture.getText());
        }else JOptionPane.showMessageDialog(this, "Login first");
    }

    public void clearSearch() {
        results.removeAll();
    }

    public void updateSearchGlobal() {
        if (!c.setSearch(jland.getSelectedItem().toString(), jausstattung.getSelectedItem().toString(), jarrival.getText(), jdeparture.getText())) {
            JOptionPane.showMessageDialog(this, "Search Failed");
        }
        results.repaint();
        this.repaint();
    }
}