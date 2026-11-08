package backend.servicio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GestorSesiones {

    public static class SesionInfo {
        public final int idSesion;
        public final int idUser;
        public final String nombreUsuario;

        public SesionInfo(int idSesion, int idUser, String nombreUsuario) {
            this.idSesion = idSesion;
            this.idUser = idUser;
            this.nombreUsuario = nombreUsuario;
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

    public static void cerrar(SesionInfo sesion) {
        sesionesActivas.remove(sesion);
    }
}
