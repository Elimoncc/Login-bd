package frontend;

import backend.modelo.Tabla;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public record Opcion(Object[] fila, String texto) {

    /** El primer campo de la fila es siempre el id. */
    public int id() {
        return ((Number) fila[0]).intValue();
    }

    @Override
    public String toString() {
        return texto;
    }

    public static List<Opcion> de(Tabla tabla, Function<Object[], String> texto) {
        List<Opcion> lista = new ArrayList<>();
        for (Object[] f : tabla.filas()) {
            lista.add(new Opcion(f, texto.apply(f)));
        }
        return lista;
    }
}