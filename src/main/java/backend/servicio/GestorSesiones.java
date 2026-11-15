package backend.servicio;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GestorSesiones {

    public static class SesionInfo {

        public final int idSesion;
        public final Connection conexion;
        public final int idUser;
        public final String nombreUsuario;
        public final int pid;

        public SesionInfo(int idSesion, Connection conexion, int idUser, String nombreUsuario, int pid) {
            this.idSesion = idSesion;
            this.conexion = conexion;
            this.idUser = idUser;
            this.nombreUsuario = nombreUsuario;
            this.pid = pid;
        }
    }

        public static void cerrar(SesionInfo sesion) {
        try {
            if (sesion.conexion != null) {
                sesion.conexion.close();
            }
        } catch (Exception e) {
            System.out.println("Error cerrando conexión: " + e.getMessage());
        } finally {
            sesionesActivas.remove(sesion);
        }
    }

    private static final List<SesionInfo> sesionesActivas
            = Collections.synchronizedList(new ArrayList<>());

    public static void agregar(SesionInfo sesion) {
        sesionesActivas.add(sesion);
    }

    public static List<SesionInfo> listar() {
        return Collections.unmodifiableList(new ArrayList<>(sesionesActivas));
    }

    public static int cantidadSesiones() {
        return sesionesActivas.size();
    }

    public static SesionInfo buscarPorId(int idSesion) {
        synchronized (sesionesActivas) {
            for (SesionInfo s : sesionesActivas) {
                if (s.idSesion == idSesion) {
                    return s;
                }
            }
        }
        return null;
    }
}
