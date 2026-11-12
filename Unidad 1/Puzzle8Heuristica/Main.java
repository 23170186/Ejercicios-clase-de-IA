import java.util.LinkedList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String inicial = "1238 4765";
        String meta = "1284376 5";

        ArbolDeBusqueda arbol = new ArbolDeBusqueda(new Nodo(inicial));
        Nodo resultado = arbol.anchura(meta);

        if (resultado == null) {
            System.out.println("No hay solucion.");
            return;
        }

        System.out.println("Se llego a la meta:");
        System.out.println(resultado.getTablero());
        System.out.println("Movimientos: " + resultado.getProfundidad());

        mostrarRuta(armarRuta(resultado));
    }

    // sube por los papas hasta la raiz y guarda cada tablero
    private static List<String> armarRuta(Nodo nodo) {
        LinkedList<String> ruta = new LinkedList<>();
        for (Nodo n = nodo; n != null; n = n.getAnterior()) {
            ruta.addFirst(n.getTablero());
        }
        return ruta;
    }

    private static void mostrarRuta(List<String> ruta) {
        System.out.println("\nRuta paso a paso:");

        for (String t : ruta) {
            dibujar(t);
            System.out.println("-----------");
        }
    }

    private static void dibujar(String t) {
        for (int fila = 0; fila < 3; fila++) {
            System.out.println(t.substring(fila * 3, fila * 3 + 3));
        }
    }
}
