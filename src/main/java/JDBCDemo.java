import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;


public class JDBCDemo {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        try (Connection connection = JDBC.getConnection()) {
            JDBC.consoleMenu(scan, connection);
        } catch (SQLException e) {
            System.err.println("Failed connection: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Failed properties: " + e.getMessage());
        }
    }
}
