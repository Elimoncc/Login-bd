package backend.dao;

import backend.config.HibernateUtil;
import backend.entidad.UserN;
import backend.modelo.Usuario;
import org.hibernate.Session;

public class UsuarioDAO {

    public Usuario buscarUsuario(String nombre, String password) {
        try (Session session = HibernateUtil.abrirSesion()) {

            UserN u = session.createSelectionQuery(
                        "from UserN where nombre = :nombre and password = :password", UserN.class)
                    .setParameter("nombre", nombre)
                    .setParameter("password", password)
                    .uniqueResult();

            if (u == null) return null;
            return new Usuario(u.getId(), u.getNombre(), u.getPassword(), u.getActivo());
        }
    }
}
