package backend.dao;
import backend.config.Conexion;
import backend.modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UsuarioDAO {

    public Usuario buscarUsuario(String nombre, String password) {

        String sql = """
            SELECT id_usern, nombre, password, activo
            FROM usern
            WHERE nombre = ? AND password = ?
            """;

        try (
            Connection conexion = Conexion.conectar();
            PreparedStatement ps = conexion.prepareStatement(sql)
        ) {

            ps.setString(1, nombre);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return new Usuario(
                    rs.getInt("id_usern"),
                    rs.getString("nombre"),
                    rs.getString("password"),
                    rs.getBoolean("activo")
                );
            }

        } catch (Exception e) {

            System.out.println("Error buscando usuario:");
            System.out.println(e.getMessage());
        }

        return null;
    }
}