public class Usuario {

    private int idUser;
    private String nombre;
    private String password;
    private boolean activo;

    public Usuario(int idUser, String nombre, String password, boolean activo) {
        this.idUser = idUser;
        this.nombre = nombre;
        this.password = password;
        this.activo = activo;
    }

    public int getIdUser() {
        return idUser;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPassword() {
        return password;
    }

    public boolean isActivo() {
        return activo;
    }
}