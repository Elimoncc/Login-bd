package backend.dao;

import backend.config.Conexion;
import backend.modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UsuarioDAO {

   public Usuario buscarUsuario(String nombre, String password) {

    String sql = "SELECT * FROM buscar_usuario(?, ?)";

    Connection conexion = Conexion.conectar();
    if (conexion == null) {
        throw new IllegalStateException(
            "No se pudo conectar a la base de datos. Revisa la consola.");
    }

    try (conexion; PreparedStatement ps = conexion.prepareStatement(sql)) {

        ps.setString(1, nombre);
        ps.setString(2, password);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new Usuario(
                    rs.getInt("id_userN"),
                    rs.getString("nombre"),
                    rs.getString("password"),
                    rs.getBoolean("activo"));
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
        throw new IllegalStateException("Error consultando usuarios: " + e.getMessage());
    }

    return null; // aquí sí: usuario o contraseña incorrectos
}
}