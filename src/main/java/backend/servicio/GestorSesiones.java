package backend.servicio;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GestorSesiones {

    public static class SesionInfo {
        public final Connection conexion;
        public final int idUser;
        public final String nombreUsuario;
        public final int pid;

        public SesionInfo(Connection conexion, int idUser, String nombreUsuario, int pid) {
            this.conexion = conexion;
            this.idUser = idUser;
            this.nombreUsuario = nombreUsuario;
            this.pid = pid;
        }
    }

    private static final List<SesionInfo> sesionesActivas =
        Collections.synchronizedList(new ArrayList<>());

    public static void agregar(SesionInfo sesion) {
        sesionesActivas.add(sesion);
    }

    public static List<SesionInfo> listar() {
        return Collections.unmodifiableList(new ArrayList<>(sesionesActivas));
    }

    public static int cantidadSesiones() {
        return sesionesActivas.size();
    }

    public static SesionInfo buscarPorPid(int pid) {
        synchronized (sesionesActivas) {
            for (SesionInfo s : sesionesActivas) {
                if (s.pid == pid) {
                    return s;
                }
            }
        }
        return null;
    }

    public static void cerrar(SesionInfo sesion) {
        try {
            if (sesion.conexion != null && !sesion.conexion.isClosed()) {
                sesion.conexion.close();
            }
        } catch (Exception e) {
            System.out.println("Error cerrando conexión:");
            System.out.println(e.getMessage());
        } finally {
            sesionesActivas.remove(sesion);
        }
    }
}