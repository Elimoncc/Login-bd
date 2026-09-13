import java.sql.Connection;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);

        System.out.println("================================");
        System.out.println("       SISTEMA DE LOGIN");
        System.out.println("================================");

        System.out.print("Usuario: ");
        String nombre = leer(teclado);
        System.out.print("Contraseña: ");
        String password = leer(teclado);

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuario = usuarioDAO.buscarUsuario(nombre, password);

        if (!usuarioEstado(usuario)) {
            teclado.close();
            return;
        }

        Connection conexion = crearConexion();
        if (conexion == null) {
            teclado.close();
            return;
        }

        int pid = obtenerPid(conexion);
        if (pid == -1) {
            cerrarConexionSilenciosa(conexion);
            teclado.close();
            return;
        }

        System.out.println("PID PostgreSQL: " + pid);

        SesionDAO sesionDAO = new SesionDAO();
        boolean sesionCreada = sesionDAO.crearSesion(usuario.getIdUser(), pid);

        if (!sesionCreada) {
            System.out.println("No se pudo crear la sesión.");
            cerrarConexionSilenciosa(conexion);
            teclado.close();
            return;
        }

        SesionActual.iniciar(conexion, usuario.getIdUser(), pid);

        mostrarSesionIniciada(usuario, pid);
        esperarCierre(teclado);

        sesionDAO.cerrarSesion(usuario.getIdUser());
        SesionActual.cerrar();

        mostrarSesionCerrada();

        teclado.close();
    }

    private static String leer(Scanner teclado) {
        return teclado.nextLine();
    }

    private static boolean usuarioEstado(Usuario usuario) {
        System.out.println();

        if (usuario == null) {
            System.out.println("Usuario o contraseña incorrectos.");
            return false;
        }

        if (!usuario.isActivo()) {
            System.out.println("El usuario está inactivo.");
            return false;
        }

        System.out.println("Usuario autenticado correctamente.");
        System.out.println("Bienvenido: " + usuario.getNombre());
        return true;
    }

    private static Connection crearConexion() {
        Connection conexion = Conexion.conectar();
        if (conexion == null) {
            System.out.println("No se pudo crear la sesión.");
        }
        return conexion;
    }

    private static int obtenerPid(Connection conexion) {
        int pid = Conexion.obtenerPid(conexion);
        if (pid == -1) {
            System.out.println("No se pudo obtener el PID.");
        }
        return pid;
    }

    private static void cerrarConexionSilenciosa(Connection conexion) {
        try {
            conexion.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void mostrarSesionIniciada(Usuario usuario, int pid) {
        System.out.println();
        System.out.println("================================");
        System.out.println("      SESIÓN INICIADA");
        System.out.println("================================");
        System.out.println("Usuario: " + usuario.getNombre());
        System.out.println("ID usuario: " + usuario.getIdUser());
        System.out.println("PID PostgreSQL: " + pid);
    }

    private static void esperarCierre(Scanner teclado) {
        System.out.println();
        System.out.println("Presiona ENTER para cerrar sesión.");
        teclado.nextLine();
    }

    private static void mostrarSesionCerrada() {
        System.out.println();
        System.out.println("Sesión cerrada correctamente.");
        System.out.println("PID eliminado de la sesión.");
    }
}