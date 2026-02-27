import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 * EJERCICIO 4 — La Blockchain y Detección de Manipulaciones
 * SESIÓN 1 | ~25 minutos
 * ============================================================
 *
 * CONTEXTO
 * --------
 * Una blockchain es una lista enlazada de Bloques donde el
 * previousHash de cada bloque apunta al hash del bloque anterior:
 *
 *   [Genesis] ← [Bloque 1] ← [Bloque 2] ← [Bloque 3]
 *
 * Para manipular el Bloque 1, un atacante necesitaría:
 *   1. Volver a minar el Bloque 1 (costoso)
 *   2. Volver a minar el Bloque 2 (su previousHash ahora es incorrecto)
 *   3. Volver a minar el Bloque 3 ... y todos los bloques siguientes
 *   4. Hacer todo esto más rápido que el resto de la red
 *      añadiendo nuevos bloques honestos — prácticamente imposible.
 *
 * TAREAS
 * ------
 * 1. Implementa addBlock() — crea un nuevo Bloque cuyo previousHash
 *    sea el hash del último bloque en la cadena, mínalo y agrégalo.
 *
 * 2. Implementa isValid() — recorre la cadena desde el índice 1 y verifica:
 *    a. block.hash es igual a un block.calculateHash() recién calculado
 *       (detecta manipulación de datos)
 *    b. block.previousHash es igual al hash del bloque anterior
 *       (detecta ruptura de la cadena)
 *    Devuelve false en cuanto alguna verificación falle.
 *
 * 3. Ejecuta:  make ex4
 *    Observa cómo isValid() devuelve true, y luego false tras el ataque.
 *
 * DISCUSIÓN
 * ---------
 * Después de ver que isValid() detecta el ataque, pregúntate:
 * ¿Por qué volver a minar el Bloque 1 aún no resolvería el problema?
 * (Piensa en qué contiene el previousHash del Bloque 2.)
 *
 * PRERREQUISITO: Block debe estar funcionando (Ejercicio 3).
 */
public class Blockchain {

    private final List<Block> chain      = new ArrayList<>();
    private final int         difficulty = 3;

    public Blockchain() {
        Block genesis = new Block("Bloque Génesis", "0000000000000000");
        genesis.mine(difficulty);
        chain.add(genesis);
        System.out.println("Bloque génesis minado: " + genesis.hash.substring(0, 16) + "...");
    }

    /**
     * Mina un nuevo bloque con los 'data' dados y lo agrega a la cadena.
     *
     * El previousHash del nuevo bloque debe ser igual a chain.get(último).hash.
     *
     * TODO: implementar este método.
     */
    public void addBlock(String data) {
        Block last = chain.getLast();
        Block newBlock = new Block(data, last.hash);
        newBlock.mine(difficulty);
        chain.addLast(newBlock);
    }

    /**
     * Devuelve true solo si cada bloque de la cadena es internamente
     * consistente y está correctamente enlazado con su predecesor.
     *
     * Itera desde i = 1 (el génesis no tiene nada con qué compararse):
     *   a. si block.hash != block.calculateHash()     → devuelve false
     *   b. si block.previousHash != chain[i-1].hash  → devuelve false
     *
     * TODO: implementar este método.
     */
    public boolean isValid() {
        for (int i = 1; i < chain.size(); i++) {
            Block cur = chain.get(i);
            Block prev = chain.get(i-1);
            if (!cur.hash.equals(cur.calculateHash()))
                return false;
            if (!cur.previousHash.equals(prev.hash))
                return false;
        }

        return true;
    }

    public List<Block> getChain() { return chain; }

    public void printChain() {
        System.out.println("\n--- Estado de la blockchain ---");
        for (int i = 0; i < chain.size(); i++) {
            Block b = chain.get(i);
            System.out.printf("Bloque %d | %-28s | %s...%n",
                i, "\"" + b.data + "\"", b.hash.substring(0, 16));
        }
        System.out.println("Válida: " + isValid());
        System.out.println("-------------------------------\n");
    }

    // ------------------------------------------------------------------ //

    public static void main(String[] args) {
        System.out.println("=== Ejercicio 4: Blockchain y Detección de Manipulaciones ===\n");

        Blockchain bc = new Blockchain();
        bc.addBlock("Alice paga a Bob 1.5 BTC");
        bc.addBlock("Bob paga a Carol 0.5 BTC");
        bc.addBlock("Carol paga a Dave 0.25 BTC");

        bc.printChain();   // Válida: true

        System.out.println("ATAQUE: modificando los datos del Bloque 1...");
        bc.getChain().get(1).data = "Alice paga a Bob 999.0 BTC";

        bc.printChain();   // Válida: false
    }
}
