package frontend;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public abstract class Formulario extends JDialog {

    private int fila = 0;

    protected Formulario(Frame padre, String titulo) {
        super(padre, titulo, true);
        setLayout(new GridBagLayout());
    }

    protected void agregar(String etiqueta, JComponent campo) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 8, 5, 8);
        c.gridy = fila++;

        c.gridx = 0;
        c.anchor = GridBagConstraints.WEST;
        add(new JLabel(etiqueta), c);

        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(campo, c);
    }

    /** Agrega los botones y ajusta la ventana. Llamar al final del constructor. */
    protected void terminar(Runnable guardar) {
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(e -> {
            try {
                guardar.run();
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                                              JOptionPane.ERROR_MESSAGE);
            }
        });
        btnCancelar.addActionListener(e -> dispose());

        JPanel botones = new JPanel();
        botones.add(btnGuardar);
        botones.add(btnCancelar);

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = fila;
        c.gridwidth = 2;
        add(botones, c);

        getRootPane().setDefaultButton(btnGuardar);
        pack();
        setLocationRelativeTo(getOwner());
    }

    protected void informar(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    protected LocalDate fecha(String texto, boolean opcional) {
        if (texto == null || texto.isBlank()) {
            if (opcional) return null;
            throw new IllegalStateException("La fecha es obligatoria (AAAA-MM-DD).");
        }
        try {
            return LocalDate.parse(texto.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalStateException("Fecha inválida: usa el formato AAAA-MM-DD.");
        }
    }
}