package backend.servicio;

import backend.config.Conexion;
import backend.dao.SesionDAO;
import backend.dao.UsuarioDAO;
import backend.modelo.Usuario;
import java.sql.Connection;

public class ServicioLogin {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final SesionDAO sesionDAO = new SesionDAO();

    /** Autentica y registra la sesión. Lanza IllegalStateException con el motivo si falla. */
    public GestorSesiones.SesionInfo iniciar(String nombre, String password) {
        Usuario usuario = usuarioDAO.buscarUsuario(nombre, password);
        if (usuario == null) {
            throw new IllegalStateException("Usuario o contraseña incorrectos.");
        }
        if (!usuario.isActivo()) {
            throw new IllegalStateException("El usuario está inactivo.");
        }

        Connection conexion = Conexion.conectar();
        if (conexion == null) {
            throw new IllegalStateException("No se pudo conectar a la base de datos.");
        }

        int pid = Conexion.obtenerPid(conexion);
        if (pid == -1 || !sesionDAO.crearSesion(usuario.getIdUser(), pid)) {
            cerrarSilenciosamente(conexion);
            throw new IllegalStateException(
                "No se pudo crear la sesión (¿el usuario ya tiene una sesión activa?).");
        }

        GestorSesiones.SesionInfo sesion =
            new GestorSesiones.SesionInfo(conexion, usuario.getIdUser(), usuario.getNombre(), pid);
        GestorSesiones.agregar(sesion);
        return sesion;
    }

    public void cerrar(GestorSesiones.SesionInfo sesion) {
        sesionDAO.cerrarSesion(sesion.pid);
        GestorSesiones.cerrar(sesion);
    }

    private void cerrarSilenciosamente(Connection conexion) {
        try { conexion.close(); } catch (Exception ignorada) { }
    }
}