package frontend;

import backend.modelo.FuncionUsuario;
import backend.servicio.GestorSesiones;
import backend.servicio.ServicioLogin;
import backend.servicio.ServicioPermisos;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
        panel.add(new JLabel("Bienvenido/a, " + sesion.nombreUsuario + "  (PID " + sesion.pid + ")"));
        return panel;
    }

    private JPanel crearPanelFunciones() {
        List<FuncionUsuario> funciones = new ServicioPermisos().funcionesDe(sesion.idUser);
        JPanel contenedor = new JPanel();
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
        contenedor.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        if (funciones.isEmpty()) {
            contenedor.add(new JLabel("Este usuario no tiene roles ni funciones asignadas."));
            return contenedor;
        }

        // Agrupa las funciones por rol, respetando el orden de la consulta
        Map<String, JPanel> panelesPorRol = new LinkedHashMap<>();
        for (FuncionUsuario f : funciones) {
            JPanel panelRol = panelesPorRol.computeIfAbsent(f.rol(), this::crearPanelRol);
            panelRol.add(crearBoton(f));
        }
        panelesPorRol.values().forEach(contenedor::add);
        return contenedor;
    }

    private JPanel crearPanelRol(String rol) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Como " + rol));
        return panel;
    }

    private JButton crearBoton(FuncionUsuario f) {
        JButton boton = new JButton(f.funcion());
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);
        boton.setToolTipText("Abre: " + f.interfaz());
        boton.addActionListener(e -> abrirFuncion(f));
        return boton;
    }

    private JPanel crearPie() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalir = new JButton("Cerrar sesión");
        btnSalir.addActionListener(e -> cerrarSesion());
        panel.add(btnSalir);
        return panel;
    }

    /** Punto de extensión: aquí se abrirá la pantalla real de cada función. */
    private void abrirFuncion(FuncionUsuario f) {
        JOptionPane.showMessageDialog(this,
            "Función: " + f.funcion() + "\nPantalla: " + f.interfaz() + "\n(Rol: " + f.rol() + ")");
    }

    private void cerrarSesion() {
        servicio.cerrar(sesion);
        dispose();
        new VentanaLogin().setVisible(true);
    }
}