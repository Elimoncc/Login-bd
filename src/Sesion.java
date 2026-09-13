public class Sesion {

    private int idSesion;
    private int idUser;
    private int pid;
    private boolean activo;

    public Sesion(int idSesion, int idUser, int pid, boolean activo) {
        this.idSesion = idSesion;
        this.idUser = idUser;
        this.pid = pid;
        this.activo = activo;
    }

    public int getIdSesion() {
        return idSesion;
    }

    public int getIdUser() {
        return idUser;
    }

    public int getPid() {
        return pid;
    }

    public boolean isActivo() {
        return activo;
    }
}