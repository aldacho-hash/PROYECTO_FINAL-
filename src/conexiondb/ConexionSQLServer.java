package conexiondb;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConexionSQLServer {
    
    public static Connection getConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/LibreriaFanny?useSSL=false&serverTimezone=UTC";
            String user = "root";
            String password = "carranza15";
            
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
            return null;
        }
    }
    
    public static boolean authenticateCliente(String usuario, String contrasena) {
        String connectionString = "jdbc:mysql://localhost:3306/LibreriaFanny?useSSL=false&serverTimezone=UTC";
        String query = "SELECT * FROM cliente WHERE (usuario = ? OR correo = ?) AND contrasena = ?";
        
        try (Connection conn = DriverManager.getConnection(connectionString, "root", "carranza15");
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, usuario);
            stmt.setString(2, usuario);
            stmt.setString(3, contrasena);
            
            ResultSet rs = stmt.executeQuery();
            return rs.next();
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al verificar credenciales de cliente: " + e.getMessage());
        }
        return false;
    }
     
    public static boolean authenticateEmpleado(String usuario, String contrasena) {
        String connectionString = "jdbc:mysql://localhost:3306/LibreriaFanny?useSSL=false&serverTimezone=UTC";
        String query = "SELECT * FROM empleado WHERE (usuario = ? OR correo_electronico = ?) AND contrasena = ?";
        
        try (Connection conn = DriverManager.getConnection(connectionString, "root", "carranza15");
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, usuario);
            stmt.setString(2, usuario);
            stmt.setString(3, contrasena);
            
            ResultSet rs = stmt.executeQuery();
            return rs.next();
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al verificar credenciales de empleado: " + e.getMessage());
        }
        return false;
    }
}