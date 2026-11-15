package backend.config;

import java.sql.Connection;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final Configuration cfg = new Configuration().configure();
    private static final SessionFactory sf = cfg.buildSessionFactory();
    private static volatile Connection conexionActual;

    public static SessionFactory getSessionFactory() { return sf; }
    public static Configuration getConfiguration() { return cfg; }

    public static void setConexionActual(Connection c) { conexionActual = c; }

    public static Session abrirSesion() {
        Connection c = conexionActual;
        return (c == null) ? sf.openSession() : abrirSesion(c);
    }

    public static Session abrirSesion(Connection c) {
        return sf.withOptions().connection(c).openSession();
    }
}