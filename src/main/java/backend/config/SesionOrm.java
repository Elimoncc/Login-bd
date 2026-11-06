package backend.config;

import org.hibernate.Session;

import java.sql.Connection;

public class SesionOrm {

    /** Sesión Hibernate que trabaja sobre la conexión ya abierta del usuario. */
    public static Session abrir(Connection conexion) {
        return HibernateUtil.getSessionFactory()
                .withOptions()
                .connection(conexion)
                .openSession();
    }

    /** Mensaje de la causa original (el RAISE EXCEPTION de Postgres), sin los envoltorios de Hibernate. */
    public static String mensaje(Throwable e) {
        Throwable t = e;
        while (t.getCause() != null && t.getCause() != t) {
            t = t.getCause();
        }
        return t.getMessage();
    }
}
