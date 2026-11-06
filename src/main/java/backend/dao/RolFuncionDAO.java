package backend.dao;

import backend.config.HibernateUtil;
import backend.modelo.RolFuncion;
import jakarta.persistence.Tuple;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

public class RolFuncionDAO {

    public List<RolFuncion> listarTodas() {

        String sql = "SELECT * FROM listar_roles_funciones()";

        List<RolFuncion> lista = new ArrayList<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            List<Tuple> filas = session
                    .createNativeQuery(sql, Tuple.class)
                    .getResultList();

            for (Tuple f : filas) {
                lista.add(new RolFuncion(
                        f.get("rol_nombre", String.class),
                        f.get("funcion_nombre", String.class),
                        f.get("funcion_descripcion", String.class)));
            }

        } catch (Exception e) {
            System.out.println("Error listando roles y funciones:");
            System.out.println(e.getMessage());
        }

        return lista;
    }
}
