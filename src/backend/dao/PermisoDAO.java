package backend.dao;

import backend.config.Conexion;
import backend.modelo.FuncionUsuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PermisoDAO {

    public List<FuncionUsuario> listarFunciones(int idUser) {
        String sql = """
            SELECT r.nombre AS rol,
                   f.id_funcion,
                   f.nombre AS funcion,
                   COALESCE(string_agg(i.nombre, ', '), '') AS interfaz
            FROM userN_rol ur
            JOIN rol r          ON r.id_rol = ur.id_rol
            JOIN rol_funcion rf ON rf.id_rol = r.id_rol
            JOIN funcion f      ON f.id_funcion = rf.id_funcion
            LEFT JOIN funcion_iu fi ON fi.id_funcion = f.id_funcion
            LEFT JOIN iu i          ON i.id_iu = fi.id_iu
            WHERE ur.id_userN = ?
              AND ur.activo
              AND ur.desde <= CURRENT_DATE
              AND (ur.hasta IS NULL OR ur.hasta >= CURRENT_DATE)
            GROUP BY r.nombre, f.id_funcion, f.nombre
            ORDER BY r.nombre, f.id_funcion
            """;

        List<FuncionUsuario> funciones = new ArrayList<>();
        try (Connection conexion = Conexion.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idUser);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    funciones.add(new FuncionUsuario(
                        rs.getString("rol"),
                        rs.getInt("id_funcion"),
                        rs.getString("funcion"),
                        rs.getString("interfaz")
                    ));
                }
            }
        } catch (Exception e) {
            System.out.println("Error listando funciones: " + e.getMessage());
        }
        return funciones;
    }
}