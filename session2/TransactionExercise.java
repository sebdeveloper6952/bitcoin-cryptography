import java.security.*;
import java.util.HexFormat;

/**
 * ============================================================
 * EJERCICIO 5 — Transacciones y Firmas Digitales
 * SESIÓN 2 | ~60 minutos
 * ============================================================
 *
 * CONTEXTO
 * --------
 * En la Sesión 1 almacenamos cadenas de texto como "Alice paga a Bob 1 BTC".
 * Pero ¿cómo sabe la red que Alice realmente autorizó esto?
 * Cualquiera podría escribir esa cadena.
 *
 * Bitcoin resuelve esto con FIRMAS DIGITALES:
 *
 *   Alice tiene un PAR DE CLAVES:
 *     • Clave privada — conocida solo por Alice (nunca se comparte)
 *     • Clave pública — difundida al mundo (su identidad/dirección)
 *
 *   Para enviar monedas, Alice:
 *     1. Crea un mensaje de transacción: "Alice → Bob, 1 BTC"
 *     2. Lo firma con su clave privada  →  produce una firma
 *     3. Difunde: (mensaje, firma, clave pública)
 *
 *   Cualquiera puede verificar:
 *     verify(mensaje, firma, clave pública de Alice)  →  true / false
 *
 * PROPIEDADES CLAVE:
 *   • Solo quien tiene la clave privada puede producir una firma válida
 *   • Cualquiera puede verificarla sin conocer la clave privada
 *   • La firma está ligada al mensaje exacto — cambiar incluso
 *     un byte hace que la verificación falle
 *
 * Usamos ECDSA (Algoritmo de Firma Digital de Curva Elíptica) —
 * el mismo algoritmo que usa Bitcoin. Está en java.security;
 * no se necesitan bibliotecas externas.
 *
 * TAREAS
 * ------
 * PARTE A — Wallet
 * 1. Implementa Wallet.generateKeys() usando KeyPairGenerator con
 *    algoritmo "EC" y tamaño de clave 256.
 *
 * PARTE B — Transaction
 * 2. Implementa Transaction.sign() usando Signature con
 *    algoritmo "SHA256withECDSA".
 *
 * 3. Implementa Transaction.isValid() para verificar la firma
 *    contra la cadena de datos de esta transacción.
 *
 * PARTE C — Ejecuta los experimentos
 *    make ex5
 *    Observa la salida de cada experimento y comprende por qué.
 */
public class TransactionExercise {

    // ================================================================== //
    //  PARTE A — Wallet (Billetera)                                       //
    // ================================================================== //

    public static class Wallet {
        public PrivateKey privateKey;
        public PublicKey  publicKey;

        /**
         * Genera un par de claves EC nuevo y almacena ambas claves en este objeto.
         *
         * Pasos:
         *   KeyPairGenerator gen = KeyPairGenerator.getInstance("EC");
         *   gen.initialize(256);
         *   KeyPair pair = gen.generateKeyPair();
         *   ... almacenar la clave privada y la clave pública
         *
         * TODO: implementar este método.
         */
        public void generateKeys() throws Exception {
            throw new UnsupportedOperationException("TODO: implementar generateKeys()");
        }

        /** Hex abreviado de la clave pública — usado como dirección legible. */
        public String getAddress() {
            return bytesToHex(publicKey.getEncoded()).substring(0, 16) + "...";
        }
    }

    // ================================================================== //
    //  PARTE B — Transaction (Transacción)                                //
    // ================================================================== //

    public static class Transaction {
        public final String    sender;      // remitente
        public final String    recipient;   // destinatario
        public final double    amount;      // monto
        public       byte[]    signature;   // llenado por sign()
        public       PublicKey senderKey;   // llenado por sign(), usado en isValid()

        public Transaction(String sender, String recipient, double amount) {
            this.sender    = sender;
            this.recipient = recipient;
            this.amount    = amount;
        }

        /** Representación canónica en cadena de los datos de esta transacción. */
        public String getData() {
            return sender + "->" + recipient + ":" + amount;
        }

