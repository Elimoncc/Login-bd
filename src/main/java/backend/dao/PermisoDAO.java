package backend.dao;

import backend.config.HibernateUtil;
import backend.modelo.IU;
import jakarta.persistence.Tuple;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

public class PermisoDAO {

    public List<Integer> listarIU(int idUser) {

        String sql = "SELECT id_iu FROM credenciales_userN(:idUser)";

        List<Integer> lista = new ArrayList<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            List<Tuple> filas = session
                    .createNativeQuery(sql, Tuple.class)
                    .setParameter("idUser", idUser)
                    .getResultList();

            for (Tuple f : filas) {
                lista.add(f.get("id_iu", Number.class).intValue());
            }

        } catch (Exception e) {
            System.out.println("Error obteniendo id_iu:");
            System.out.println(e.getMessage());
        }

        return lista;
    }

    public List<IU> listarInterfaces(int idUser) {

        String sql = "SELECT * FROM interfaces_usuario(:idUser)";

        List<IU> lista = new ArrayList<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            List<Tuple> filas = session
                    .createNativeQuery(sql, Tuple.class)
                    .setParameter("idUser", idUser)
                    .getResultList();

            for (Tuple f : filas) {
                lista.add(new IU(
                        f.get("id_iu", Number.class).intValue(),
                        f.get("nombre", String.class),
                        f.get("descripcion", String.class)));
            }

        } catch (Exception e) {
            System.out.println("Error obteniendo interfaces (IU):");
            System.out.println(e.getMessage());
        }

        return lista;
    }
}
