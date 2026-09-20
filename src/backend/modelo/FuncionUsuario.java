package backend.modelo;
/** Una función disponible para un usuario, indicando de qué rol proviene. */
public record FuncionUsuario(String rol, int idFuncion, String funcion, String interfaz) {}