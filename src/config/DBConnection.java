package config;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
  public static Connection getConnection() {
    Connection conn = null;
    try {
      conn =
          DriverManager.getConnection(
              "jdbc:h2:tcp://localhost/C:\\Users\\Astrax\\Desktop\\��\\��� � ��\\H2",
              "hbstudent",
              "hbstudent");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return conn;
  }
}
