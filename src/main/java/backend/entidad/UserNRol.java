package backend.entidad;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;

@Entity
@DynamicInsert
@Table(name = "usern_rol")
public class UserNRol {

    @EmbeddedId // incrustando una clave primaria compuesta
    private UserNRolId id = new UserNRolId();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("idUser") //integrando  los ID de usuario
    @JoinColumn(name = "id_usern")
    private UserN usuario;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("idRol")
    @JoinColumn(name = "id_rol")
    private Rol rol;

    private LocalDate hasta;

    private Boolean activo;

    public UserNRol() { }

    public UserNRol(UserN usuario, Rol rol, LocalDate desde) {
        this.usuario = usuario;
        this.rol = rol;
        this.id = new UserNRolId(usuario.getId(), rol.getId(), desde);
    }

    public UserNRolId getId() { return id; }
    public UserN getUsuario() { return usuario; }
    public Rol getRol() { return rol; }
    public LocalDate getHasta() { return hasta; }
    public void setHasta(LocalDate hasta) { this.hasta = hasta; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
