package backend.config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Conexion {

    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USUARIO = "";
    private static final String PASSWORD = "";
    private static final String ESQUEMA = "\"TBD deporte\"";

    public static Connection conectar() {
        try {
            Connection conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            try (Statement st = conexion.createStatement()) {
                st.execute("SET search_path TO " + ESQUEMA);
            }
            return conexion;
        } catch (Exception e) {
            System.out.println("Error de conexión: " + e.getMessage());
            return null;
        }
    }

    public static int obtenerPid(Connection conexion) {
        try (PreparedStatement ps = conexion.prepareStatement("SELECT pg_backend_pid()");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            System.out.println("Error obteniendo PID: " + e.getMessage());
        }
        return -1;
    }
}