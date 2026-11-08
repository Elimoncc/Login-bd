package backend.config;

public class Errores {

    /** Mensaje de la causa original, sin los envoltorios de Hibernate. */
    public static String raiz(Throwable e) {
        Throwable t = e;
        while (t.getCause() != null && t.getCause() != t) {
            t = t.getCause();
        }
        return t.getMessage();
    }
}
