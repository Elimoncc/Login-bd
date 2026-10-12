package backend.modelo;
//guardar datos inmutables
public record FuncionUsuario(String rol, int idFuncion, String funcion,
                             String categoria, String descripcion) {}