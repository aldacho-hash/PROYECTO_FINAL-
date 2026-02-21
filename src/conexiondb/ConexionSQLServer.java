package conexiondb;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ConexionSQLServer {
    
    private static final String URL = "jdbc:mysql://localhost:3306/LibreriaFanny?useSSL=false&serverTimezone=America/Lima";
    private static final String USER = "root";
    private static final String PASSWORD = "carranza15";
    
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
            return null;
        }
    }
    
    public static boolean authenticateCliente(String usuario, String contrasena) {
        String query = "SELECT * FROM cliente WHERE (usuario = ? OR correo = ?) AND contrasena = ?";
        try (Connection conn = getConnection();
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
        String query = "SELECT * FROM empleado WHERE (usuario = ? OR correo_electronico = ?) AND contrasena = ?";
        try (Connection conn = getConnection();
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
    
    public static boolean estaBloqueado(String usuario) {
        String query = "SELECT bloqueado, tiempo_bloqueo FROM intentos_login WHERE usuario = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, usuario);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int bloqueado = rs.getInt("bloqueado");
                System.out.println("DEBUG - usuario: " + usuario + " | bloqueado: " + bloqueado);
                if (bloqueado == 1) {
                    java.sql.Timestamp tiempoBloqueo = rs.getTimestamp("tiempo_bloqueo");
                    if (tiempoBloqueo == null) return true;
                    long diff = System.currentTimeMillis() - tiempoBloqueo.getTime();
                    System.out.println("DEBUG - segundos transcurridos: " + (diff / 1000));
                    if (diff > 5 * 60 * 1000) {
                        desbloquearUsuario(usuario);
                        return false;
                    }
                    return true;
                }
            } else {
                System.out.println("DEBUG - usuario NO encontrado: " + usuario);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
    
    public static void registrarIntentoFallido(String usuario) {
        String query = "INSERT INTO intentos_login (usuario, intentos) VALUES (?, 1) " +
                       "ON DUPLICATE KEY UPDATE intentos = intentos + 1";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, usuario);
            stmt.executeUpdate();

            PreparedStatement checkStmt = conn.prepareStatement(
                "SELECT intentos FROM intentos_login WHERE usuario = ?");
            checkStmt.setString(1, usuario);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt("intentos") >= 3) {
                PreparedStatement bloquearStmt = conn.prepareStatement(
                    "UPDATE intentos_login SET bloqueado = 1, tiempo_bloqueo = NOW() WHERE usuario = ?");
                bloquearStmt.setString(1, usuario);
                bloquearStmt.executeUpdate();
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
    
    public static void desbloquearUsuario(String usuario) {
        String query = "UPDATE intentos_login SET intentos = 0, bloqueado = 0, tiempo_bloqueo = NULL WHERE usuario = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, usuario);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    
    public static void registrarLogin(String usuario, String tipoUsuario) {
        String query = "INSERT INTO registro_login (usuario, fecha_hora, tipo_usuario) VALUES (?, NOW(), ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, usuario);
            stmt.setString(2, tipoUsuario);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static boolean tienePermiso(String cargo, String modulo) {
        String query = "SELECT puede_acceder FROM permisos WHERE LOWER(cargo) = LOWER(?) AND LOWER(modulo) = LOWER(?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, cargo);
            stmt.setString(2, modulo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("puede_acceder") == 1;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public static boolean usuarioExiste(String usuario) {
        String query = "SELECT 1 FROM usuarios WHERE usuario = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, usuario);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public static String generarCodigo() {
        int codigo = (int)(Math.random() * 900000) + 100000;
        return String.valueOf(codigo);
    }

    public static void enviarCorreoRecuperacion(String correoDestino, String codigo) {
        String correoOrigen = "llantahulpa@gmail.com";
        String contraseñaApp = "wwae jntj ywop vyrw";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(correoOrigen, contraseñaApp);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(correoOrigen));
            message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(correoDestino));
            message.setSubject("Recuperación de contraseña - Librería Fanny");
            message.setText("Tu código de recuperación es: " + codigo + "\nEste código expira en 10 minutos.");
            Transport.send(message);
            System.out.println("Correo enviado correctamente.");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    public static void guardarCodigoRecuperacion(String correo, String codigo) {
    String query = "INSERT INTO recuperacion_contrasena (correo, codigo, fecha_expiracion) " +
                   "VALUES (?, ?, DATE_ADD(NOW(), INTERVAL 10 MINUTE))";
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, correo);
        stmt.setString(2, codigo);
        stmt.executeUpdate();
    } catch (SQLException e) { e.printStackTrace(); }
}

public static boolean verificarCodigo(String correo, String codigo) {
    String query = "SELECT * FROM recuperacion_contrasena WHERE correo = ? AND codigo = ? " +
                   "AND fecha_expiracion > NOW() AND usado = 0";
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, correo);
        stmt.setString(2, codigo);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    } catch (SQLException e) { e.printStackTrace(); }
    return false;
}

public static void cambiarContrasena(String correo, String nuevaContrasena) {
    String queryCliente = "UPDATE cliente SET contrasena = ? WHERE correo = ?";
    String queryEmpleado = "UPDATE empleado SET contrasena = ? WHERE correo_electronico = ?";
    String queryUsuarios = "UPDATE usuarios SET contrasena = ? WHERE email = ?";
    try (Connection conn = getConnection()) {
        PreparedStatement s1 = conn.prepareStatement(queryCliente);
        s1.setString(1, nuevaContrasena);
        s1.setString(2, correo);
        s1.executeUpdate();

        PreparedStatement s2 = conn.prepareStatement(queryEmpleado);
        s2.setString(1, nuevaContrasena);
        s2.setString(2, correo);
        s2.executeUpdate();

        PreparedStatement s3 = conn.prepareStatement(queryUsuarios);
        s3.setString(1, nuevaContrasena);
        s3.setString(2, correo);
        s3.executeUpdate();

        PreparedStatement s4 = conn.prepareStatement(
            "UPDATE recuperacion_contrasena SET usado = 1 WHERE correo = ?");
        s4.setString(1, correo);
        s4.executeUpdate();

    } catch (SQLException e) { e.printStackTrace(); }
}
}