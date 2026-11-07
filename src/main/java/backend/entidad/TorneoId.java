package backend.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Embeddable
public class TorneoId implements Serializable {

    @Column(name = "deporte_id")
    private Integer deporteId;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    public TorneoId() { }

    public TorneoId(Integer deporteId, LocalDate fechaInicio) {
        this.deporteId = deporteId;
        this.fechaInicio = fechaInicio;
    }

    public Integer getDeporteId() { return deporteId; }
    public LocalDate getFechaInicio() { return fechaInicio; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TorneoId)) return false;
        TorneoId that = (TorneoId) o;
        return Objects.equals(deporteId, that.deporteId)
            && Objects.equals(fechaInicio, that.fechaInicio);
    }

    @Override
    public int hashCode() { return Objects.hash(deporteId, fechaInicio); }
}
