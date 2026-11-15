package backend.servicio;

import java.sql.Connection;

import backend.config.Conexion;
import backend.config.HibernateUtil;
import backend.dao.SesionDAO;
import backend.dao.UsuarioDAO;
import backend.modelo.Usuario;

public class ServicioLogin {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final SesionDAO sesionDAO = new SesionDAO();

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

        // El usuario queda marcado en la conexión ANTES de cualquier escritura
        if (!Conexion.fijarUsuario(conexion, usuario.getNombre())) {
            cerrarSilenciosamente(conexion);
            throw new IllegalStateException("No se pudo asociar el usuario a la conexión.");
        }

        int pid = Conexion.obtenerPid(conexion);
        Integer idSesion = (pid == -1) ? null : sesionDAO.crearSesion(usuario.getIdUser(), conexion, pid);
        if (idSesion == null) {
            cerrarSilenciosamente(conexion);
            throw new IllegalStateException("No se pudo crear la sesión (¿el usuario ya tiene una sesión activa?).");
        }

        HibernateUtil.setConexionActual(conexion);
        GestorSesiones.SesionInfo sesion
                = new GestorSesiones.SesionInfo(idSesion, conexion, usuario.getIdUser(), usuario.getNombre(), pid);
        GestorSesiones.agregar(sesion);
        return sesion;
    }

    public void cerrar(GestorSesiones.SesionInfo sesion) {
        sesionDAO.cerrarSesion(sesion.idSesion);     // primero: aún usa la conexión del usuario
        HibernateUtil.setConexionActual(null);
        GestorSesiones.cerrar(sesion);               // después: cierra la conexión
    }

    private void cerrarSilenciosamente(Connection conexion) {
        try {
            conexion.close();
        } catch (Exception ignorada) {
        }
    }
}
