package backend.dao;

import backend.config.HibernateUtil;
import backend.modelo.IU;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

public class PermisoDAO {

    // userN -> userN_rol -> rol_funcion -> funcion_iu -> iu
    private static final String RUTA =
        " from UserNRol ur, RolFuncion rf, FuncionIU fi join fi.iu i " +
        " where ur.usuario.id = :idUser and rf.rol = ur.rol and fi.funcion = rf.funcion " +
        " order by i.id";

    public List<Integer> listarIU(int idUser) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createSelectionQuery("select distinct i.id" + RUTA, Integer.class)
                    .setParameter("idUser", idUser)
                    .getResultList();
        } catch (Exception e) {
            System.out.println("Error obteniendo id_iu:");
            System.out.println(e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<IU> listarInterfaces(int idUser) {
        List<IU> lista = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Object[]> filas = session
                    .createSelectionQuery("select distinct i.id, i.nombre, i.descripcion" + RUTA, Object[].class)
                    .setParameter("idUser", idUser)
                    .getResultList();

            for (Object[] f : filas) {
                lista.add(new IU((Integer) f[0], (String) f[1], (String) f[2]));
            }
        } catch (Exception e) {
            System.out.println("Error obteniendo interfaces (IU):");
            System.out.println(e.getMessage());
        }
        return lista;
    }
}
