package backend.entidad;

import jakarta.persistence.*;

@Entity
@Table(name = "iu")
public class IU {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_iu")
    private Integer id;

    @Column(nullable = false, unique = true)
    private String nombre;

    private String descripcion;

    public IU() { }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}