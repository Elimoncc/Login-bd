package frontend;

import backend.modelo.IU;
import backend.servicio.GestorSesiones;
import backend.servicio.ServicioLogin;
import backend.servicio.ServicioPermisos;
import backend.servicio.ServicioGestion;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Ventana principal de una sesión.
 * El menú se construye SOLO con las interfaces (id_iu) que devuelve
 * credenciales_userN(id_userN) → tabla iu.
 */
public class VentanaPrincipal extends JFrame {

    private final GestorSesiones.SesionInfo sesion;
    private final ServicioLogin servicio = new ServicioLogin();
    private final List<IU> interfaces;
    private final ServicioGestion gestion = new ServicioGestion();
    private boolean sesionCerrada = false;

    public VentanaPrincipal(GestorSesiones.SesionInfo sesion) {
        super("Sistema Deportivo — " + sesion.nombreUsuario);
        this.sesion = sesion;
        this.interfaces = new ServicioPermisos().interfacesDeUsuario(sesion.idUser);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrarSesion();
            }
        });

        setLayout(new BorderLayout(10, 10));
        add(crearEncabezado(), BorderLayout.NORTH);
        add(crearContenido(), BorderLayout.CENTER);
        add(crearPie(), BorderLayout.SOUTH);

        setSize(560, 640);
        setLocationRelativeTo(null);
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String ids = interfaces.isEmpty()
            ? "ninguna"
            : interfaces.stream()
                .map(iu -> String.valueOf(iu.idIu()))
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
        panel.add(new JLabel(
            "Bienvenido/a, " + sesion.nombreUsuario +
            "  (PID " + sesion.pid + ")  —  IU autorizadas: " + ids));
        return panel;
    }

    private JComponent crearContenido() {
        if (interfaces.isEmpty()) {
            JPanel vacio = new JPanel();
            vacio.add(new JLabel(
                "Este usuario no tiene interfaces (IU) asignadas vía credenciales_userN."));
            return vacio;
        }

        JPanel contenedor = new JPanel();
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
        contenedor.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JPanel grupo = new JPanel(new GridLayout(0, 1, 6, 6));
        grupo.setBorder(BorderFactory.createTitledBorder("Interfaces autorizadas"));
        grupo.setAlignmentX(LEFT_ALIGNMENT);

        for (IU iu : interfaces) {
            String texto = iu.nombre();
            if (iu.descripcion() != null && !iu.descripcion().isBlank()) {
                texto = iu.nombre() + "  —  " + iu.descripcion();
            }
            JButton boton = new JButton(texto);
            boton.addActionListener(e -> abrirInterfaz(iu));
            grupo.add(boton);
        }

        contenedor.add(grupo);
        contenedor.add(Box.createVerticalStrut(8));
        return new JScrollPane(contenedor);
    }

    /**
     * Abre la ventana según el nombre de la IU.
     * El nombre en la tabla iu debe coincidir exactamente con estos case.
     */
    private void abrirInterfaz(IU iu) {
        try {
            switch (iu.nombre()) {
                case "Crear torneo" ->
                    new VentanaCrearTorneo(this, sesion).setVisible(true);
                case "Registrar jugador" ->
                    new VentanaRegistrarJugador(this, sesion).setVisible(true);
                case "Registrar equipo" ->
                    new VentanaRegistrarEquipo(this, sesion).setVisible(true);
                case "Crear partido" ->
                    new VentanaCrearPartido(this, sesion).setVisible(true);

                case "Consultar equipos" ->
                    new VentanaTabla(this, "Equipos",
                        gestion.equipos(sesion.conexion)).setVisible(true);
                case "Consultar partidos asignados" ->
                    new VentanaTabla(this, "Partidos asignados",
                        gestion.partidosArbitro(sesion.conexion, sesion.idUser)).setVisible(true);

                case "Consultar torneos" ->
                    new VentanaTabla(this, "Torneos",
                        gestion.torneos(sesion.conexion)).setVisible(true);
                case "Consultar sus partidos" ->
                    new VentanaTabla(this, "Mis partidos",
                        gestion.partidosJugador(sesion.conexion, sesion.idUser)).setVisible(true);

                default -> JOptionPane.showMessageDialog(this,
                    "IU id=" + iu.idIu() + " — \"" + iu.nombre() + "\"\n"
                    + "(Pendiente de implementar en el cliente)");
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel crearPie() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnNuevaSesion = new JButton("Nueva sesión (otro usuario)");
        btnNuevaSesion.addActionListener(e -> new VentanaLogin().setVisible(true));
        panel.add(btnNuevaSesion);

        JButton btnSalir = new JButton("Cerrar sesión");
        btnSalir.addActionListener(e -> cerrarSesion());
        panel.add(btnSalir);
        return panel;
    }

    private void cerrarSesion() {
        if (sesionCerrada) {
            return;
        }
        sesionCerrada = true;
        servicio.cerrar(sesion);
        dispose();

        if (GestorSesiones.cantidadSesiones() == 0) {
            boolean hayLoginVisible = false;
            for (java.awt.Window w : java.awt.Window.getWindows()) {
                if (w instanceof VentanaLogin && w.isDisplayable() && w.isVisible()) {
                    hayLoginVisible = true;
                    break;
                }
            }
            if (!hayLoginVisible) {
                new VentanaLogin().setVisible(true);
            }
        }
    }
}