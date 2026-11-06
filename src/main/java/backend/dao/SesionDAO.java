package backend.dao;

import backend.config.HibernateUtil;
import backend.entidad.Sesion;
import backend.entidad.UserN;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class SesionDAO {

    public boolean crearSesion(int idUser, int pid) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Sesion sesion = new Sesion();
            sesion.setUsuario(session.getReference(UserN.class, idUser)); // sin consulta extra
            sesion.setActivo(true);
            sesion.setPid(pid);

            session.persist(sesion);
            tx.commit();
            return true;

        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            System.out.println("Error creando sesión:");
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean cerrarSesion(int pid) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            session.createMutationQuery(
                    "update Sesion set activo = false where pid = :pid and activo = true")
                .setParameter("pid", pid)
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