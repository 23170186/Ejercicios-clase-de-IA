import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class ArbolDeBusqueda {

    private final Nodo inicio;

    // estadisticas de la ultima busqueda (para la tabla comparativa)
    private long expandidos;
    private int maxFrontera;
    private boolean corte; // true si la busqueda limitada se topo con el limite

    public ArbolDeBusqueda(Nodo inicio) {
        this.inicio = inicio;
    }

    public long getExpandidos() {
        return expandidos;
    }

    public int getMaxFrontera() {
        return maxFrontera;
    }

    private void reiniciar() {
        expandidos = 0;
        maxFrontera = 0;
        corte = false;
    }

    // 1. ANCHURA: nivel por nivel con una cola (FIFO)
    public Nodo anchura(String meta) {
        reiniciar();
        if (inicio == null)
            return null;

        Set<String> vistos = new HashSet<>();
        Queue<Nodo> pendientes = new ArrayDeque<>();

        pendientes.add(inicio);
        vistos.add(inicio.getTablero());

        while (!pendientes.isEmpty()) {
            maxFrontera = Math.max(maxFrontera, pendientes.size());
            Nodo n = pendientes.poll();

            if (n.getTablero().equals(meta))
                return n;

            expandidos++;
            for (Nodo h : n.expandir()) {
                if (vistos.add(h.getTablero())) {
                    pendientes.add(h);
                }
            }
        }
        return null;
    }

    // 2. PROFUNDIDAD: con una pila (LIFO), se va hasta el fondo antes de regresar
    public Nodo profundidad(String meta) {
        reiniciar();
        if (inicio == null)
            return null;

        Set<String> vistos = new HashSet<>();
        Deque<Nodo> pila = new ArrayDeque<>();

        pila.push(inicio);
        vistos.add(inicio.getTablero());

        while (!pila.isEmpty()) {
            maxFrontera = Math.max(maxFrontera, pila.size());
            Nodo n = pila.pop();

            if (n.getTablero().equals(meta))
                return n;

            expandidos++;
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

    // 3. COSTO UNIFORME: siempre expande el nodo con menor costo acumulado g(n).
    // No usa la meta para decidir nada (es no informada); solo mira lo que ya costo llegar.
    public Nodo costoUniforme(String meta) {
        if (inicio == null)
            return null;

        PriorityQueue<Nodo> frontera = new PriorityQueue<>(
                Comparator.comparingInt(Nodo::getCosto));
        Map<String, Integer> mejorCosto = new HashMap<>();

        frontera.add(inicio);
        mejorCosto.put(inicio.getTablero(), 0);

        while (!frontera.isEmpty()) {
            Nodo n = frontera.poll();

            if (n.getCosto() > mejorCosto.get(n.getTablero()))
                continue; // entrada vieja, ya hay un camino más barato

            if (n.getTablero().equals(meta))
                return n;

            for (Nodo h : n.expandir()) {
                Integer previo = mejorCosto.get(h.getTablero());
                if (previo == null || h.getCosto() < previo) {
                    mejorCosto.put(h.getTablero(), h.getCosto());
                    frontera.add(h);
                }
            }
        }
        return null;
    }

    // 4a. PROFUNDIDAD LIMITADA: profundidad pero sin pasar de "limite" movimientos.
    // Devuelve null si no encuentra la meta dentro del limite.
    public Nodo profundidadLimitada(String meta, int limite) {
        reiniciar();
        if (inicio == null)
            return null;
        return limitadaRec(inicio, meta, limite, new HashSet<>());
    }

    // Solo se revisan ciclos en la rama actual ("camino"), no en todo lo visitado,
    // porque con un limite un estado puede alcanzarse despues por un camino mas corto.
    private Nodo limitadaRec(Nodo n, String meta, int limite, Set<String> camino) {
        if (n.getTablero().equals(meta))
            return n;

        if (n.getProfundidad() >= limite) {
            corte = true;
            return null;
        }

        expandidos++;
        camino.add(n.getTablero());
        maxFrontera = Math.max(maxFrontera, camino.size());

        for (Nodo h : n.expandir()) {
            if (camino.contains(h.getTablero()))
                continue;
            Nodo r = limitadaRec(h, meta, limite, camino);
            if (r != null)
                return r;
        }

        camino.remove(n.getTablero());
        return null;
    }

    // 4b. PROFUNDIZACION ITERATIVA: repite la limitada con limite 0, 1, 2, ...
    // Combina lo bueno de anchura (completa y optima) con lo de profundidad (poca memoria).
    // maxLimite evita ciclar si no hay solucion (en el 8-puzzle, 31 basta).
    public Nodo iterativa(String meta, int maxLimite) {
        reiniciar();
        if (inicio == null)
            return null;

        for (int limite = 0; limite <= maxLimite; limite++) {
            corte = false;
            Nodo r = limitadaRec(inicio, meta, limite, new HashSet<>());
            if (r != null)
                return r;
            if (!corte) // se recorrio todo el arbol sin toparse con el limite: no hay solucion
                return null;
        }
        return null;
    }

    // 5. BIDIRECCIONAL: dos anchuras, una desde el inicio y otra desde la meta,
    // hasta que se encuentran. Funciona porque los movimientos son reversibles.
    public Nodo bidireccional(String meta) {
        reiniciar();
        if (inicio == null)
            return null;
        if (inicio.getTablero().equals(meta))
            return inicio;

        Map<String, Nodo> visA = new HashMap<>(); // desde el inicio
        Map<String, Nodo> visB = new HashMap<>(); // desde la meta
        Queue<Nodo> colaA = new ArrayDeque<>();
        Queue<Nodo> colaB = new ArrayDeque<>();

        Nodo fin = new Nodo(meta);
        colaA.add(inicio);
        visA.put(inicio.getTablero(), inicio);
        colaB.add(fin);
        visB.put(meta, fin);

        while (!colaA.isEmpty() && !colaB.isEmpty()) {
            maxFrontera = Math.max(maxFrontera, colaA.size() + colaB.size());

            // se expande un nivel completo por lado, alternando
            Nodo[] enc = expandirNivel(colaA, visA, visB);
            if (enc != null)
                return unir(enc[0], enc[1]);

            enc = expandirNivel(colaB, visB, visA);
            if (enc != null)
                return unir(enc[1], enc[0]);
        }
        return null;
    }

    // Expande un nivel completo. Si algun hijo ya fue visto por el otro lado, devuelve
    // {nodo propio, nodo del otro lado} del encuentro con menor longitud total.
    // Se termina el nivel completo antes de devolver para no quedarse con uno peor.
    private Nodo[] expandirNivel(Queue<Nodo> cola, Map<String, Nodo> propios,
            Map<String, Nodo> opuestos) {
        int tam = cola.size();
        Nodo[] mejor = null;
        int mejorLong = Integer.MAX_VALUE;

        for (int i = 0; i < tam; i++) {
            Nodo n = cola.poll();
            expandidos++;

            for (Nodo h : n.expandir()) {
                if (propios.containsKey(h.getTablero()))
                    continue;
                propios.put(h.getTablero(), h);
                cola.add(h);

                Nodo otro = opuestos.get(h.getTablero());
                if (otro != null) {
                    int largo = h.getProfundidad() + otro.getProfundidad();
                    if (largo < mejorLong) {
                        mejorLong = largo;
                        mejor = new Nodo[] { h, otro };
                    }
                }
            }
        }
        return mejor;
    }

    // Une la mitad que viene del inicio (f) con la que viene de la meta (b).
    // Ambos nodos tienen el mismo tablero. Se sigue la cadena de b hacia la meta
    // creando nodos nuevos colgados de f, asi el resultado es una ruta normal inicio -> meta.
    private Nodo unir(Nodo f, Nodo b) {
        Nodo actual = f;
        for (Nodo n = b.getAnterior(); n != null; n = n.getAnterior()) {
            actual = new Nodo(n.getTablero(), actual);
        }
        return actual;
    }
}
