package backend.entidad;

import jakarta.persistence.*;

@Entity
@Table(name = "partido")
public class Partido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partido_id")
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "torneo_deporte_id",
                    referencedColumnName = "deporte_id", nullable = false),
        @JoinColumn(name = "torneo_fecha_inicio",
                    referencedColumnName = "fecha_inicio", nullable = false)
    })
    private Torneo torneo;

    @Column(name = "tipo_partido", nullable = false, length = 20)
    private String tipoPartido;   // '1VS1' o 'EQUIPO'

    @Column(name = "cantidad_vs", nullable = false, length = 50)
    private String cantidadVs;

    public Partido() { }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Torneo getTorneo() { return torneo; }
    public void setTorneo(Torneo torneo) { this.torneo = torneo; }
    public String getTipoPartido() { return tipoPartido; }
    public void setTipoPartido(String tipoPartido) { this.tipoPartido = tipoPartido; }
    public String getCantidadVs() { return cantidadVs; }
    public void setCantidadVs(String cantidadVs) { this.cantidadVs = cantidadVs; }
}
