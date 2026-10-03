package backend.servicio;

import backend.dao.ConsultaDAO;
import backend.dao.GestionDAO;
import backend.modelo.Tabla;
import java.sql.Connection;
import java.time.LocalDate;

public class ServicioGestion {

    private final GestionDAO gestionDAO = new GestionDAO();
    private final ConsultaDAO consultaDAO = new ConsultaDAO();

    // ---------- Consultas ----------

    public Tabla deportes(Connection c) {
        return consultaDAO.consultar(c, "SELECT deporte_id, nombre FROM listar_deportes()");
    }

    public Tabla torneos(Connection c) {
        return consultaDAO.consultar(c,
            "SELECT deporte_id, deporte, nombre, fecha_inicio, fecha_fin FROM listar_torneos()");
    }

    public Tabla jugadores(Connection c) {
        return consultaDAO.consultar(c, "SELECT persona_id, nombre FROM listar_jugadores()");
    }

    public Tabla equipos(Connection c) {
        return consultaDAO.consultar(c,
            "SELECT equipo_id, equipo, deporte FROM listar_equipos_detalle()");
    }

    public Tabla equiposDeDeporte(Connection c, int deporteId) {
        return consultaDAO.consultar(c,
            "SELECT equipo_id, nombre FROM listar_equipos() WHERE deporte_id = ?", deporteId);
    }

    private static final String COLUMNAS_PARTIDO =
        "partido_id, torneo, tipo, cantidad_vs, local_nombre, visitante_nombre, fecha, hora, arbitro";

    public Tabla partidosArbitro(Connection c, int idUser) {
        return consultaDAO.consultar(c,
            "SELECT " + COLUMNAS_PARTIDO + " FROM partidos_arbitro(?)", idUser);
    }

    public Tabla partidosJugador(Connection c, int idUser) {
        return consultaDAO.consultar(c,
            "SELECT " + COLUMNAS_PARTIDO + " FROM partidos_jugador(?)", idUser);
    }

    // ---------- Operaciones (con validación) ----------

    public void crearTorneo(Connection c, int deporteId, String nombre,
                            LocalDate inicio, LocalDate fin) {
        if (nombre == null || nombre.isBlank())
            throw new IllegalStateException("El nombre del torneo es obligatorio.");
        if (inicio == null)
            throw new IllegalStateException("La fecha de inicio es obligatoria.");
        if (fin != null && fin.isBefore(inicio))
            throw new IllegalStateException("La fecha de fin no puede ser anterior al inicio.");

        if (!gestionDAO.crearTorneo(c, deporteId, nombre.trim(), inicio, fin))
            throw new IllegalStateException("No se pudo crear el torneo.");
    }

    public void registrarJugador(Connection c, String nombre, String email, String contacto) {
        if (nombre == null || nombre.isBlank())
            throw new IllegalStateException("El nombre del jugador es obligatorio.");

        if (!gestionDAO.registrarJugador(c, nombre.trim(), vacioANull(email), vacioANull(contacto)))
            throw new IllegalStateException("No se pudo registrar el jugador.");
    }

    public void crearPartido(Connection c, int deporteId, LocalDate inicio, String tipo,
                             String cantidadVs, int local, int visitante) {
        if (cantidadVs == null || cantidadVs.isBlank())
            throw new IllegalStateException("Indica la modalidad (ej. 5VS5).");
        if (local == visitante)
            throw new IllegalStateException("El local y el visitante no pueden ser iguales.");

        if (!gestionDAO.crearPartido(c, deporteId, inicio, tipo, cantidadVs.trim(), local, visitante))
            throw new IllegalStateException("No se pudo crear el partido.");
    }

    private String vacioANull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}