import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            System.out.println();
            System.out.println("================================");
            System.out.println("       SISTEMA DE LOGIN");
            System.out.println("================================");
            System.out.println("1. Iniciar sesión");
            System.out.println("2. Ver sesiones activas");
            System.out.println("3. Cerrar una sesión");
            System.out.println("4. Salir");
            System.out.print("Opción: ");

            String opcion = teclado.nextLine();

            switch (opcion) {
                case "1" -> iniciarSesion(teclado);
                case "2" -> listarSesiones();
                case "3" -> cerrarSesionElegida(teclado);
                case "4" -> {
                    cerrarTodasLasSesiones();
                    salir = true;
                }
                default -> System.out.println("Opción inválida.");
            }
        }

        teclado.close();
    }

    private static void iniciarSesion(Scanner teclado) {
        System.out.print("Usuario: ");
        String nombre = teclado.nextLine();
        System.out.print("Contraseña: ");
        String password = teclado.nextLine();

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuario = usuarioDAO.buscarUsuario(nombre, password);

        if (!usuarioEstado(usuario)) {
            return;
        }

        Connection conexion = Conexion.conectar();
        if (conexion == null) {
            System.out.println("No se pudo crear la sesión.");
            return;
        }

        int pid = Conexion.obtenerPid(conexion);
        if (pid == -1) {
            System.out.println("No se pudo obtener el PID.");
            cerrarConexionSilenciosa(conexion);
            return;
        }

        SesionDAO sesionDAO = new SesionDAO();
        boolean sesionCreada = sesionDAO.crearSesion(usuario.getIdUser(), pid);

        if (!sesionCreada) {
            System.out.println("No se pudo crear la sesión.");
            cerrarConexionSilenciosa(conexion);
            return;
        }

        GestorSesiones.agregar(
            new GestorSesiones.SesionInfo(conexion, usuario.getIdUser(), usuario.getNombre(), pid)
        );

        System.out.println();
        System.out.println("Sesión iniciada. Usuario: " + usuario.getNombre() + " | PID: " + pid);
    }

    private static void listarSesiones() {
        List<GestorSesiones.SesionInfo> activas = GestorSesiones.listar();

        if (activas.isEmpty()) {
            System.out.println("No hay sesiones activas.");
            return;
        }

        System.out.println("Sesiones activas:");
        for (GestorSesiones.SesionInfo s : activas) {
            System.out.println(" - Usuario: " + s.nombreUsuario + " | PID: " + s.pid);
        }
    }

    private static void cerrarSesionElegida(Scanner teclado) {
        listarSesiones();
        if (GestorSesiones.listar().isEmpty()) return;

        System.out.print("Ingresa el PID de la sesión a cerrar: ");
        int pid;
        try {
            pid = Integer.parseInt(teclado.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("PID inválido.");
            return;
        }

        GestorSesiones.SesionInfo sesion = GestorSesiones.buscarPorPid(pid);
        if (sesion == null) {
            System.out.println("No existe una sesión activa con ese PID.");
            return;
        }

        SesionDAO sesionDAO = new SesionDAO();
        sesionDAO.cerrarSesion(pid);
        GestorSesiones.cerrar(sesion);

        System.out.println("Sesión cerrada correctamente.");
    }

    private static void cerrarTodasLasSesiones() {
        SesionDAO sesionDAO = new SesionDAO();
        for (GestorSesiones.SesionInfo s : List.copyOf(GestorSesiones.listar())) {
            sesionDAO.cerrarSesion(s.pid);
            GestorSesiones.cerrar(s);
        }
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
        return true;
    }

    private static void cerrarConexionSilenciosa(Connection conexion) {
        try {
            conexion.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}