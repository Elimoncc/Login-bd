package backend.servicio;

import backend.dao.GestionDAO;

import java.sql.Connection;

public class ServicioEquipos {

    private final GestionDAO gestionDAO = new GestionDAO();

    public boolean registrar(
            Connection conexion,
            String nombre,
            int deporteId) {

        if (nombre == null || nombre.isBlank()) {
            return false;
        }

        return gestionDAO.registrarEquipo(
            conexion,
            nombre,
            deporteId
        );
    }
}