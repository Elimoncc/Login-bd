package backend.servicio;

import backend.dao.PermisoDAO;
import backend.dao.RolFuncionDAO;
import backend.modelo.RolFuncion;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ServicioPermisos {

    private final PermisoDAO permisoDAO = new PermisoDAO();
    private final RolFuncionDAO rolFuncionDAO = new RolFuncionDAO();

    public List<Integer> listarIU(int idUser) {
        return permisoDAO.listarIU(idUser);
    }

    public Map<String, List<RolFuncion>> funcionesPorRol() {

        Map<String, List<RolFuncion>> agrupadas =
            new LinkedHashMap<>();

        for (RolFuncion rf : rolFuncionDAO.listarTodas()) {

            agrupadas
                .computeIfAbsent(
                    rf.rol(),
                    r -> new ArrayList<>()
                )
                .add(rf);
        }

        return agrupadas;
    }
}