/*
Compilar: javac -cp "lib/postgresql-42.7.12.jar" -d out $(find src -name "*.java")
Ejecutar: java -cp "lib/postgresql-42.7.12.jar:out" app.Main
*/

package app;

import frontend.VentanaLogin;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaLogin().setVisible(true));
    }
}