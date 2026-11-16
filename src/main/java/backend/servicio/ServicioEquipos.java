package backend.servicio;

import java.util.function.Consumer;

import backend.dao.GestionDAO;

public class ServicioEquipos {

    private final GestionDAO gestionDAO = new GestionDAO();

    public boolean registrar(String nombre, int deporteId) {
        if (nombre == null || nombre.isBlank()) {
            return false;
        }
        return gestionDAO.registrarEquipo(nombre.trim(), deporteId);
    }

    /** Versión con callbacks: avisa por alExito o alError en vez de devolver boolean. */
    public void registrar(String nombre, int deporteId,
                          Runnable alExito, Consumer<String> alError) {
        if (nombre == null || nombre.isBlank()) {
            alError.accept("El nombre del equipo es obligatorio.");
            return;
        }
        try {
            gestionDAO.registrarEquipo(nombre.trim(), deporteId);
        } catch (Exception e) {
            alError.accept(e.getMessage());
            return;
        }
        alExito.run();
    }
}