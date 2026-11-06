package backend.dao;

import backend.config.SesionOrm;
import backend.modelo.Tabla;
import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class ConsultaDAO {

    public Tabla consultar(Connection conexion, String sql, Object... params) {
        try (Session s = SesionOrm.abrir(conexion)) {

            NativeQuery<Tuple> q = s.createNativeQuery(numerar(sql), Tuple.class);
            for (int i = 0; i < params.length; i++) {
                q.setParameter(i + 1, params[i]);
            }
            List<Tuple> resultado = q.getResultList();

            List<String> columnas = new ArrayList<>();
            List<Object[]> filas = new ArrayList<>();

            if (resultado.isEmpty()) {
                columnas = columnasDelSelect(sql);   // sin filas no hay metadatos
            } else {
                for (TupleElement<?> e : resultado.get(0).getElements()) {
                    columnas.add(e.getAlias());
                }
                for (Tuple t : resultado) {
                    filas.add(t.toArray());
                }
            }
            return new Tabla(columnas, filas);

        } catch (Exception e) {
            throw new IllegalStateException("Error consultando: " + SesionOrm.mensaje(e));
        }
    }

    /** Convierte cada ? en ?1, ?2, ... (parámetros posicionales de Hibernate 6). */
    private String numerar(String sql) {
        StringBuilder sb = new StringBuilder();
        int n = 0;
        for (char ch : sql.toCharArray()) {
            if (ch == '?') sb.append('?').append(++n);
            else sb.append(ch);
        }
        return sb.toString();
    }

    /** Lee los nombres de columna entre SELECT y FROM, para tablas vacías. */
    private List<String> columnasDelSelect(String sql) {
        String bajo = sql.toLowerCase();
        int ini = bajo.indexOf("select") + 6;
        int fin = bajo.indexOf(" from ");
        List<String> columnas = new ArrayList<>();
        if (ini < 6 || fin < 0) return columnas;
        for (String c : sql.substring(ini, fin).split(",")) {
            columnas.add(c.trim());
        }
        return columnas;
    }
}
