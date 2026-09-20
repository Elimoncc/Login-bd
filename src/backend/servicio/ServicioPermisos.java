package backend.servicio;

import backend.dao.PermisoDAO;
import backend.dao.RolFuncionDAO;
import backend.modelo.FuncionUsuario;
import backend.modelo.RolFuncion;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ServicioPermisos {

    private final PermisoDAO permisoDAO = new PermisoDAO();
    private final RolFuncionDAO rolFuncionDAO = new RolFuncionDAO();

    public List<FuncionUsuario> funcionesDe(int idUser) {
        return permisoDAO.listarFunciones(idUser);
    }

    /** Todas las funciones del sistema agrupadas por rol (mantiene el orden de la consulta). */
    public Map<String, List<RolFuncion>> funcionesPorRol() {
        Map<String, List<RolFuncion>> agrupadas = new LinkedHashMap<>();
        for (RolFuncion rf : rolFuncionDAO.listarTodas()) {
            agrupadas.computeIfAbsent(rf.rol(), r -> new java.util.ArrayList<>()).add(rf);
        }
        return agrupadas;
    }
}