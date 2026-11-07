package backend.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RolFuncionId implements Serializable {

    @Column(name = "id_rol")
    private Integer idRol;

    @Column(name = "id_funcion")
    private Integer idFuncion;

    public RolFuncionId() { }

    public RolFuncionId(Integer idRol, Integer idFuncion) {
        this.idRol = idRol;
        this.idFuncion = idFuncion;
    }

    public Integer getIdRol() { return idRol; }
    public Integer getIdFuncion() { return idFuncion; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RolFuncionId)) return false;
        RolFuncionId that = (RolFuncionId) o;
        return Objects.equals(idRol, that.idRol) && Objects.equals(idFuncion, that.idFuncion);
    }

    @Override
    public int hashCode() { return Objects.hash(idRol, idFuncion); }
}
