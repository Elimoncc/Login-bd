package backend.dao;

import backend.config.HibernateUtil;
import backend.modelo.Usuario;
import jakarta.persistence.Tuple;
import org.hibernate.Session;

import java.util.List;

public class UsuarioDAO {

    public Usuario buscarUsuario(String nombre, String password) {

        String sql = "SELECT * FROM buscar_usuario(:nombre, :password)";

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            List<Tuple> filas = session
                    .createNativeQuery(sql, Tuple.class)
                    .setParameter("nombre", nombre)
                    .setParameter("password", password)
                    .getResultList();

            if (!filas.isEmpty()) {
                Tuple f = filas.get(0);
                return new Usuario(
                        f.get("id_usern", Number.class).intValue(),
                        f.get("nombre", String.class),
                        f.get("password", String.class),
                        f.get("activo", Boolean.class));
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Error consultando usuarios: " + e.getMessage());
        }

        return null;
    }
}