package backend.dao;

import backend.config.Conexion;
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
}