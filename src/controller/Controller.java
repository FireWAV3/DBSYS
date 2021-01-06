package controller;
import gui.SwingGui;

import java.sql.*;
import java.util.ArrayList;

public class Controller {
    private SwingGui gui;
    Connection conn = null;
    Statement stmt = null;
    PreparedStatement booking;
    PreparedStatement search;
    PreparedStatement land;
    PreparedStatement ausstattung;
    PreparedStatement login;
    PreparedStatement wohnunginfo;

    public Controller(String name, String password) throws Exception {

        try {

            //TODO dosen't work
            //DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
            String url = "jdbc:oracle:thin:@oracle19c.in.htwg-konstanz.de:1521:ora19c"; // String für DB-Connection
            conn = DriverManager.getConnection(url, name, password);
            stmt = conn.createStatement();

        } catch (Exception e){
            throw new IllegalArgumentException(e.getMessage() + "\nNot able to connect");
        }

        try {
            //Land,Ausstattung,Land,Ausstattung,Land,Ausstattung,Land,Ausstattung, startD, endD, startD, endD, startD, endD
            //Search sql command
            search = conn.prepareStatement("SELECT DISTINCT fwo.fName, x.SternDurchschnitt as SternDurchschnitt\n" +
                    " FROM (\n" +
                    "    (\n" +
                    "    SELECT DISTINCT Name, AVG(SternU) as SternDurchschnitt\n" +
                    "        FROM(\n" +
                    "                    SELECT fw.fName as Name , bu.anzSterne as SternU\n" +
                    "                    FROM dbsys03.Ferienwohnung fw ,dbsys03.Adresse ad, dbsys03.ausgestattet au ,dbsys03.Buchung bu\n" +
                    "                    WHERE fw.fID = au.fID\n" +
                    "                    AND   fw.aID = ad.aID\n" +
                    "                    AND   fw.fID = bu.fID\n" +
                    "                    AND   ad.land =?\n" +
                    "                    AND   au.aName =?\n" +
                    "        \n" +
                    "            UNION\n" +
                    "                        SELECT  fw.fName as Name , 0 as SternU\n" +
                    "                        FROM dbsys03.Ferienwohnung fw ,dbsys03.Adresse ad, dbsys03.ausgestattet au\n" +
                    "                        WHERE fw.fID = au.fID\n" +
                    "                        AND   fw.aID = ad.aID\n" +
                    "                        AND   ad.land =?\n" +
                    "                        AND   au.aName =?\n" +
                    "                 MINUS\n" +
                    "                         SELECT fw.fName as Name , 0 as SternU\n" +
                    "                        FROM dbsys03.Ferienwohnung fw ,dbsys03.Adresse ad, dbsys03.ausgestattet au ,dbsys03.Buchung bu\n" +
                    "                        WHERE fw.fID = au.fID\n" +
                    "                        AND   fw.aID = ad.aID\n" +
                    "                        AND   fw.fID = bu.fID\n" +
                    "                        AND   ad.land =?\n" +
                    "                        AND   au.aName =?\n" +
                    "             )\n" +
                    "        GROUP BY Name        \n" +
                    "    )\n" +
                    "    MINUS\n" +
                    "    (\n" +
                    "            SELECT  Name as Name , AVG(bu.anzSterne) as SternDurchschnitt  \n" +
                    "                 FROM (\n" +
                    "                    SELECT fw.fName as Name\n" +
                    "                        FROM dbsys03.Ferienwohnung fw ,dbsys03.Buchung bu,dbsys03.Adresse ad, dbsys03.ausgestattet au\n" +
                    "                        WHERE  fw.fID = bu.fID\n" +
                    "                        AND fw.fID = au.fID\n" +
                    "                        AND fw.aID = ad.aID\n" +
                    "                        AND ad.land =?\n" +
                    "                        AND au.aName =?\n" +
                    "                        AND (bu.startDate BETWEEN ? AND ?\n" +
                    "                          OR bu.endDate BETWEEN ? AND ?)\n" +
                    "                        OR (bu.startDate <= ? AND bu.endDate >= ?)\n" +
                    "                        GROUP BY fw.fName\n" +
                    "                )x\n" +
                    "            ,dbsys03.Buchung bu\n" +
                    "            ,dbsys03.Ferienwohnung fw\n" +
                    "        WHERE x.Name = fw.fName\n" +
                    "        AND bu.fID = fw.fID\n" +
                    "        GROUP BY Name\n" +
                    "     )) x\n" +
                    "    , dbsys03.Ferienwohnung fwo\n" +
                    "    WHERE fwo.fName = x.Name\n" +
                    "ORDER BY x.SternDurchschnitt DESC\n" +
                    "    ; commit;");

            //booking sql command
            //mail,fID,startD,endD
            booking = conn.prepareStatement(
                    "INSERT INTO dbsys03.Buchung(buchungsNr,email,fID,bDatum,startDate,endDate) " +
                        "VALUES ((SELECT ISNULL(MAX(dbsys03.buchungsNr) + 1, 1) FROM Buchung),?,?,CURRENT_DATE,?,?);" +
                            "commit;");

            //get länder sql command
            land = conn.prepareStatement("SELECT land From dbsys03.Land;");

            //get ausstattung sql command
            ausstattung = conn.prepareStatement("SELECT aName FROM dbsys03.Ausstattung;");

            //get login sql command
            login = conn.prepareStatement("SELECT count(email) FROM dbsys03.Kunde\n" +
                                                    "WHERE email =?\n" +
                                                    "AND  passwort =?;");

            //get wohnug info sql command
            wohnunginfo = conn.prepareStatement("SELECT fname , anzZimmer, fSize, fPrize, ortsname, fid\n" +
                                                    "FROM dbsys03.Adresse ad, dbsys03.Ferienwohnung f\n" +
                                                    "WHERE ad.aID = f.aID \n" +
                                                    "AND fName = ?;");

        }catch (SQLException e){
            throw new IllegalArgumentException(e.getMessage() + "\nNot able to compile a sql command");
        }
    }


