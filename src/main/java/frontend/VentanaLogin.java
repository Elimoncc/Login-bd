package frontend;

import backend.servicio.GestorSesiones;
import backend.servicio.ServicioLogin;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
public class VentanaLogin extends JFrame {

    private final JTextField txtUsuario = new JTextField(15);
    private final JPasswordField txtPassword = new JPasswordField(15);
    private final ServicioLogin servicio = new ServicioLogin();

    public VentanaLogin() {
        super("Iniciar sesión");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0; add(new JLabel("Usuario:"), c);
        c.gridx = 1;              add(txtUsuario, c);
        c.gridx = 0; c.gridy = 1; add(new JLabel("Contraseña:"), c);
        c.gridx = 1;              add(txtPassword, c);

        JButton btnIngresar = new JButton("Ingresar");
        c.gridx = 0; c.gridy = 2; c.gridwidth = 2;
        add(btnIngresar, c);
        
        JButton btnFunciones = new JButton("Ver funciones del sistema");
        c.gridy = 3;
        add(btnFunciones, c);
        btnFunciones.addActionListener(e -> new VentanaFunciones(this).setVisible(true));

        btnIngresar.addActionListener(e -> ingresar());
        getRootPane().setDefaultButton(btnIngresar);
        pack();
        setLocationRelativeTo(null);
    }

    private void ingresar() {
        String nombre = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (nombre.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa usuario y contraseña.");
            return;
        }

        try {
            GestorSesiones.SesionInfo sesion = servicio.iniciar(nombre, password);
            new VentanaPrincipal(sesion).setVisible(true);
            dispose();
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
        }
    }
}