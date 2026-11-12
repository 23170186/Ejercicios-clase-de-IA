import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class ArbolDeBusqueda {

    private final Nodo inicio;

    public ArbolDeBusqueda(Nodo inicio) {
        this.inicio = inicio;
    }

    // anchura: va nivel por nivel con una cola
    public Nodo anchura(String meta) {
        if (inicio == null)
            return null;

        Set<String> vistos = new HashSet<>();
        Queue<Nodo> pendientes = new LinkedList<>();

        pendientes.add(inicio);
        vistos.add(inicio.getTablero());

        while (!pendientes.isEmpty()) {
            Nodo n = pendientes.poll();

            if (n.getTablero().equals(meta))
                return n;

            for (Nodo h : n.expandir()) {
                if (vistos.add(h.getTablero())) {
                    pendientes.add(h);
                }
            }
        }
        return null;
    }

    // profundidad: usa una pila, se va hasta el fondo antes de regresar
    public Nodo profundidad(String meta) {
        if (inicio == null)
            return null;

        Set<String> vistos = new HashSet<>();
        Deque<Nodo> pila = new ArrayDeque<>();

        pila.push(inicio);
        vistos.add(inicio.getTablero());

        while (!pila.isEmpty()) {
            Nodo n = pila.pop();

            if (n.getTablero().equals(meta))
                return n;

            List<Nodo> hijos = n.expandir();

            // se meten al reves para que salga primero el primer hijo
            for (int i = hijos.size() - 1; i >= 0; i--) {
                Nodo h = hijos.get(i);
                if (vistos.add(h.getTablero())) {
                    pila.push(h);
                }
            }
        }
        return null;
    }

    // costo uniforme: de los hijos se queda con el que mas se parece a la meta
    public Nodo costoUniforme(String meta) {
        if (inicio == null)
            return null;

        Set<String> vistos = new HashSet<>();
        Queue<Nodo> pendientes = new LinkedList<>();

        pendientes.add(inicio);
        vistos.add(inicio.getTablero());

        // casillas que se comparan contra la meta
        int[] casillas = { 0, 2, 4, 6, 8 };

        while (!pendientes.isEmpty()) {
            Nodo n = pendientes.poll();

            if (n.getTablero().equals(meta))
                return n;

            List<Nodo> hijos = n.expandir();
            Nodo elegido = null;
            int maxPuntos = -1;

            for (Nodo h : hijos) {
                if (vistos.contains(h.getTablero()))
                    continue;

                h.setAnterior(n);
                h.setProfundidad(n.getProfundidad() + 1);

                int puntos = 0;
                for (int c : casillas) {
                    if (h.getTablero().charAt(c) == meta.charAt(c))
                        puntos++;
                }
                h.setPuntaje(puntos);

                if (puntos > maxPuntos) {
                    elegido = h;
                    maxPuntos = puntos;
                }
            }

            if (elegido != null) {
                vistos.add(elegido.getTablero());
                pendientes.add(elegido);
            } else {
                // ninguno nuevo con puntaje, se agregan los que falten
                for (Nodo h : hijos) {
                    if (!vistos.contains(h.getTablero())) {
                        h.setAnterior(n);
                        h.setProfundidad(n.getProfundidad() + 1);
                        vistos.add(h.getTablero());
                        pendientes.add(h);
                    }
                }
            }
        }
        return null;
    }
}
