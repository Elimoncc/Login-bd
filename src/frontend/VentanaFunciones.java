package frontend;

import backend.modelo.RolFuncion;
import backend.servicio.ServicioPermisos;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

public class VentanaFunciones extends JDialog {

    public VentanaFunciones(Frame padre) {
        super(padre, "Funciones del sistema por rol", true);
        setLayout(new BorderLayout(10, 10));

        add(new JScrollPane(crearArbol()), BorderLayout.CENTER);
        add(crearPie(), BorderLayout.SOUTH);

        setSize(460, 460);
        setLocationRelativeTo(padre);
    }

    private JTree crearArbol() {
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("Roles del sistema");
        Map<String, List<RolFuncion>> porRol = new ServicioPermisos().funcionesPorRol();

        if (porRol.isEmpty()) {
            raiz.add(new DefaultMutableTreeNode("(No hay roles ni funciones registrados)"));
        }

        for (Map.Entry<String, List<RolFuncion>> entrada : porRol.entrySet()) {
            DefaultMutableTreeNode nodoRol = new DefaultMutableTreeNode(entrada.getKey());
            for (RolFuncion rf : entrada.getValue()) {
                nodoRol.add(new DefaultMutableTreeNode(textoFuncion(rf)));
            }
            raiz.add(nodoRol);
        }

        JTree arbol = new JTree(raiz);
        // getRowCount() crece a medida que se expanden nodos, por eso se reevalúa en cada vuelta
        for (int i = 0; i < arbol.getRowCount(); i++) {
            arbol.expandRow(i);
        }
        return arbol;
    }

    private String textoFuncion(RolFuncion rf) {
        boolean tieneDescripcion = rf.descripcion() != null && !rf.descripcion().isBlank();
        return tieneDescripcion ? rf.funcion() + "  —  " + rf.descripcion() : rf.funcion();
    }

    private JPanel crearPie() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        panel.add(btnCerrar);
        return panel;
    }
}