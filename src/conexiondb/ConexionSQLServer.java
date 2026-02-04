package conexiondb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConexionSQLServer 
{
     public static Connection getConnection() 
    {
        try 
        {
            String url = "jdbc:sqlserver://localhost:1433;databaseName=BD_TPOO;encrypt=false";
            String user = "lucianoadm";
            String password = "hilario123";
            
            return DriverManager.getConnection(url, user, password);
        } 
        catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
            return null;
        }
    }
    public static boolean authenticateCliente(String usuario, String contraseña) {
    String connectionString = "jdbc:sqlserver://localhost:1433;databaseName=BD_TPOO;encrypt=false";
    String query = "SELECT * FROM usuarios WHERE (usuario = ? OR email = ?) AND contraseña = ? AND tipo_usuario = ?";
    try (Connection conn = DriverManager.getConnection(connectionString, "lucianoadm", "hilario123");
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setString(1, usuario);
        stmt.setString(2, usuario);
        stmt.setString(3, contraseña);
        stmt.setString(4, "cliente");  

        ResultSet rs = stmt.executeQuery();
        
        if (rs.next()) {
            return true;
        }
    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("Error al verificar las credenciales: " + e.getMessage());
    }
    return false;
}
     
   public static boolean authenticateEmpleado(String usuario, String contraseña) {
    String connectionString = "jdbc:sqlserver://localhost:1433;databaseName=BD_TPOO;encrypt=false";
    String query = "SELECT * FROM usuarios WHERE (usuario = ? OR email = ?) AND contraseña = ? AND tipo_usuario = ?";
    try (Connection conn = DriverManager.getConnection(connectionString, "lucianoadm", "hilario123");
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setString(1, usuario);
        stmt.setString(2, usuario);
        stmt.setString(3, contraseña);
        stmt.setString(4, "empleado"); 
        ResultSet rs = stmt.executeQuery();
        
        if (rs.next()) {
            return true;
        }
    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("Error al verificar las credenciales: " + e.getMessage());
    }
    return false;
}
}
