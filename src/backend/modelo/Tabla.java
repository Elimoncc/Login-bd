package backend.modelo;

import java.util.List;

public record Tabla(List<String> columnas, List<Object[]> filas) {}