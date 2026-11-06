package frontend;

import backend.modelo.Tabla;
import backend.servicio.GestorSesiones;
import backend.servicio.ServicioGestion;
import java.awt.Frame;
import java.time.LocalDate;
import javax.swing.JComboBox;
import javax.swing.JTextField;

public class VentanaCrearPartido extends Formulario {

    private final ServicioGestion servicio = new ServicioGestion();
    private final GestorSesiones.SesionInfo sesion;

    private final JComboBox<Opcion> cmbTorneo = new JComboBox<>();
    private final JComboBox<String> cmbTipo = new JComboBox<>(new String[] {"1VS1", "EQUIPO"});
    private final JComboBox<Opcion> cmbLocal = new JComboBox<>();
    private final JComboBox<Opcion> cmbVisitante = new JComboBox<>();
    private final JTextField txtCantidad = new JTextField(10);

    public VentanaCrearPartido(Frame padre, GestorSesiones.SesionInfo sesion) {
        super(padre, "Crear partido");
        this.sesion = sesion;

        // fila: deporte_id, deporte, nombre, fecha_inicio, fecha_fin
        for (Opcion o : Opcion.de(servicio.torneos(sesion.conexion),
                f -> f[2] + " (" + f[1] + ") " + f[3])) {
            cmbTorneo.addItem(o);
        }

        agregar("Torneo:", cmbTorneo);
        agregar("Tipo:", cmbTipo);
        agregar("Local:", cmbLocal);
        agregar("Visitante:", cmbVisitante);
        agregar("Modalidad (ej. 5VS5):", txtCantidad);

        cmbTorneo.addActionListener(e -> recargar());
        cmbTipo.addActionListener(e -> recargar());
        recargar();

        terminar(this::guardar);
    }

    /** Llena local/visitante con jugadores (1VS1) o con equipos del deporte del torneo. */
    private void recargar() {
        cmbLocal.removeAllItems();
        cmbVisitante.removeAllItems();

        Opcion torneo = (Opcion) cmbTorneo.getSelectedItem();
        String tipo = (String) cmbTipo.getSelectedItem();
        if (torneo == null || tipo == null) return;

        Tabla tabla;
        if (tipo.equals("1VS1")) {
            tabla = servicio.jugadores(sesion.conexion);
            txtCantidad.setText("1VS1");
            txtCantidad.setEnabled(false);
        } else {
            tabla = servicio.equiposDeDeporte(sesion.conexion, torneo.id());
            if (!txtCantidad.isEnabled()) txtCantidad.setText("");
            txtCantidad.setEnabled(true);
        }

        for (Opcion o : Opcion.de(tabla, f -> (String) f[1])) {
            cmbLocal.addItem(o);
            cmbVisitante.addItem(o);
        }
        if (cmbVisitante.getItemCount() > 1) {
            cmbVisitante.setSelectedIndex(1);
        }
    }

    private void guardar() {
        Opcion torneo = (Opcion) cmbTorneo.getSelectedItem();
        Opcion local = (Opcion) cmbLocal.getSelectedItem();
        Opcion visitante = (Opcion) cmbVisitante.getSelectedItem();

        if (torneo == null) throw new IllegalStateException("No hay torneos creados.");
        if (local == null || visitante == null)
            throw new IllegalStateException("No hay suficientes participantes para este tipo.");

        LocalDate inicio = ((java.sql.Date) torneo.fila()[3]).toLocalDate();

        servicio.crearPartido(
            sesion.conexion,
            torneo.id(),                       // deporte_id del torneo
            inicio,
            (String) cmbTipo.getSelectedItem(),
            txtCantidad.getText(),
            local.id(),
            visitante.id());

        informar("Partido creado correctamente.");
        dispose();
    }
}