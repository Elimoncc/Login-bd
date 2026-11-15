package backend.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import backend.config.HibernateUtil;
import backend.modelo.IU;

public class PermisoDAO {

    // userN -> userN_rol -> rol_funcion -> funcion_iu -> iu
    private static final String RUTA
            = " from UserNRol ur, RolFuncion rf, FuncionIU fi join fi.iu i "
            + " where ur.usuario.id = :idUser and ur.activo = true "
            + "   and ur.id.desde <= current_date "
            + "   and (ur.hasta is null or ur.hasta >= current_date) "
            + "   and rf.rol = ur.rol and fi.funcion = rf.funcion "
            + " order by i.id";

    public List<Integer> listarIU(int idUser) {
        try (Session session = HibernateUtil.abrirSesion()) {
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
        try (Session session = HibernateUtil.abrirSesion()) {
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
