package backend.entidad;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicInsert;

@Entity
@DynamicInsert
@Table(name = "puntaje_resultado")
public class PuntajeResultado {

    @EmbeddedId
    private PuntajeResultadoId id = new PuntajeResultadoId();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("partidoId")
    @JoinColumn(name = "partido_id")
    private ResultadoPartido resultado;

    private Integer puntos;   // null -> la BD usa 0

    public PuntajeResultado() { }

    public PuntajeResultado(ResultadoPartido resultado, String lado, Integer puntos) {
        this.resultado = resultado;
        this.puntos = puntos;
        this.id = new PuntajeResultadoId(resultado.getId(), lado);
    }

    public PuntajeResultadoId getId() { return id; }
    public ResultadoPartido getResultado() { return resultado; }
    public Integer getPuntos() { return puntos; }
    public void setPuntos(Integer puntos) { this.puntos = puntos; }
}
