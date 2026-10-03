package frontend;

import backend.servicio.GestorSesiones;
import backend.servicio.ServicioGestion;
import java.awt.Frame;
import javax.swing.JTextField;

public class VentanaRegistrarJugador extends Formulario {

    private final ServicioGestion servicio = new ServicioGestion();
    private final GestorSesiones.SesionInfo sesion;

    private final JTextField txtNombre = new JTextField(20);
    private final JTextField txtEmail = new JTextField(20);
    private final JTextField txtContacto = new JTextField(20);

    public VentanaRegistrarJugador(Frame padre, GestorSesiones.SesionInfo sesion) {
        super(padre, "Registrar jugador");
        this.sesion = sesion;

        agregar("Nombre:", txtNombre);
        agregar("Email:", txtEmail);
        agregar("Contacto:", txtContacto);
        terminar(this::guardar);
    }

    private void guardar() {
        servicio.registrarJugador(
            sesion.conexion,
            txtNombre.getText(),
            txtEmail.getText(),
            txtContacto.getText());

        informar("Jugador registrado correctamente.");
        dispose();
    }
}