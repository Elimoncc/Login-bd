package backend.entidad;

import jakarta.persistence.*;

@Entity
@Table(name = "partido_1vs1")
public class Partido1vs1 {

    @Id
    @Column(name = "partido_id")
    private Integer id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "partido_id")
    private Partido partido;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "jugador_local_id", nullable = false)
    private Jugador jugadorLocal;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "jugador_visitante_id", nullable = false)
    private Jugador jugadorVisitante;

    public Partido1vs1() { }

    public Integer getId() { return id; }
    public Partido getPartido() { return partido; }
    public void setPartido(Partido partido) { this.partido = partido; }
    public Jugador getJugadorLocal() { return jugadorLocal; }
    public void setJugadorLocal(Jugador jugadorLocal) { this.jugadorLocal = jugadorLocal; }
    public Jugador getJugadorVisitante() { return jugadorVisitante; }
    public void setJugadorVisitante(Jugador jugadorVisitante) { this.jugadorVisitante = jugadorVisitante; }
}
