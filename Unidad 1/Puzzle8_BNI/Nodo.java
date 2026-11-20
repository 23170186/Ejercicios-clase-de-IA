import java.util.ArrayList;
import java.util.List;

public class Nodo {

    private final String tablero;
    private final Nodo anterior;
    private final int profundidad; // numero de movimientos desde la raiz
    private final int costo;       // costo acumulado g(n)

    // nodo raiz, no tiene papa
    public Nodo(String tablero) {
        this.tablero = tablero;
        this.anterior = null;
        this.profundidad = 0;
        this.costo = 0;
    }

    // nodo hijo: profundidad del papa + 1, costo del papa + costo del movimiento.
    // El costo de mover una ficha es el numero de la ficha (mover la 5 cuesta 5).
    // Para que todos los movimientos cuesten 1, cambia la linea de "ficha" por: int ficha = 1;
    public Nodo(String tablero, Nodo anterior) {
        this.tablero = tablero;
        this.anterior = anterior;
        this.profundidad = anterior.profundidad + 1;
        // la ficha que se movio quedo en el lugar donde estaba el hueco del papa
        int ficha = tablero.charAt(anterior.tablero.indexOf(' ')) - '0';
        this.costo = anterior.costo + ficha;
    }

    public String getTablero() {
        return tablero;
    }

    public Nodo getAnterior() {
        return anterior;
    }

    public int getProfundidad() {
        return profundidad;
    }

    public int getCosto() {
        return costo;
    }

    // crea un nodo por cada movimiento posible
    public List<Nodo> expandir() {
        List<Nodo> hijos = new ArrayList<>();
        for (String t : Puzzle8.vecinos(tablero)) {
            hijos.add(new Nodo(t, this));
        }
        return hijos;
    }

}
