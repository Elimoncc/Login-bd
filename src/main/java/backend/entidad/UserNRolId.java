package backend.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Embeddable
public class UserNRolId implements Serializable {

    @Column(name = "id_usern")
    private Integer idUser;

    @Column(name = "id_rol")
    private Integer idRol;

    @Column(name = "desde")
    private LocalDate desde;

    public UserNRolId() { }

    public UserNRolId(Integer idUser, Integer idRol, LocalDate desde) {
        this.idUser = idUser;
        this.idRol = idRol;
        this.desde = desde;
    }

    public Integer getIdUser() { return idUser; }
    public Integer getIdRol() { return idRol; }
    public LocalDate getDesde() { return desde; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserNRolId)) return false;
        UserNRolId that = (UserNRolId) o;
        return Objects.equals(idUser, that.idUser)
            && Objects.equals(idRol, that.idRol)
            && Objects.equals(desde, that.desde);
    }

    @Override
    public int hashCode() { return Objects.hash(idUser, idRol, desde); }
}
