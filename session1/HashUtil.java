import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * ============================================================
 * EJERCICIO 1 — Hashing SHA-256 y el Efecto Avalancha
 * SESIÓN 1 | ~20 minutos
 * ============================================================
 *
 * CONTEXTO
 * --------
 * Una función hash toma cualquier entrada (una palabra, una oración,
 * un libro entero) y produce una "huella digital" de longitud fija —
 * 64 caracteres hexadecimales en el caso de SHA-256.
 *
 * Bitcoin usa SHA-256 en todas partes:
 *   - para identificar cada bloque de transacciones
 *   - como núcleo del puzzle de minería
 *   - para derivar direcciones de billetera a partir de claves públicas
 *
 * La propiedad clave que hace útil a SHA-256 es el EFECTO AVALANCHA:
 * cambiar incluso un solo carácter en la entrada cambia completamente
 * la salida — no existe ninguna relación gradual ni predecible entre
 * la entrada y la salida.
 *
 * TAREAS
 * ------
 * 1. Implementa sha256() para que devuelva el hash SHA-256 del
 *    texto recibido, codificado como cadena hexadecimal.
 *
 * 2. Compila y ejecuta:
 *      make ex1
 *    Observa que "Hello" y "hello" producen hashes completamente
 *    distintos a pesar de diferir en un solo carácter.
 *
 * 3. Hashea la misma cadena dos veces. ¿Qué observas?
 *    (Esta propiedad se llama DETERMINISMO.)
 *
 * 4. ¿Puedes encontrar dos entradas distintas que produzcan el mismo hash?
 *    (Spoiler: no — esto se llama RESISTENCIA A COLISIONES.)
 *
 * PISTA
 * -----
 * Usa MessageDigest.getInstance("SHA-256").
 * digest() devuelve un byte[]. Convierte cada byte a dos dígitos
 * hexadecimales con HexFormat.of().formatHex(bytes).
 */
public class HashUtil {

    /**
     * Devuelve el hash SHA-256 del texto dado como cadena hexadecimal de 64 caracteres.
     *
     * TODO: implementar este método.
     */
    public static String sha256(String text) throws NoSuchAlgorithmException {
        byte[] bytes = MessageDigest.getInstance("SHA-256").digest(text.getBytes());

        return HexFormat.of().formatHex(bytes);
    }

    // ------------------------------------------------------------------ //

    public static void main(String[] args) {
        System.out.println("=== Ejercicio 1: SHA-256 y el Efecto Avalancha ===\n");

        try {
            String a = sha256("Hello");
            String b = sha256("hello");  // solo difiere 'H' vs 'h'

            System.out.println("sha256(\"Hello\") = " + a);
            System.out.println("sha256(\"hello\") = " + b);
            System.out.println();
            System.out.println("¿Iguales?       " + a.equals(b));               // false
            System.out.println("¿Determinista?  " + a.equals(sha256("Hello"))); // true

            System.out.println("\n--- Prueba tus propias entradas ---");
            System.out.println("sha256(\"Bitcoin\") = " + sha256("Bitcoin"));
            System.out.println("sha256(\"bitcoin\") = " + sha256("bitcoin"));
            System.out.println("sha256(\"\")        = " + sha256(""));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
