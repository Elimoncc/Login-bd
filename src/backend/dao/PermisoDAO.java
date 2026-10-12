package backend.dao;

import backend.config.Conexion;
import backend.modelo.FuncionUsuario;
import backend.modelo.IU;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PermisoDAO {

    public List<Integer> listarIU(int idUser) {
//[1,2,3]
        String sql = "SELECT id_iu FROM credenciales_userN(?)";

        List<Integer> lista = new ArrayList<>();

        try (
            Connection conexion = Conexion.conectar();
            PreparedStatement ps = conexion.prepareStatement(sql)
        ) {

            ps.setInt(1, idUser);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    lista.add(rs.getInt("id_iu"));

                }

            }

        } catch (Exception e) {

            System.out.println("Error obteniendo id_iu:");
            System.out.println(e.getMessage());

        }

        return lista;
    }
// cuando ya iniciasesion
    public List<IU> listarInterfaces(int idUser) {

        String sql = "SELECT * FROM interfaces_usuario(?)";

        List<IU> lista = new ArrayList<>();

        try (
            Connection conexion = Conexion.conectar();
            PreparedStatement ps = conexion.prepareStatement(sql)
        ) {

            ps.setInt(1, idUser);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    lista.add(new IU(
                        rs.getInt("id_iu"),
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                    ));

                }
            }

        } catch (Exception e) {
            System.out.println("Error obteniendo interfaces (IU):");
            System.out.println(e.getMessage());
        }

        return lista;
    }
}