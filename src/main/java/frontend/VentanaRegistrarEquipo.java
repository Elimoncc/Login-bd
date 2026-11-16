package frontend;

import java.awt.Frame;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import backend.servicio.GestorSesiones;
import backend.servicio.ServicioEquipos;
import backend.servicio.ServicioGestion;

public class VentanaRegistrarEquipo extends Formulario {

    private final ServicioEquipos servicioEquipos = new ServicioEquipos();
    private final GestorSesiones.SesionInfo sesion;

    private final JTextField txtNombre = new JTextField(20);
    private final JComboBox<Opcion> cmbDeporte = new JComboBox<>();

    public VentanaRegistrarEquipo(Frame padre, GestorSesiones.SesionInfo sesion) {
        super(padre, "Registrar equipo");
        this.sesion = sesion;

        ServicioGestion gestion = new ServicioGestion();
        for (Opcion o : Opcion.de(gestion.deportes(), f -> (String) f[1])) {
            cmbDeporte.addItem(o);
        }

        agregar("Nombre:", txtNombre);
        agregar("Deporte:", cmbDeporte);
        terminar(this::guardar);
    }

        private void guardar() {
        Opcion deporte = (Opcion) cmbDeporte.getSelectedItem();
        if (deporte == null) {
            throw new IllegalStateException("No hay deportes registrados.");
        }

        servicioEquipos.registrar(
            txtNombre.getText(),
            deporte.id(),
            // callback de éxito
            () -> {
                informar("Equipo registrado correctamente.");
                dispose();
            },
            // callback de error
            msg -> {
                throw new IllegalStateException(msg);
            });
    }
}