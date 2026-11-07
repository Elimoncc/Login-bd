package backend.entidad;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "torneo")
public class Torneo {

    @EmbeddedId
    private TorneoId id = new TorneoId();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("deporteId")
    @JoinColumn(name = "deporte_id")
    private Deporte deporte;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;   // puede ser null

    public Torneo() { }

    public Torneo(Deporte deporte, LocalDate fechaInicio, String nombre) {
        this.deporte = deporte;
        this.nombre = nombre;
        this.id = new TorneoId(deporte.getId(), fechaInicio);
    }

    public TorneoId getId() { return id; }
    public Deporte getDeporte() { return deporte; }
    public void setDeporte(Deporte deporte) { this.deporte = deporte; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
}
