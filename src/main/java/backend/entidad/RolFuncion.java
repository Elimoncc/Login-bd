package backend.entidad;

import jakarta.persistence.*;

@Entity
@Table(name = "rol_funcion")
public class RolFuncion {

    @EmbeddedId
    private RolFuncionId id = new RolFuncionId();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("idRol")
    @JoinColumn(name = "id_rol")
    private Rol rol;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("idFuncion")
    @JoinColumn(name = "id_funcion")
    private Funcion funcion;

    public RolFuncion() { }

    public RolFuncion(Rol rol, Funcion funcion) {
        this.rol = rol;
        this.funcion = funcion;
        this.id = new RolFuncionId(rol.getId(), funcion.getId());
    }

    public RolFuncionId getId() { return id; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    public Funcion getFuncion() { return funcion; }
    public void setFuncion(Funcion funcion) { this.funcion = funcion; }
}
