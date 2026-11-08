package backend.servicio;

import backend.dao.GestionDAO;

public class ServicioEquipos {

    private final GestionDAO gestionDAO = new GestionDAO();

    public boolean registrar(String nombre, int deporteId) {
        if (nombre == null || nombre.isBlank()) {
            return false;
        }
        return gestionDAO.registrarEquipo(nombre.trim(), deporteId);
    }
}
