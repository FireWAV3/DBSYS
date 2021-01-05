package gui;
import controller.Controller;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

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


        jEmail = new TextField("enter Mail");
        jEmail.setMinimumSize(new Dimension(jEmail.getSize().height ,250));
        jpasswd = new JPasswordField("*****");
        jpasswd.setMinimumSize(new Dimension(jpasswd.getSize().height ,200));
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
        jarrival = new JTextField("00-00-0000");
        jarrival.setMinimumSize(new Dimension(jarrival.getSize().height ,300));
        jdeparture = new JTextField("00-00-0000");
        jdeparture.setMinimumSize(new Dimension(jdeparture.getSize().height ,300));
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

       results.add(new Wohnung("2","name","500","1","infinit","Konne","5",this));
        results.add(new Wohnung("3","der name","5","100000","2","Konne","2",this));
        results.add(new Wohnung("4","der dasd","5","100000","2","Konne","3.5",this));


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

    public void updateSearch(String pName,int pZimmer,int pSize, int pPrice,String pStadt,String pFID,int pSterne){

        results.add(new Wohnung(pFID,
                pName,
                Integer.toString(pZimmer),
                Integer.toString(pSize),
                Integer.toString(pPrice),
                pStadt,
                Integer.toString(pSterne),this));
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
    }
}