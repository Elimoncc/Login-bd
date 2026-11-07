package backend.entidad;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "programacion")
public class Programacion {

    @EmbeddedId
    private ProgramacionId id = new ProgramacionId();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("partidoId")
    @JoinColumn(name = "partido_id")
    private Partido partido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lugar_id")
    private Lugar lugar;          // puede ser null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arbitro_id")
    private Arbitro arbitro;      // puede ser null

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    public Programacion() { }

    /** 'fechaRegistro' forma parte de la clave, así que se asigna desde Java. */
    public Programacion(Partido partido, LocalDateTime fechaRegistro, LocalDate fecha, LocalTime hora) {
        this.partido = partido;
        this.fecha = fecha;
        this.hora = hora;
        this.id = new ProgramacionId(partido.getId(), fechaRegistro);
    }

    public ProgramacionId getId() { return id; }
    public Partido getPartido() { return partido; }
    public Lugar getLugar() { return lugar; }
    public void setLugar(Lugar lugar) { this.lugar = lugar; }
    public Arbitro getArbitro() { return arbitro; }
    public void setArbitro(Arbitro arbitro) { this.arbitro = arbitro; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }
}
