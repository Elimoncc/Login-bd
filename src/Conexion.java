import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Conexion {

    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USUARIO = "postgres";
    private static final String PASSWORD = "1313AB!";

    public static Connection conectar() {
        try {
            Connection conexion = DriverManager.getConnection(
                URL,
                USUARIO,
                PASSWORD
            );
            return conexion;
        } catch (Exception e) {
            System.out.println("Error de conexión:");
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static int obtenerPid(Connection conexion) {
        String sql = "SELECT pg_backend_pid()";
        try (
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Error obteniendo PID:");
            System.out.println(e.getMessage());
        }
        return -1;
    }
}