package backend.entidad;

import jakarta.persistence.*;

@Entity
@Table(name = "arbitro")
@PrimaryKeyJoinColumn(name = "persona_id")
public class Arbitro extends Persona {
    public Arbitro() { }
}