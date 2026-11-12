import java.util.ArrayList;
import java.util.List;

public class Nodo {

    private final String tablero;
    private Nodo anterior;
    private int profundidad;
    private int puntaje;

    // nodo raiz, no tiene papa
    public Nodo(String tablero) {
        this.tablero = tablero;
        this.anterior = null;
        this.profundidad = 0;
        this.puntaje = 0;
    }

    // nodo hijo, hereda la profundidad del papa + 1
    public Nodo(String tablero, Nodo anterior) {
        this.tablero = tablero;
        this.anterior = anterior;
        this.profundidad = anterior.profundidad + 1;
        this.puntaje = 0;
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

    public void setAnterior(Nodo anterior) {
        this.anterior = anterior;
    }

    public void setProfundidad(int profundidad) {
        this.profundidad = profundidad;
    }

    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
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
