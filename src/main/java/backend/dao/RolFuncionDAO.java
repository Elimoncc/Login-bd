package backend.dao;

import backend.config.HibernateUtil;
import backend.modelo.RolFuncion;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

public class RolFuncionDAO {

    public List<RolFuncion> listarTodas() {
        List<RolFuncion> lista = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Object[]> filas = session.createSelectionQuery(
                    "select r.nombre, f.nombre, f.descripcion " +
                    "from RolFuncion rf join rf.rol r join rf.funcion f " +
                    "order by r.id, f.id", Object[].class)
                .getResultList();

            for (Object[] f : filas) {
                lista.add(new RolFuncion((String) f[0], (String) f[1], (String) f[2]));
            }
        } catch (Exception e) {
            System.out.println("Error listando roles y funciones:");
            System.out.println(e.getMessage());
        }
        return lista;
    }
}
