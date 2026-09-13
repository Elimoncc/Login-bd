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

    public boolean cerrarSesion(int idUser) {
        String sql = """
            UPDATE seguridad.sesion
            SET activo = FALSE
            WHERE id_userN = ?
            """;
        try (
            Connection conexion = Conexion.conectar();
            PreparedStatement ps = conexion.prepareStatement(sql)
        ) {

            ps.setInt(1, idUser);

            ps.executeUpdate();

            return true;

        } catch (Exception e) {

            System.out.println("Error cerrando sesión:");
            System.out.println(e.getMessage());

            return false;
        }
    }
}