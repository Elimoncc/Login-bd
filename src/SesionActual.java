import java.sql.Connection;

public class SesionActual {

    private static Connection conexion;
    private static int idUser;
    private static int pid;

    public static void iniciar(
        Connection nuevaConexion,
        int nuevoIdUser,
        int nuevoPid
    ) {

        conexion = nuevaConexion;
        idUser = nuevoIdUser;
        pid = nuevoPid;
    }

    public static Connection getConexion() {
        return conexion;
    }

    public static int getIdUser() {
        return idUser;
    }

    public static int getPid() {
        return pid;
    }

    public static boolean estaActiva() {
        return conexion != null;
    }

    public static void cerrar() {

        try {

            if (conexion != null) {
                conexion.close();
            }

        } catch (Exception e) {

            System.out.println("Error cerrando conexión:");
            System.out.println(e.getMessage());

        } finally {

            conexion = null;
            idUser = 0;
            pid = 0;
        }
    }
}