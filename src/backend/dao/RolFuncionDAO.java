package backend.dao;

import backend.config.Conexion;
import backend.modelo.RolFuncion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RolFuncionDAO {

    public List<RolFuncion> listarTodas() {
        String sql = """
            SELECT r.nombre AS rol, f.nombre AS funcion, f.descripcion
            FROM rol r
            JOIN rol_funcion rf ON rf.id_rol = r.id_rol
            JOIN funcion f      ON f.id_funcion = rf.id_funcion
            ORDER BY r.id_rol, f.id_funcion
            """;

        List<RolFuncion> lista = new ArrayList<>();
        try (Connection conexion = Conexion.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new RolFuncion(
                    rs.getString("rol"),
                    rs.getString("funcion"),
                    rs.getString("descripcion")
                ));
            }
        } catch (Exception e) {
            System.out.println("Error listando roles y funciones: " + e.getMessage());
        }
        return lista;
    }
}