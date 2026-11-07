package backend.entidad;

import jakarta.persistence.*;

@Entity
@Table(name = "funcion_iu")
public class FuncionIU {

    @EmbeddedId
    private FuncionIuId id = new FuncionIuId();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("idFuncion")
    @JoinColumn(name = "id_funcion")
    private Funcion funcion;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("idIu")
    @JoinColumn(name = "id_iu")
    private IU iu;

    public FuncionIU() { }

    public FuncionIU(Funcion funcion, IU iu) {
        this.funcion = funcion;
        this.iu = iu;
        this.id = new FuncionIuId(funcion.getId(), iu.getId());
    }

    public FuncionIuId getId() { return id; }
    public Funcion getFuncion() { return funcion; }
    public void setFuncion(Funcion funcion) { this.funcion = funcion; }
    public IU getIu() { return iu; }
    public void setIu(IU iu) { this.iu = iu; }
}
