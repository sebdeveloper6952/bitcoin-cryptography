import java.security.NoSuchAlgorithmException;

/**
 * ============================================================
 * EJERCICIO 2 — Prueba de Trabajo (Minería)
 * SESIÓN 1 | ~30 minutos
 * ============================================================
 *
 * CONTEXTO
 * --------
 * "Minar" un bloque de Bitcoin significa resolver un puzzle:
 *
 *   Encuentra un número (llamado NONCE) tal que:
 *     SHA-256(datosDBloque + nonce)  comience con N ceros consecutivos
 *
 * Como las salidas de SHA-256 son impredecibles, la única estrategia
 * es la fuerza bruta — probar nonce = 0, 1, 2, 3, ... hasta encontrar uno.
 *
 * ¿POR QUÉ ESTO FUNCIONA COMO PRUEBA DE TRABAJO?
 *   - Encontrar un nonce válido requiere un enorme cómputo
 *   - Verificarlo toma un solo hash — trivialmente rápido
 *   - Cada cero adicional al inicio es ~16 veces más difícil en promedio
 *     (cada dígito hexadecimal tiene 16 valores posibles; solo 1 empieza con 0)
 *
 * Esta asimetría — difícil de encontrar, fácil de verificar — es exactamente
 * lo que lo convierte en una prueba útil de que se realizó trabajo real.
 *
 * La dificultad real de Bitcoin produce ~18 ceros iniciales, lo que
 * requiere quintillones de hashes por bloque.
 *
 * TAREAS
 * ------
 * 1. Implementa mine() para que itere nonces hasta encontrar uno cuyo
 *    hash comience con 'difficulty' ceros. Devuelve el nonce.
 *
 * 2. Ejecuta:  make ex2
 *    Completa la tabla en tus notas:
 *
 *    Dificultad | Nonces intentados (aprox.) | Tiempo
 *    -----------|----------------------------|--------
 *         1     |                            |
 *         2     |                            |
 *         3     |                            |
 *         4     |                            |
 *         5     |                            |
 *
 * 3. DISCUSIÓN: los intentos se multiplican por aproximadamente ___ con
 *    cada cero adicional. ¿Por qué? (Piensa en cuántos de los 16 valores
 *    hexadecimales son "0".)
 *
 * PRERREQUISITO: HashUtil.sha256() debe estar funcionando (Ejercicio 1).
 */
public class ProofOfWork {

    /**
     * Encuentra el primer nonce >= 0 tal que:
     *   HashUtil.sha256(data + nonce) comience con 'difficulty' ceros.
     *
     * Imprime el nonce ganador y su hash, luego devuelve el nonce.
     *
     * TODO: implementar este método.
     */
    public static long mine(String data, int difficulty) {
        String target = "0".repeat(difficulty);
        long nonce = 0;
        try {
            while (true) {
                String hash = HashUtil.sha256(data + nonce);
                if (hash.startsWith(target)) return nonce;
                nonce++;
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // ------------------------------------------------------------------ //

    public static void main(String[] args) {
        System.out.println("=== Ejercicio 2: Prueba de Trabajo ===\n");
        System.out.println("Datos del bloque: \"Hello, Blockchain!\"\n");

        String data = "Hello, Blockchain!";

        try {
            for (int difficulty = 1; difficulty <= 5; difficulty++) {
                System.out.printf("Dificultad %d (objetivo: \"%s...\")%n",
                        difficulty, "0".repeat(difficulty));

                long start = System.currentTimeMillis();
                long nonce = mine(data, difficulty);
                long elapsed = System.currentTimeMillis() - start;

                System.out.printf("  Nonce: %d%n", nonce);
                System.out.printf("  Hash:  %s%n", HashUtil.sha256(data + nonce));
                System.out.printf("  Tiempo: %d ms%n%n", elapsed);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
