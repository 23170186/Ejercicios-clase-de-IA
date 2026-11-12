import java.util.ArrayList;
import java.util.List;

public class Puzzle8 {

    // para cada posicion del hueco, con que casillas se puede intercambiar
    // (el orden importa porque define el orden de los sucesores)
    private static final int[][] MOVIMIENTOS = {
            { 1, 3 },       // hueco en 0
            { 0, 2, 4 },    // hueco en 1
            { 1, 5 },       // hueco en 2
            { 0, 4, 6 },    // hueco en 3
            { 1, 7, 3, 5 }, // hueco en 4
            { 2, 4, 8 },    // hueco en 5
            { 3, 7 },       // hueco en 6
            { 4, 6, 8 },    // hueco en 7
            { 5, 7 }        // hueco en 8
    };

    public static List<String> vecinos(String tablero) {
        List<String> resultado = new ArrayList<>();
        int hueco = tablero.indexOf(' ');

        for (int destino : MOVIMIENTOS[hueco]) {
            char[] copia = tablero.toCharArray();
            copia[hueco] = copia[destino];
            copia[destino] = ' ';
            resultado.add(new String(copia));
        }
        return resultado;
    }
}
