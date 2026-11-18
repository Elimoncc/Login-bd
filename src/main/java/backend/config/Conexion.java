package backend.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.hibernate.cfg.Configuration;

public class Conexion {

    public static Connection conectar() {
        try {
            Configuration cfg = HibernateUtil.getConfiguration();
            Connection c = DriverManager.getConnection(
                cfg.getProperty("hibernate.connection.url"),
                cfg.getProperty("hibernate.connection.username"),
                cfg.getProperty("hibernate.connection.password"));
            try (Statement st = c.createStatement()) {
                st.execute("SET search_path TO " + cfg.getProperty("hibernate.default_schema"));
            }
            return c;
        } catch (Exception e) {
            System.out.println("Error de conexión: " + e.getMessage());
            return null;
        }
    }

    public static int obtenerPid(Connection c) {
        try (PreparedStatement ps = c.prepareStatement("SELECT pg_backend_pid()");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            System.out.println("Error obteniendo PID: " + e.getMessage());
        }
        return -1;
    }
        public static boolean fijarUsuario(Connection c, String nombreUsuario) {
        try (PreparedStatement ps = c.prepareStatement("SELECT set_config('app.user_name', ?, false)")) {
            //establecer una variable de configuración personalizada para la conexión.
            ps.setString(1, nombreUsuario);
            ps.execute();
            return true;
        } catch (Exception e) {
            System.out.println("Error fijando usuario: " + e.getMessage());
            return false;
        }
    }
}