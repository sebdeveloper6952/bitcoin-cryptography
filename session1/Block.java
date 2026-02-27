import java.security.NoSuchAlgorithmException;

/**
 * ============================================================
 * EJERCICIO 3 — El Bloque
 * SESIÓN 1 | ~15 minutos
 * ============================================================
 *
 * CONTEXTO
 * --------
 * Un bloque de Bitcoin es un contenedor que almacena:
 *   - data        : las transacciones (contenido del bloque)
 *   - previousHash: hash del bloque anterior — esto crea la cadena
 *   - timestamp   : cuándo fue creado este bloque
 *   - nonce       : la respuesta al puzzle de minería
 *   - hash        : la huella digital propia del bloque (calculada a partir de todo lo anterior)
 *
 * El campo previousHash es lo que enlaza los bloques entre sí. Si
 * cambias cualquier cosa en el bloque #3, su hash cambia — lo que
 * invalida el previousHash del bloque #4 — que a su vez invalida el
 * del bloque #5, y así sucesivamente. Esta reacción en cadena es lo
 * que hace que el registro sea a prueba de manipulaciones.
 *
 * TAREAS
 * ------
 * 1. Implementa calculateHash() — concatena todos los campos y devuelve
 *    su hash SHA-256 mediante HashUtil.sha256().
 *
 * 2. Implementa mine() — incrementa el nonce y recalcula el hash
 *    hasta que comience con 'difficulty' caracteres cero.
 *
 * 3. Ejecuta:  make ex3
 *    Verifica que los hashes impresos realmente comiencen con "000".
 *
 * PRERREQUISITO: HashUtil.sha256() debe estar funcionando (Ejercicio 1).
 */
public class Block {

    public String data;                 // transacciones / contenido (mutable para la demo de manipulación)
    public final String previousHash;   // enlaza este bloque con el anterior
    public final long   timestamp;      // momento de creación del bloque
    public int          nonce;          // respuesta a la minería — comienza en 0
    public String       hash;           // huella digital propia de este bloque

    public Block(String data, String previousHash) {
        this.data         = data;
        this.previousHash = previousHash;
        this.timestamp    = System.currentTimeMillis();
        this.nonce        = 0;
        this.hash         = calculateHash();
    }

    /**
     * Calcula el hash SHA-256 del contenido de este bloque.
     *
     * Concatena: data + previousHash + timestamp + nonce
     * Devuelve: HashUtil.sha256() de esa cadena combinada.
     *
     * TODO: implementar este método.
     */
    public String calculateHash() {
        try {
            return HashUtil.sha256(data + previousHash + timestamp + nonce);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Mina este bloque con la dificultad dada.
     *
     * Incrementa el nonce y recalcula el hash hasta que
     * this.hash comience con 'difficulty' caracteres cero.
     * Actualiza tanto this.nonce como this.hash en cada intento.
     *
     * TODO: implementar este método.
     */
    public void mine(int difficulty) {
       String target = "0".repeat(difficulty);
       while (!hash.startsWith(target)) {
           nonce++;
           hash = calculateHash();
       }
    }

    @Override
    public String toString() {
        return String.format(
            "Block { data='%s', nonce=%d, hash=%s..., prev=%s... }",
            data, nonce,
            hash.substring(0, 12),
            previousHash.length() > 12 ? previousHash.substring(0, 12) : previousHash
        );
    }

    // ------------------------------------------------------------------ //

    public static void main(String[] args) {
        System.out.println("=== Ejercicio 3: El Bloque ===\n");

        int difficulty = 5;
        System.out.println("Minando dos bloques con dificultad " + difficulty + "...\n");

        Block genesis = new Block("Genesis — primer bloque", "0000000000000000");
        genesis.mine(difficulty);
        System.out.println("Bloque 1: " + genesis);
        System.out.println("  Hash completo: " + genesis.hash);

        Block second = new Block("Alice paga a Bob 1.0 BTC", genesis.hash);
        second.mine(difficulty);
        System.out.println("\nBloque 2: " + second);
        System.out.println("  Hash completo: " + second.hash);

        System.out.println("\nEl previousHash del Bloque 2 coincide con el hash del Bloque 1: "
                + second.previousHash.equals(genesis.hash));

        Block third = new Block("Bob paga 0.75 BTC a Carol", second.hash);
        third.mine(difficulty);
        System.out.println("\nBloque 3: " + third);
        System.out.println("  Hash completo: " + third.hash);
    }
}
