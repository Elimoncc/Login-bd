package backend.entidad;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "sesion")
@DynamicInsert
public class Sesion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sesion")
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usern")
    private UserN usuario;

    private LocalDate fecha;   // la llena la BD (DEFAULT)
    private LocalTime hora;    // la llena la BD (DEFAULT)
    private Boolean activo;
    private Integer pid;

    public Sesion() { }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public UserN getUsuario() { return usuario; }
    public void setUsuario(UserN usuario) { this.usuario = usuario; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public Integer getPid() { return pid; }
    public void setPid(Integer pid) { this.pid = pid; }
}