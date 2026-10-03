package backend.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class GestionDAO {

    public boolean crearTorneo(Connection c, int deporteId, String nombre,
                               LocalDate inicio, LocalDate fin) {
        return llamar(c, "SELECT crear_torneo(?, ?, ?::date, ?::date)",
                      deporteId, nombre, inicio, fin);
    }

    public boolean registrarJugador(Connection c, String nombre, String email, String contacto) {
        return llamar(c, "SELECT registrar_jugador(?, ?, ?)", nombre, email, contacto);
    }

    public boolean crearPartido(Connection c, int deporteId, LocalDate inicio, String tipo,
                                String cantidadVs, int local, int visitante) {
        return llamar(c, "SELECT crear_partido(?, ?::date, ?, ?, ?, ?)",
                      deporteId, inicio, tipo, cantidadVs, local, visitante);
    }

    /** Ejecuta una función booleana. Si la BD lanza un error, sube el mensaje a la interfaz. */
    private boolean llamar(Connection c, String sql, Object... params) {
        try (PreparedStatement ps = c.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }

        } catch (SQLException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }
}