package frontend;

import backend.servicio.GestorSesiones;
import backend.servicio.ServicioLogin;
import backend.servicio.ServicioPermisos;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class VentanaPrincipal extends JFrame {

    private final GestorSesiones.SesionInfo sesion;
    private final ServicioLogin servicio = new ServicioLogin();

    public VentanaPrincipal(GestorSesiones.SesionInfo sesion) {

        super("Sistema Deportivo");

        this.sesion = sesion;

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrarSesion();
            }
        });

        setLayout(new BorderLayout(10, 10));

        add(crearEncabezado(), BorderLayout.NORTH);
        add(new JScrollPane(crearPanelFunciones()), BorderLayout.CENTER);
        add(crearPie(), BorderLayout.SOUTH);

        setSize(480, 520);
        setLocationRelativeTo(null);
    }

    private JPanel crearEncabezado() {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        panel.add(new JLabel(
            "Bienvenido/a, " + sesion.nombreUsuario +
            "  (PID " + sesion.pid + ")"
        ));

        return panel;
    }

    private JPanel crearPanelFunciones() {

        List<Integer> ius =
            new ServicioPermisos().listarIU(sesion.idUser);

        JPanel contenedor = new JPanel();

        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));

        contenedor.setBorder(
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        );

        if (ius.isEmpty()) {

            contenedor.add(
                new JLabel("Este usuario no tiene interfaces asignadas.")
            );

            return contenedor;
        }

        for (int idIu : ius) {

            JButton boton = crearBoton(idIu);

            contenedor.add(boton);
        }

        return contenedor;
    }

    private JButton crearBoton(int idIu) {

        String nombre = obtenerNombreIU(idIu);

        JButton boton = new JButton(nombre);

        boton.setAlignmentX(JButton.LEFT_ALIGNMENT);

        boton.addActionListener(e -> abrirFuncion(idIu));

        return boton;
    }

    private String obtenerNombreIU(int idIu) {

        return switch (idIu) {

            case 1 -> "Inicio de sesión";
            case 2 -> "Registro de equipo";
            case 3 -> "Gestión de partidos";
            case 4 -> "Programación de partidos";
            case 5 -> "Resultados";
            case 6 -> "Impresión de resultados";

            default -> "IU " + idIu;
        };
    }

    private void abrirFuncion(int idIu) {

        JOptionPane.showMessageDialog(
            this,
            "Se seleccionó la interfaz con ID: " + idIu
        );
    }

    private JPanel crearPie() {

        JPanel panel = new JPanel(
            new FlowLayout(FlowLayout.RIGHT)
        );

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