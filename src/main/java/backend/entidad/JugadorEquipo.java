package backend.entidad;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;

@Entity
@DynamicInsert
@Table(name = "jugador_equipo")
public class JugadorEquipo {

    @EmbeddedId
    private JugadorEquipoId id = new JugadorEquipoId();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("jugadorId")
    @JoinColumn(name = "jugador_id")
    private Jugador jugador;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("equipoId")
    @JoinColumn(name = "equipo_id")
    private Equipo equipo;

    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso;   // si es null, la BD usa CURRENT_DATE

    public JugadorEquipo() { }

    public JugadorEquipo(Jugador jugador, Equipo equipo) {
        this.jugador = jugador;
        this.equipo = equipo;
        this.id = new JugadorEquipoId(jugador.getId(), equipo.getId());
    }

    public JugadorEquipoId getId() { return id; }
    public Jugador getJugador() { return jugador; }
    public Equipo getEquipo() { return equipo; }
    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }
}
