package backend.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
public class ProgramacionId implements Serializable {

    @Column(name = "partido_id")
    private Integer partidoId;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    public ProgramacionId() { }

    public ProgramacionId(Integer partidoId, LocalDateTime fechaRegistro) {
        this.partidoId = partidoId;
        this.fechaRegistro = fechaRegistro;
    }

    public Integer getPartidoId() { return partidoId; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProgramacionId)) return false;
        ProgramacionId that = (ProgramacionId) o;
        return Objects.equals(partidoId, that.partidoId)
            && Objects.equals(fechaRegistro, that.fechaRegistro);
    }

    @Override
    public int hashCode() { return Objects.hash(partidoId, fechaRegistro); }
}