        /**
         * Firma esta transacción con la clave privada dada.
         * Almacena los bytes de la firma en this.signature.
         * Almacena la clave pública en this.senderKey (necesaria para la verificación).
         *
         * Pasos:
         *   Signature signer = Signature.getInstance("SHA256withECDSA");
         *   signer.initSign(privateKey);
         *   signer.update(getData().getBytes());
         *   this.signature = signer.sign();
         *   this.senderKey = publicKey;
         *
         * TODO: implementar este método.
         */
        public void sign(PrivateKey privateKey, PublicKey publicKey) throws Exception {
            throw new UnsupportedOperationException("TODO: implementar sign()");
        }

        /**
         * Devuelve true si this.signature es una firma ECDSA válida de
         * getData() bajo this.senderKey.
         * Devuelve false si no está firmada (signature o senderKey es null) o es inválida.
         *
         * Pasos:
         *   Signature verifier = Signature.getInstance("SHA256withECDSA");
         *   verifier.initVerify(senderKey);
         *   verifier.update(getData().getBytes());
         *   return verifier.verify(signature);
         *
         * TODO: implementar este método.
         */
        public boolean isValid() throws Exception {
            throw new UnsupportedOperationException("TODO: implementar isValid()");
        }

        @Override
        public String toString() {
            return String.format("Tx{ %s -> %s : %.2f BTC | firmada: %s }",
                sender.substring(0, 8) + "...",
                recipient.substring(0, 8) + "...",
                amount,
                signature != null ? "SÍ" : "NO");
        }
    }

    // ================================================================== //
    //  Main                                                               //
    // ================================================================== //

    public static void main(String[] args) throws Exception {
        System.out.println("=== Ejercicio 5: Firmas Digitales ===\n");

        Wallet alice = new Wallet();  alice.generateKeys();
        Wallet bob   = new Wallet();  bob.generateKeys();
        Wallet carol = new Wallet();  carol.generateKeys();

        System.out.println("Alice: " + alice.getAddress());
        System.out.println("Bob:   " + bob.getAddress());
        System.out.println("Carol: " + carol.getAddress());
        System.out.println();

        // --- Experimento 1: Transacción válida ---
        System.out.println("--- Experimento 1: Alice paga a Bob (legítimo) ---");
        Transaction tx1 = new Transaction(alice.getAddress(), bob.getAddress(), 1.0);
        tx1.sign(alice.privateKey, alice.publicKey);
        System.out.println(tx1);
        System.out.println("¿Válida? " + tx1.isValid());      // esperado: true
        System.out.println();

        // --- Experimento 2: Firmante incorrecto ---
        System.out.println("--- Experimento 2: Carol se hace pasar por Alice ---");
        Transaction tx2 = new Transaction(alice.getAddress(), bob.getAddress(), 1.0);
        tx2.sign(carol.privateKey, carol.publicKey);  // firmada por Carol, no por Alice
        System.out.println(tx2);
        System.out.println("¿Válida? " + tx2.isValid());      // esperado: false
        System.out.println();

        // --- Experimento 3: Monto manipulado ---
        System.out.println("--- Experimento 3: Monto manipulado después de firmar ---");
        Transaction tx3 = new Transaction(alice.getAddress(), bob.getAddress(), 0.01);
        tx3.sign(alice.privateKey, alice.publicKey);
        System.out.println("Antes de manipular — ¿Válida? " + tx3.isValid());  // true

        // Simula un atacante reutilizando la firma de Alice en una transacción de mayor valor
        Transaction forged = new Transaction(alice.getAddress(), bob.getAddress(), 999.0);
        forged.signature = tx3.signature;   // la firma real de Alice...
        forged.senderKey = tx3.senderKey;   // ...pero sobre datos distintos
        System.out.println("Después de manipular — ¿Válida? " + forged.isValid()); // false
    }

    // ------------------------------------------------------------------ //

    static String bytesToHex(byte[] bytes) {
        return HexFormat.of().formatHex(bytes);
    }
}
