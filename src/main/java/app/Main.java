package app;

import backend.config.HibernateUtil;
import frontend.VentanaLogin;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        // PRUEBA TEMPORAL: borrar cuando Hibernate funcione
        try {
            HibernateUtil.getSessionFactory();
            System.out.println("Hibernate OK");
        } catch (Throwable e) {
            e.printStackTrace();
            return;
        }

        SwingUtilities.invokeLater(() -> new VentanaLogin().setVisible(true));
    }
}