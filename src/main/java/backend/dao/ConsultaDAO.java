package backend.dao;

import backend.config.Errores;
import backend.config.HibernateUtil;
import backend.modelo.Tabla;
import org.hibernate.Session;
import org.hibernate.query.SelectionQuery;

import java.util.List;

public class ConsultaDAO {

    /** Ejecuta una consulta HQL de varias columnas. Parámetros posicionales: ?1, ?2... */
    public Tabla consultar(List<String> columnas, String hql, Object... params) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            SelectionQuery<Object[]> q = session.createSelectionQuery(hql, Object[].class);
            for (int i = 0; i < params.length; i++) {
                q.setParameter(i + 1, params[i]);
            }
            return new Tabla(columnas, q.getResultList());
        } catch (Exception e) {
            throw new IllegalStateException("Error consultando: " + Errores.raiz(e));
        }
    }
}
