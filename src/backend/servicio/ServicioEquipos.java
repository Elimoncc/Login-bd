package backend.servicio;

import backend.dao.EquipoDAO;
import java.sql.Connection;
import java.util.List;

public class ServicioEquipos {

    private final EquipoDAO equipoDAO = new EquipoDAO();

    public boolean registrar(
            Connection conexion,
            String nombre,
            int deporteId) {

        if (nombre == null || nombre.isBlank()) {
            return false;
        }

        return equipoDAO.registrar(
            conexion,
            nombre,
            deporteId
        );
    }

    public boolean modificar(
            Connection conexion,
            int equipoId,
            String nombre,
            int deporteId) {

        if (nombre == null || nombre.isBlank()) {
            return false;
        }

        return equipoDAO.modificar(
            conexion,
            equipoId,
            nombre,
            deporteId
        );
    }

    public boolean eliminar(
            Connection conexion,
            int equipoId) {

        return equipoDAO.eliminar(
            conexion,
            equipoId
        );
    }

    public List<String> listar(Connection conexion) {
        return equipoDAO.listar(conexion);
    }
}