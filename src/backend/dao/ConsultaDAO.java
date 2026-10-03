package backend.dao;

import backend.modelo.Tabla;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConsultaDAO {

    public Tabla consultar(Connection conexion, String sql, Object... params) {
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData md = rs.getMetaData();
                int n = md.getColumnCount();

                List<String> columnas = new ArrayList<>();
                for (int i = 1; i <= n; i++) {
                    columnas.add(md.getColumnLabel(i));
                }

                List<Object[]> filas = new ArrayList<>();
                while (rs.next()) {
                    Object[] fila = new Object[n];
                    for (int i = 0; i < n; i++) {
                        fila[i] = rs.getObject(i + 1);
                    }
                    filas.add(fila);
                }
                return new Tabla(columnas, filas);
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Error consultando: " + e.getMessage());
        }
    }
}