package backend.dao;

import backend.config.Conexion;
import backend.modelo.FuncionUsuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PermisoDAO {

    public List<Integer> listarIU(int idUser) {

        String sql = "SELECT * FROM credenciales_userN(?)";

        List<Integer> lista = new ArrayList<>();

        try (
            Connection conexion = Conexion.conectar();
            PreparedStatement ps = conexion.prepareStatement(sql)
        ) {

            ps.setInt(1, idUser);

            boolean tieneResultado = ps.execute();

            if (tieneResultado) {

                try (ResultSet rs = ps.getResultSet()) {

                    while (rs.next()) {
                        lista.add(rs.getInt("id_iu"));
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error obteniendo IU:");
            System.out.println(e.getMessage());
        }

        return lista;
    }

    public List<FuncionUsuario> listarFunciones(int idUser) {

        String sql = "SELECT * FROM funciones_usuario(?)";
        List<FuncionUsuario> lista = new ArrayList<>();

        try (Connection conexion = Conexion.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idUser);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String categoria = rs.getString("categoria");
                    lista.add(new FuncionUsuario(
                        rs.getString("rol"),
                        rs.getInt("id_funcion"),
                        rs.getString("funcion"),
                        categoria == null ? "General" : categoria,
                        rs.getString("descripcion")));
                }
            }

        } catch (Exception e) {
            System.out.println("Error obteniendo funciones del usuario:");
            System.out.println(e.getMessage());
        }

        return lista;
    }
}