package backend.servicio;

import backend.dao.PermisoDAO;
import backend.dao.RolFuncionDAO;
import backend.modelo.FuncionUsuario;
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

    public Map<String, Map<String, List<FuncionUsuario>>> funcionesDeUsuario(int idUser) {

      Map<String, Map<String, List<FuncionUsuario>>> resultado = new LinkedHashMap<>();

      for (FuncionUsuario f : permisoDAO.listarFunciones(idUser)) {
        resultado
            .computeIfAbsent(f.rol(), r -> new LinkedHashMap<>())
            .computeIfAbsent(f.categoria(), c -> new ArrayList<>())
            .add(f);
      }

      return resultado;
    }
}