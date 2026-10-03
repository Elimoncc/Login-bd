package backend.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EquipoDAO {

    public boolean registrar(
            Connection conexion,
            String nombre,
            int deporteId) {

        String sql = "SELECT * FROM registrar_equipo(?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setInt(2, deporteId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getBoolean(1);
                }
            }

        } catch (Exception e) {

            System.out.println("Error registrando equipo:");
            System.out.println(e.getMessage());

        }

        return false;
    }

    public boolean modificar(
            Connection conexion,
            int equipoId,
            String nombre,
            int deporteId) {

        String sql = "SELECT * FROM modificar_equipo(?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, equipoId);
            ps.setString(2, nombre);
            ps.setInt(3, deporteId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getBoolean(1);
                }
            }

        } catch (Exception e) {

            System.out.println("Error modificando equipo:");
            System.out.println(e.getMessage());

        }

        return false;
    }

    public boolean eliminar(
            Connection conexion,
            int equipoId) {

        String sql = "SELECT * FROM eliminar_equipo(?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, equipoId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getBoolean(1);
                }
            }

        } catch (Exception e) {

            System.out.println("Error eliminando equipo:");
            System.out.println(e.getMessage());

        }

        return false;
    }

    public List<String> listar(Connection conexion) {

        String sql = "SELECT * FROM listar_equipos()";

        List<String> equipos = new ArrayList<>();

        try (
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                String equipo =
                    rs.getInt("equipo_id") + " - " +
                    rs.getString("nombre") + " - deporte " +
                    rs.getInt("deporte_id");

                equipos.add(equipo);
            }

        } catch (Exception e) {

            System.out.println("Error listando equipos:");
            System.out.println(e.getMessage());
        }

        return equipos;
    }
}