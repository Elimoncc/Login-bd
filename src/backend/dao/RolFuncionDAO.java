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

        String sql = "SELECT * FROM listar_roles_funciones()";

        List<RolFuncion> lista = new ArrayList<>();

        try (
            Connection conexion = Conexion.conectar();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {
                lista.add(new RolFuncion(
                    rs.getString("rol_nombre"),
                    rs.getString("funcion_nombre"),
                    rs.getString("funcion_descripcion")
                ));
            }

        } catch (Exception e) {
            System.out.println("Error listando roles y funciones:");
            System.out.println(e.getMessage());
        }

        return lista;
    }
}