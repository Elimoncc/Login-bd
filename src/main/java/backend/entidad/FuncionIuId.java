package backend.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FuncionIuId implements Serializable {

    @Column(name = "id_funcion")
    private Integer idFuncion;

    @Column(name = "id_iu")
    private Integer idIu;

    public FuncionIuId() { }

    public FuncionIuId(Integer idFuncion, Integer idIu) {
        this.idFuncion = idFuncion;
        this.idIu = idIu;
    }

    public Integer getIdFuncion() { return idFuncion; }
    public Integer getIdIu() { return idIu; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FuncionIuId)) return false;
        FuncionIuId that = (FuncionIuId) o;
        return Objects.equals(idFuncion, that.idFuncion) && Objects.equals(idIu, that.idIu);
    }

    @Override
    public int hashCode() { return Objects.hash(idFuncion, idIu); }
}