  public PreparedStatement getBooking(){return booking;}
  public PreparedStatement getSearch(){return search;}

  public boolean setBoocking(String mail, String fID, String stratD, String endD) {
        try {
            booking.setString(1,mail);
            booking.setString(2,fID);
            booking.setString(3,stratD);
            booking.setString(4,endD);

            booking.executeQuery();
            return true;
        }catch (SQLException throwables){
            try {
                conn.rollback();
            } catch (SQLException e) {
                System.out.println("rollback Failed");
            }
            return false;
        }
  }

  public boolean setSearch(String land,String ausstattung,String startD, String endD){
      try {
          search.setString(1,land);
          search.setString(2,ausstattung);
          search.setString(3,land);
          search.setString(4,ausstattung);
          search.setString(5,land);
          search.setString(6,ausstattung);
          search.setString(7,land);
          search.setString(8,ausstattung);

          search.setString(9,startD);
          search.setString(10,endD);
          search.setString(11,startD);
          search.setString(12,endD);
          search.setString(13,startD);
          search.setString(14,endD);

          ResultSet rs = search.executeQuery();
          gui.clearSearch();

          while (rs.next()){
              updateGuiSearch(rs.getString(1),rs.getInt(2));
          }
          return true;
      } catch (SQLException throwables) {
          try {
              conn.rollback();
          } catch (SQLException e) {
              System.out.println("rollback Failed");
          }
          return false;
      }
  }

    private void updateGuiSearch(String string, int sterne) {
        try{
            wohnunginfo.setString(1,string);

            ResultSet rs = wohnunginfo.executeQuery();
            gui.updateSearch(rs.getString(1),rs.getInt(2),
                    rs.getInt(3),rs.getInt(4),rs.getString(5),
                    rs.getString(6),sterne);
        }catch (SQLException throwables){
            System.out.println("");
        }
    }

    public Object[] getLand() throws SQLException {
        ArrayList<String> temp = new ArrayList<String>();
        try {
            ResultSet rs = land.executeQuery();
            while (rs.next()) {
                temp.add(rs.getString("land"));
            }
        } catch (SQLException throwables) {
            String temp2[] = {"test","test"};
            return temp2;
        }
        return temp.toArray();
    }

    public Object[] getAusstattung() throws SQLException {
        ArrayList<String> temp = new ArrayList<String>();
        try {

            ResultSet rs = ausstattung.executeQuery();

            while (rs.next()){
                temp.add(rs.getString("aName"));
                System.out.println(rs.getString("aName"));
            }
        } catch (SQLException throwables) {
            String temp2[] = {"test","test"};
            return temp2;
        }
        return temp.toArray();
    }

    public boolean testLogin(String text, char[] password) {
        try {
            ResultSet rs = login.executeQuery();
            if(rs.getInt(1) == 1){
                return true;
            }else return false;
        }catch (SQLException throwables){
            return false;
        }
    }

    public void setGui(SwingGui gui){
        this.gui = gui;
    }
}
