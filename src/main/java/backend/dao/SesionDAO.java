package backend.dao;

import java.sql.Connection;

import org.hibernate.Session;
import org.hibernate.Transaction;

import backend.config.HibernateUtil;
import backend.entidad.Sesion;
import backend.entidad.UserN;

public class SesionDAO {

    /**
     * Devuelve el id_sesion creado, o null si no se pudo crear.
     */
    public Integer crearSesion(int idUser, Connection conexion, int pid) {
        Transaction tx = null;
        try (Session session = HibernateUtil.abrirSesion(conexion)) {
            tx = session.beginTransaction();

            // Libera sesiones huérfanas (app cerrada a la fuerza) cuyo PID ya no existe
            session.createNativeMutationQuery(
                    "UPDATE sesion SET activo = false WHERE activo "
                    + "AND (pid IS NULL OR pid NOT IN (SELECT pid FROM pg_stat_activity))")
                    .executeUpdate();

            Sesion s = new Sesion();
            s.setUsuario(session.getReference(UserN.class, idUser));
            s.setActivo(true);
            s.setPid(pid);
            session.persist(s);

            tx.commit();
            return s.getId();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            System.out.println("Error creando sesión: " + e.getMessage());
            return null;
        }
    }

    public boolean cerrarSesion(int idSesion) {
        Transaction tx = null;
        try (Session session = HibernateUtil.abrirSesion()) {
            tx = session.beginTransaction();

            session.createMutationQuery(
                    "update Sesion set activo = false where id = :id and activo = true")
                    .setParameter("id", idSesion)
                    .executeUpdate();

            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            System.out.println("Error cerrando sesión:");
            System.out.println(e.getMessage());
            return false;
        }
    }
}
