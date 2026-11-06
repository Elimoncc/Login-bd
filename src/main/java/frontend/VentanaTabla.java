package frontend;

import backend.modelo.Tabla;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class VentanaTabla extends JDialog {

    public VentanaTabla(Frame padre, String titulo, Tabla tabla) {
        super(padre, titulo, true);
        setLayout(new BorderLayout(8, 8));

        DefaultTableModel modelo = new DefaultTableModel(tabla.columnas().toArray(), 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        for (Object[] fila : tabla.filas()) {
            modelo.addRow(fila);
        }

        JTable jt = new JTable(modelo);
        jt.setAutoCreateRowSorter(true);

        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        pie.add(btnCerrar);

        add(new JLabel("  " + tabla.filas().size() + " registro(s)"), BorderLayout.NORTH);
        add(new JScrollPane(jt), BorderLayout.CENTER);
        add(pie, BorderLayout.SOUTH);

        setSize(760, 380);
        setLocationRelativeTo(padre);
    }
}