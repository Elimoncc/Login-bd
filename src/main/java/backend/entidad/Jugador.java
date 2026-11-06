package backend.entidad;

import jakarta.persistence.*;

@Entity
@Table(name = "jugador")
@PrimaryKeyJoinColumn(name = "persona_id")
public class Jugador extends Persona {

    @Column(name = "partidos_jugados")
    private Integer partidosJugados;

    public Jugador() { }

    public Integer getPartidosJugados() { return partidosJugados; }
    public void setPartidosJugados(Integer partidosJugados) { this.partidosJugados = partidosJugados; }
}