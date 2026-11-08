package backend.servicio;

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

        Integer idSesion = sesionDAO.crearSesion(usuario.getIdUser());
        if (idSesion == null) {
            throw new IllegalStateException(
                "No se pudo crear la sesión (¿el usuario ya tiene una sesión activa?).");
        }

        GestorSesiones.SesionInfo sesion =
            new GestorSesiones.SesionInfo(idSesion, usuario.getIdUser(), usuario.getNombre());
        GestorSesiones.agregar(sesion);
        return sesion;
    }

    public void cerrar(GestorSesiones.SesionInfo sesion) {
        sesionDAO.cerrarSesion(sesion.idSesion);
        GestorSesiones.cerrar(sesion);
    }
}
