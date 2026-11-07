package backend.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class JugadorEquipoId implements Serializable {

    @Column(name = "jugador_id")
    private Integer jugadorId;

    @Column(name = "equipo_id")
    private Integer equipoId;

    public JugadorEquipoId() { }

    public JugadorEquipoId(Integer jugadorId, Integer equipoId) {
        this.jugadorId = jugadorId;
        this.equipoId = equipoId;
    }

    public Integer getJugadorId() { return jugadorId; }
    public Integer getEquipoId() { return equipoId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JugadorEquipoId)) return false;
        JugadorEquipoId that = (JugadorEquipoId) o;
        return Objects.equals(jugadorId, that.jugadorId)
            && Objects.equals(equipoId, that.equipoId);
    }

    @Override
    public int hashCode() { return Objects.hash(jugadorId, equipoId); }
}
