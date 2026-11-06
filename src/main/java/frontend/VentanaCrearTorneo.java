package frontend;

import backend.servicio.GestorSesiones;
import backend.servicio.ServicioGestion;
import java.awt.Frame;
import javax.swing.JComboBox;
import javax.swing.JTextField;

public class VentanaCrearTorneo extends Formulario {

    private final ServicioGestion servicio = new ServicioGestion();
    private final GestorSesiones.SesionInfo sesion;

    private final JComboBox<Opcion> cmbDeporte = new JComboBox<>();
    private final JTextField txtNombre = new JTextField(20);
    private final JTextField txtInicio = new JTextField(10);
    private final JTextField txtFin = new JTextField(10);

    public VentanaCrearTorneo(Frame padre, GestorSesiones.SesionInfo sesion) {
        super(padre, "Crear torneo");
        this.sesion = sesion;

        for (Opcion o : Opcion.de(servicio.deportes(sesion.conexion), f -> (String) f[1])) {
            cmbDeporte.addItem(o);
        }

        agregar("Deporte:", cmbDeporte);
        agregar("Nombre:", txtNombre);
        agregar("Inicio (AAAA-MM-DD):", txtInicio);
        agregar("Fin (opcional):", txtFin);
        terminar(this::guardar);
    }

    private void guardar() {
        Opcion deporte = (Opcion) cmbDeporte.getSelectedItem();
        if (deporte == null) {
            throw new IllegalStateException("No hay deportes registrados.");
        }

        servicio.crearTorneo(
            sesion.conexion,
            deporte.id(),
            txtNombre.getText(),
            fecha(txtInicio.getText(), false),
            fecha(txtFin.getText(), true));

        informar("Torneo creado correctamente.");
        dispose();
    }
}