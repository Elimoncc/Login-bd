package backend.entidad;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

import java.time.LocalDate;
import java.time.LocalTime;

/** Bitácora llenada por triggers de Postgres. Solo lectura desde Hibernate. */
@Entity
@Immutable
@Table(name = "log")
public class Log {

    @Id
    @Column(name = "id_log")
    private Integer id;

    private LocalDate fecha;
    private LocalTime hora;
    private String tabla;
    private String operacion;

    @Column(name = "usernombre")
    private String userNombre;

    @Column(name = "datonuevo", length = 5000)
    private String datoNuevo;

    @Column(name = "datoviejo", length = 5000)
    private String datoViejo;

    public Log() { }

    public Integer getId() { return id; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHora() { return hora; }
    public String getTabla() { return tabla; }
    public String getOperacion() { return operacion; }
    public String getUserNombre() { return userNombre; }
    public String getDatoNuevo() { return datoNuevo; }
    public String getDatoViejo() { return datoViejo; }
}
