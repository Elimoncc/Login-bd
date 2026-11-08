package backend.servicio;

import backend.dao.ConsultaDAO;
import backend.dao.GestionDAO;
import backend.modelo.Tabla;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ServicioGestion {

    private final GestionDAO gestionDAO = new GestionDAO();
    private final ConsultaDAO consultaDAO = new ConsultaDAO();

    public Tabla deportes() {
        return consultaDAO.consultar(List.of("deporte_id", "nombre"),
            "select d.id, d.nombre from Deporte d order by d.id");
    }

    public Tabla torneos() {
        return consultaDAO.consultar(
            List.of("deporte_id", "deporte", "nombre", "fecha_inicio", "fecha_fin"),
            "select t.id.deporteId, d.nombre, t.nombre, t.id.fechaInicio, t.fechaFin " +
            "from Torneo t join t.deporte d order by t.id.fechaInicio desc");
    }

    public Tabla jugadores() {
        return consultaDAO.consultar(List.of("persona_id", "nombre"),
            "select j.id, j.nombre from Jugador j order by j.id");
    }

    public Tabla equipos() {
        return consultaDAO.consultar(List.of("equipo_id", "equipo", "deporte"),
            "select e.id, e.nombre, d.nombre from Equipo e join e.deporte d order by e.id");
    }

    public Tabla equiposDeDeporte(int deporteId) {
        return consultaDAO.consultar(List.of("equipo_id", "nombre"),
            "select e.id, e.nombre from Equipo e where e.deporte.id = ?1 order by e.id",
            deporteId);
    }

    // ===== PARTIDOS DEL ÁRBITRO (EQUIPO + 1VS1 combinados) =====
    private static final List<String> COLUMNAS_PARTIDOS_ARBITRO = List.of(
        "partido_id", "torneo_nombre", "tipo_partido", "cantidad_vs",
        "local_nombre", "visitante_nombre", "fecha", "hora", "arbitro_nombre");

    private static final String PARTIDOS_ARBITRO_EQUIPO =
        "select p.id, t.nombre, p.tipoPartido, p.cantidadVs, el.nombre, ev.nombre, " +
        "       pr.fecha, pr.hora, pr.arbitro.nombre " +
        "from Programacion pr join pr.partido p join p.torneo t, " +
        "     PartidoEquipoVsEquipo pe join pe.equipoLocal el join pe.equipoVisitante ev, " +
        "     UserN u " +
        "where pe.partido = p and u.id = ?1 and pr.arbitro.id = u.persona.id";

    private static final String PARTIDOS_ARBITRO_1VS1 =
        "select p.id, t.nombre, p.tipoPartido, p.cantidadVs, jl.nombre, jv.nombre, " +
        "       pr.fecha, pr.hora, pr.arbitro.nombre " +
        "from Programacion pr join pr.partido p join p.torneo t, " +
        "     Partido1vs1 x join x.jugadorLocal jl join x.jugadorVisitante jv, " +
        "     UserN u " +
        "where x.partido = p and u.id = ?1 and pr.arbitro.id = u.persona.id";

    public Tabla partidosArbitro(int idUser) {
        List<Object[]> filas = new ArrayList<>();
        filas.addAll(consultaDAO.consultar(COLUMNAS_PARTIDOS_ARBITRO, PARTIDOS_ARBITRO_EQUIPO, idUser).filas());
        filas.addAll(consultaDAO.consultar(COLUMNAS_PARTIDOS_ARBITRO, PARTIDOS_ARBITRO_1VS1, idUser).filas());
        filas.sort(Comparator
            .comparing((Object[] f) -> (LocalDate) f[6])
            .thenComparing(f -> (LocalTime) f[7]));
        return new Tabla(COLUMNAS_PARTIDOS_ARBITRO, filas);
    }

    public Tabla partidosJugador(int idUser) {
        return consultaDAO.consultar(
            List.of("partido_id", "torneo_nombre", "tipo_partido", "cantidad_vs",
                    "jugador_local", "jugador_visitante", "fecha", "hora", "arbitro_nombre"),
            "select p.id, t.nombre, p.tipoPartido, p.cantidadVs, jl.nombre, jv.nombre, " +
            "       pr.fecha, pr.hora, pr.arbitro.nombre " +
            "from Programacion pr join pr.partido p join p.torneo t, " +
            "     Partido1vs1 x join x.jugadorLocal jl join x.jugadorVisitante jv, " +
            "     UserN u " +
            "where x.partido = p and p.tipoPartido = '1VS1' and u.id = ?1 " +
            "  and (jl.id = u.persona.id or jv.id = u.persona.id) " +
            "order by pr.fecha, pr.hora", idUser);
    }

    // ---------- Operaciones (con validación) ----------

    public void crearTorneo(int deporteId, String nombre,
                            LocalDate inicio, LocalDate fin) {
        if (nombre == null || nombre.isBlank())
            throw new IllegalStateException("El nombre del torneo es obligatorio.");
        if (inicio == null)
            throw new IllegalStateException("La fecha de inicio es obligatoria.");
        if (fin != null && fin.isBefore(inicio))
            throw new IllegalStateException("La fecha de fin no puede ser anterior al inicio.");

        if (!gestionDAO.crearTorneo(deporteId, nombre.trim(), inicio, fin))
            throw new IllegalStateException("No se pudo crear el torneo.");
    }

    public void registrarJugador(String nombre, String email, String contacto) {
        if (nombre == null || nombre.isBlank())
            throw new IllegalStateException("El nombre del jugador es obligatorio.");

        if (!gestionDAO.registrarJugador(nombre.trim(), vacioANull(email), vacioANull(contacto)))
            throw new IllegalStateException("No se pudo registrar el jugador.");
    }

    public void crearPartido(int deporteId, LocalDate inicio, String tipo,
                             String cantidadVs, int local, int visitante) {
        if (cantidadVs == null || cantidadVs.isBlank())
            throw new IllegalStateException("Indica la modalidad (ej. 5VS5).");
        if (local == visitante)
            throw new IllegalStateException("El local y el visitante no pueden ser iguales.");

        if (!gestionDAO.crearPartido(deporteId, inicio, tipo, cantidadVs.trim(), local, visitante))
            throw new IllegalStateException("No se pudo crear el partido.");
    }

    private String vacioANull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
