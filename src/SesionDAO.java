import java.sql.Connection;
import java.sql.PreparedStatement;

public class SesionDAO {

    public boolean crearSesion(int idUser, int pid) {
        String sql = """
            INSERT INTO seguridad.sesion
            (id_userN, activo, pid)
            VALUES (?, TRUE, ?)
            """;
        try (
            Connection conexion = Conexion.conectar();
            PreparedStatement ps = conexion.prepareStatement(sql)
        ) {
            ps.setInt(1, idUser);
            ps.setInt(2, pid);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            System.out.println("Error creando sesión:");
            System.out.println(e.getMessage());
            return false;
        }
    }

    // Antes cerraba TODAS las sesiones del usuario. Ahora cierra SOLO la sesión con ese pid.
    public boolean cerrarSesion(int pid) {
        String sql = """
            UPDATE seguridad.sesion
            SET activo = FALSE
            WHERE pid = ? AND activo = TRUE;
            """;
        try (
            Connection conexion = Conexion.conectar();
            PreparedStatement ps = conexion.prepareStatement(sql)
        ) {
            ps.setInt(1, pid);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            System.out.println("Error cerrando sesión:");
            System.out.println(e.getMessage());
            return false;
        }
    }
}