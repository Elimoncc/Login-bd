package backend.dao;

import backend.config.Errores;
import backend.config.HibernateUtil;
import backend.entidad.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.List;

public class GestionDAO {

    public boolean crearTorneo(int deporteId, String nombre,
                               LocalDate inicio, LocalDate fin) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        try {
            Torneo t = new Torneo(session.getReference(Deporte.class, deporteId), inicio, nombre);
            t.setFechaFin(fin);
            session.persist(t);
            tx.commit();
            return true;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw new IllegalStateException(Errores.raiz(e));
        } finally {
            session.close();
        }
    }

    public boolean registrarJugador(String nombre, String email, String contacto) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        try {
            Jugador j = new Jugador();
            j.setNombre(nombre);
            j.setEmail(email);
            j.setContacto(contacto);
            j.setPartidosJugados(0);
            session.persist(j);       // inserta persona y jugador
            tx.commit();
            return true;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw new IllegalStateException(Errores.raiz(e));
        } finally {
            session.close();
        }
    }

    public boolean registrarEquipo(String nombre, int deporteId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        try {
            Equipo e = new Equipo();
            e.setNombre(nombre);
            e.setDeporte(session.getReference(Deporte.class, deporteId));
            session.persist(e);
            tx.commit();
            return true;
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            throw new IllegalStateException(Errores.raiz(ex));
        } finally {
            session.close();
        }
    }

    public boolean crearPartido(int deporteId, LocalDate inicio, String tipo,
                                String cantidadVs, int local, int visitante) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        try {
            if (local == visitante) {
                throw new IllegalStateException("El local y el visitante no pueden ser iguales");
            }

            if ("EQUIPO".equals(tipo)) {
                Long n = session.createSelectionQuery(
                            "select count(e) from Equipo e " +
                            "where e.id in (:ids) and e.deporte.id = :dep", Long.class)
                        .setParameterList("ids", List.of(local, visitante))
                        .setParameter("dep", deporteId)
                        .uniqueResult();
                if (n == null || n != 2L) {
                    throw new IllegalStateException("Ambos equipos deben ser del deporte del torneo");
                }
            }

            Partido p = new Partido();
            p.setTorneo(session.getReference(Torneo.class, new TorneoId(deporteId, inicio)));
            p.setTipoPartido(tipo);
            p.setCantidadVs(cantidadVs);
            session.persist(p);

            if ("1VS1".equals(tipo)) {
                Partido1vs1 x = new Partido1vs1();
                x.setPartido(p);
                x.setJugadorLocal(session.getReference(Jugador.class, local));
                x.setJugadorVisitante(session.getReference(Jugador.class, visitante));
                session.persist(x);
            } else {
                PartidoEquipoVsEquipo x = new PartidoEquipoVsEquipo();
                x.setPartido(p);
                x.setEquipoLocal(session.getReference(Equipo.class, local));
                x.setEquipoVisitante(session.getReference(Equipo.class, visitante));
                session.persist(x);
            }

            tx.commit();
            return true;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw new IllegalStateException(Errores.raiz(e));
        } finally {
            session.close();
        }
    }
}
