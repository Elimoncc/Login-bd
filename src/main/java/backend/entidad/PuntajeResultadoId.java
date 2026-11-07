package backend.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PuntajeResultadoId implements Serializable {

    @Column(name = "partido_id")
    private Integer partidoId;

    @Column(name = "lado", length = 10)
    private String lado;   // 'LOCAL' o 'VISITANTE'

    public PuntajeResultadoId() { }

    public PuntajeResultadoId(Integer partidoId, String lado) {
        this.partidoId = partidoId;
        this.lado = lado;
    }

    public Integer getPartidoId() { return partidoId; }
    public String getLado() { return lado; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PuntajeResultadoId)) return false;
        PuntajeResultadoId that = (PuntajeResultadoId) o;
        return Objects.equals(partidoId, that.partidoId) && Objects.equals(lado, that.lado);
    }

    @Override
    public int hashCode() { return Objects.hash(partidoId, lado); }
}
