package backend.dao;

import backend.config.SesionOrm;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.function.Function;

public class GestionDAO {

    public boolean crearTorneo(Connection c, int deporteId, String nombre,
                               LocalDate inicio, LocalDate fin) {
        return ejecutar(c, s -> s.createNativeQuery(
                    "SELECT crear_torneo(?1, ?2, CAST(?3 AS date), CAST(?4 AS date))", Boolean.class)
                .setParameter(1, deporteId)
                .setParameter(2, nombre)
                .setParameter(3, inicio, LocalDate.class)
                .setParameter(4, fin, LocalDate.class)   // puede ser null
                .getSingleResult());
    }

    public boolean registrarJugador(Connection c, String nombre, String email, String contacto) {
        return ejecutar(c, s -> s.createNativeQuery(
                    "SELECT registrar_jugador(?1, ?2, ?3)", Boolean.class)
                .setParameter(1, nombre)
                .setParameter(2, email, String.class)     // pueden ser null
                .setParameter(3, contacto, String.class)
                .getSingleResult());
    }

    public boolean registrarEquipo(Connection c, String nombre, int deporteId) {
        return ejecutar(c, s -> s.createNativeQuery(
                    "SELECT registrar_equipo(?1, ?2)", Boolean.class)
                .setParameter(1, nombre)
                .setParameter(2, deporteId)
                .getSingleResult());
    }

    public boolean crearPartido(Connection c, int deporteId, LocalDate inicio, String tipo,
                                String cantidadVs, int local, int visitante) {
        return ejecutar(c, s -> s.createNativeQuery(
                    "SELECT crear_partido(?1, CAST(?2 AS date), ?3, ?4, ?5, ?6)", Boolean.class)
                .setParameter(1, deporteId)
                .setParameter(2, inicio, LocalDate.class)
                .setParameter(3, tipo)
                .setParameter(4, cantidadVs)
                .setParameter(5, local)
                .setParameter(6, visitante)
                .getSingleResult());
    }

    /** Ejecuta la función de la BD dentro de una transacción y devuelve su resultado booleano. */
    private boolean ejecutar(Connection c, Function<Session, Boolean> accion) {
        try (Session s = SesionOrm.abrir(c)) {
            Transaction tx = s.beginTransaction();
            try {
                Boolean resultado = accion.apply(s);
                tx.commit();
                return Boolean.TRUE.equals(resultado);
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        } catch (Exception e) {
            throw new IllegalStateException(SesionOrm.mensaje(e));
        }
    }
}
