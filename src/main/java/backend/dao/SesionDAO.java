package backend.dao;

import backend.config.HibernateUtil;
import backend.entidad.Sesion;
import backend.entidad.UserN;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class SesionDAO {

    /** Devuelve el id_sesion creado, o null si no se pudo crear. */
    public Integer crearSesion(int idUser) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Sesion sesion = new Sesion();
            sesion.setUsuario(session.getReference(UserN.class, idUser));
            sesion.setActivo(true);
            session.persist(sesion);

            tx.commit();
            return sesion.getId();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            System.out.println("Error creando sesión:");
            System.out.println(e.getMessage());
            return null;
        }
    }

    public boolean cerrarSesion(int idSesion) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            session.createMutationQuery(
                    "update Sesion set activo = false where id = :id and activo = true")
                .setParameter("id", idSesion)
                .executeUpdate();

            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            System.out.println("Error cerrando sesión:");
            System.out.println(e.getMessage());
            return false;
        }
    }
}
