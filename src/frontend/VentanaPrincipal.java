package frontend;

import backend.modelo.FuncionUsuario;
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
import java.util.Map;

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
import javax.swing.JTabbedPane;

public class VentanaPrincipal extends JFrame {

    private final GestorSesiones.SesionInfo sesion;
    private final ServicioLogin servicio = new ServicioLogin();
    private final Map<String, Map<String, List<FuncionUsuario>>> funciones;
    private final ServicioGestion gestion = new ServicioGestion();
    public VentanaPrincipal(GestorSesiones.SesionInfo sesion) {
        super("Sistema Deportivo");
        this.sesion = sesion;
        this.funciones = new ServicioPermisos().funcionesDeUsuario(sesion.idUser);

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

        setSize(520, 620);
        setLocationRelativeTo(null);
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel(
            "Bienvenido/a, " + sesion.nombreUsuario +
            "  (PID " + sesion.pid + ")  —  Rol: " + String.join(", ", funciones.keySet())));
        return panel;
    }

    private JComponent crearContenido() {
        if (funciones.isEmpty()) {
            JPanel vacio = new JPanel();
            vacio.add(new JLabel("Este usuario no tiene funciones asignadas."));
            return vacio;
        }

        JTabbedPane pestanias = new JTabbedPane();
        for (Map.Entry<String, Map<String, List<FuncionUsuario>>> rol : funciones.entrySet()) {
            pestanias.addTab(rol.getKey(), new JScrollPane(crearPanelRol(rol.getValue())));
        }
        return pestanias;
    }

    private JPanel crearPanelRol(Map<String, List<FuncionUsuario>> porCategoria) {
        JPanel contenedor = new JPanel();
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
        contenedor.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        for (Map.Entry<String, List<FuncionUsuario>> cat : porCategoria.entrySet()) {
            JPanel grupo = new JPanel(new GridLayout(0, 1, 4, 4));
            grupo.setBorder(BorderFactory.createTitledBorder(cat.getKey()));
            grupo.setAlignmentX(LEFT_ALIGNMENT);

            for (FuncionUsuario f : cat.getValue()) {
                JButton boton = new JButton(f.funcion());
                boton.addActionListener(e -> abrirFuncion(f));
                grupo.add(boton);
            }

            contenedor.add(grupo);
            contenedor.add(Box.createVerticalStrut(8));
        }
        return contenedor;
    }

    private void abrirFuncion(FuncionUsuario f) {
     try {
        switch (f.funcion()) {
            // Administrador
            case "Crear torneo"      -> new VentanaCrearTorneo(this, sesion).setVisible(true);
            case "Registrar jugador" -> new VentanaRegistrarJugador(this, sesion).setVisible(true);
            case "Registrar equipo"  -> new VentanaRegistrarEquipo(this, sesion).setVisible(true);
            case "Crear partido"     -> new VentanaCrearPartido(this, sesion).setVisible(true);

            // Árbitro
            case "Consultar equipos" ->
                new VentanaTabla(this, "Equipos", gestion.equipos(sesion.conexion)).setVisible(true);
            case "Consultar partidos asignados" ->
                new VentanaTabla(this, "Partidos asignados",
                    gestion.partidosArbitro(sesion.conexion, sesion.idUser)).setVisible(true);

            // Jugador
            case "Consultar torneos" ->
                new VentanaTabla(this, "Torneos", gestion.torneos(sesion.conexion)).setVisible(true);
            case "Consultar sus partidos" ->
                new VentanaTabla(this, "Mis partidos",
                    gestion.partidosJugador(sesion.conexion, sesion.idUser)).setVisible(true);

            default -> JOptionPane.showMessageDialog(this,
                "Función: " + f.funcion() + "\n(Pendiente de implementar)");
         }
      } catch (RuntimeException ex) {
         JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }
    }

    private JPanel crearPie() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalir = new JButton("Cerrar sesión");
        btnSalir.addActionListener(e -> cerrarSesion());
        panel.add(btnSalir);
        return panel;
    }

    private void cerrarSesion() {
        servicio.cerrar(sesion);
        dispose();
        new VentanaLogin().setVisible(true);
    }
}