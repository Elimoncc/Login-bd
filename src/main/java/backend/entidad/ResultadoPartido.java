package backend.entidad;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Entity
@DynamicInsert
@Table(name = "resultado_partido")
public class ResultadoPartido {

    @Id
    @Column(name = "partido_id")
    private Integer id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "partido_id")
    private Partido partido;

    private Boolean empate;                 // null -> la BD usa FALSE

    @Column(length = 250)
    private String observaciones;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;    // null -> la BD usa now()

    public ResultadoPartido() { }

    public Integer getId() { return id; }
    public Partido getPartido() { return partido; }
    public void setPartido(Partido partido) { this.partido = partido; }
    public Boolean getEmpate() { return empate; }
    public void setEmpate(Boolean empate) { this.empate = empate; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
}
