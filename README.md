# Criptografía Bitcoin

Vas a construir la maquinaria criptográfica real que impulsa Bitcoin — desde cero, en Java puro, usando únicamente la biblioteca estándar. Al terminar la Sesión 2 entenderás cómo Bitcoin prueba identidad sin contraseñas, y por qué falsificar una transacción es matemáticamente imposible.

---

## Configuración

Clona o descomprime el proyecto. Tu directorio de trabajo debe verse así:

```
bitcoin-crypto/
├── Makefile
├── session1/        ← tu trabajo de la Sesión 1
├── session2/        ← tu trabajo de la Sesión 2
└── out/             ← archivos .class compilados (se crean automáticamente)
```

Necesitas Java 11 o superior. Verifica con:

```bash
java -version
javac -version
```

**No** necesitas IntelliJ, Maven, Gradle ni ningún IDE. Cada ejercicio se compila y ejecuta desde la terminal usando `make`.

---

## Cómo trabajar

Cada ejercicio es un único archivo `.java`. Ábrelo en cualquier editor de texto, encuentra los métodos marcados con `TODO`, impleméntalos y luego ejecuta:

```bash
make ex1   # compilar y ejecutar el Ejercicio 1
make ex2   # compilar y ejecutar el Ejercicio 2
# ... y así sucesivamente
```

Si la compilación falla, `javac` imprimirá el error con el número de línea. Corrígelo y ejecuta `make` de nuevo.

Si ves `UnsupportedOperationException: TODO: implement ...` en tiempo de ejecución, el código compiló bien — simplemente aún no has implementado ese método.

---

## Comandos de Make

```bash
make ex1        # Ejercicio 1 — HashUtil
make ex2        # Ejercicio 2 — ProofOfWork
make ex3        # Ejercicio 3 — Block
make ex4        # Ejercicio 4 — Blockchain
make ex5        # Ejercicio 5 — TransactionExercise

make s1         # compilar toda la Sesión 1 de una vez
make s2         # compilar la Sesión 2

make clean      # eliminar todo lo compilado y empezar de cero
make help       # mostrar esta lista
```

---

# Sesión 1 — Hashing, Minería y La Blockchain

---

## Ejercicio 1 — SHA-256 y el Efecto Avalancha
**Archivo:** `session1/HashUtil.java` | **Ejecutar:** `make ex1` | **Tiempo:** ~20 min

Una **función hash** toma cualquier entrada y produce una huella digital de longitud fija. SHA-256 siempre produce exactamente 64 caracteres hexadecimales, sin importar si la entrada es una palabra o un libro entero.

Bitcoin usa SHA-256 en todas partes: para identificar bloques, como núcleo del puzzle de minería, y para derivar direcciones de billetera.

**Implementa:** `sha256(String text)` usando `MessageDigest.getInstance("SHA-256")`.

`digest()` devuelve un `byte[]`. Convierte el array de bytes a hexadecimal con `HexFormat.of().formatHex(bytes)`.

**Una vez que funcione**, ejecútalo y observa:

```
sha256("Hello") = 185f8db32921bd46...
sha256("hello") = 2cf24dba5fb0a30e...
```

Diferir en un solo carácter (`H` vs `h`) produce un hash completamente distinto. Esto es el **efecto avalancha** — la propiedad que hace funcionar todo lo demás en esta sesión.

---

## Ejercicio 2 — Prueba de Trabajo (Minería)
**Archivo:** `session1/ProofOfWork.java` | **Ejecutar:** `make ex2` | **Tiempo:** ~30 min

"Minar" un bloque de Bitcoin significa resolver este puzzle:

> Encuentra un número (el **nonce**) tal que `sha256(blockData + nonce)` comience con N ceros.

La única estrategia es fuerza bruta — prueba nonce = 0, 1, 2, 3, ... hasta que uno funcione.

**Implementa:** `mine(String data, int difficulty)`.

Itera desde nonce = 0. En cada iteración, calcula `HashUtil.sha256(data + nonce)`. Si comienza con `"0".repeat(difficulty)`, terminaste — imprime el resultado y devuelve el nonce.

**Ejecútalo y completa esta tabla:**

| Dificultad | Nonce encontrado | Tiempo (ms) |
|---|---|---|
| 1 | | |
| 2 | | |
| 3 | | |
| 4 | | |
| 5 | | |

Cada cero adicional hace el puzzle aproximadamente **16 veces más difícil**. (Hay 16 dígitos hexadecimales posibles; solo uno es `0`.) Bitcoin usa alrededor de 18 ceros iniciales — eso es 16¹⁸ ≈ 4.7 × 10²¹ intentos esperados por bloque.

**La idea clave:** encontrar un nonce válido es costoso. Verificar uno toma un solo hash — cualquiera puede comprobar tu respuesta en un microsegundo. Esta asimetría es lo que lo convierte en una prueba genuina de trabajo.

---

## Ejercicio 3 — El Bloque
**Archivo:** `session1/Block.java` | **Ejecutar:** `make ex3` | **Tiempo:** ~15 min

Un bloque es un contenedor. Almacena:

| Campo | Propósito |
|---|---|
| `data` | las transacciones (contenido) |
| `previousHash` | hash del bloque anterior — esto crea la cadena |
| `timestamp` | cuándo se creó este bloque |
| `nonce` | la respuesta al puzzle de minería |
| `hash` | la huella digital propia del bloque, calculada a partir de todo lo anterior |

**Implementa:**

`calculateHash()` — concatena `data + previousHash + timestamp + nonce` en una sola cadena y devuelve `HashUtil.sha256()` de ella.

`mine(int difficulty)` — el mismo bucle del Ejercicio 2, pero actualiza `this.nonce` y `this.hash` en cada iteración hasta que `this.hash` comience con la cantidad correcta de ceros.

**Ejecútalo** y confirma que los hashes impresos genuinamente comienzan con `000`.

---

## Ejercicio 4 — La Blockchain y Detección de Manipulaciones
**Archivo:** `session1/Blockchain.java` | **Ejecutar:** `make ex4` | **Tiempo:** ~25 min

Una blockchain es una **lista enlazada de Bloques** donde el `previousHash` de cada bloque apunta al hash del bloque anterior:

```
[Genesis] ← [Block 1] ← [Block 2] ← [Block 3]
```

Si alguien manipula el Bloque 1:
- El hash del Bloque 1 cambia
- El `previousHash` del Bloque 2 ya no coincide con el nuevo hash del Bloque 1
- Todos los bloques posteriores al manipulado quedan rotos

**Implementa:**

`addBlock(String data)` — obtén el último bloque de la cadena, crea un nuevo `Block` con `previousHash = last.hash`, mínalo y agrégalo.

`isValid()` — itera desde el índice 1. Para cada bloque:
- Verifica `block.hash.equals(block.calculateHash())` — detecta manipulación de datos
- Verifica `block.previousHash.equals(chain.get(i-1).hash)` — detecta ruptura de la cadena
- Devuelve `false` inmediatamente si alguna verificación falla. Devuelve `true` si todas pasan.

**Ejecútalo.** Observa cómo imprime `Valid: true`. Luego el código corrompe los datos del Bloque 1 (sin volver a minarlo). Observa cómo imprime `Valid: false`.

**Reflexiona:** ¿por qué volver a minar el bloque manipulado *todavía* no resolvería el problema?

---

# Sesión 2 — Firmas Digitales

Asegúrate de que toda la Sesión 1 compile correctamente antes de comenzar:

```bash
make s1
```

---

## Ejercicio 5 — Transacciones y Firmas Digitales
**Archivo:** `session2/TransactionExercise.java` | **Ejecutar:** `make ex5` | **Tiempo:** ~60 min

En la Sesión 1 almacenamos cadenas de texto como `"Alice pays Bob 1 BTC"`. Pero la red no tiene forma de saber que Alice realmente escribió eso. Cualquiera podría hacerlo.

Bitcoin resuelve esto con **firmas digitales**. Alice tiene un par de claves:

- **Clave privada** — mantenida en secreto, nunca compartida (es su contraseña)
- **Clave pública** — difundida al mundo (es su dirección e identidad)

Para enviar Bitcoin, Alice:
1. Crea un mensaje de transacción
2. Lo firma con su clave privada → produce una firma (unos bytes)
3. Difunde: (mensaje, firma, clave pública)

Cualquiera puede verificar: `verify(mensaje, firma, clave pública de Alice)` → `true` o `false`

Lo crucial:
- Solo Alice puede producir una firma válida — ella es la única con su clave privada
- Cambiar incluso un solo byte en el mensaje invalida la firma
- Cualquiera puede verificar sin conocer la clave privada de Alice

Usamos **ECDSA** (Algoritmo de Firma Digital de Curva Elíptica) — el algoritmo exacto que usa Bitcoin. Está en `java.security`, no se necesitan bibliotecas externas.

---

### Parte A — `Wallet.generateKeys()`

Implementa esto primero y confirma que `wallet.getAddress()` imprime una cadena hexadecimal corta antes de continuar.

```java
KeyPairGenerator gen = KeyPairGenerator.getInstance("EC");
gen.initialize(256);
KeyPair pair = gen.generateKeyPair();
// store pair.getPrivate() and pair.getPublic()
```

---

### Parte B — `Transaction.sign()` y `Transaction.isValid()`

**sign():**
```java
Signature signer = Signature.getInstance("SHA256withECDSA");
signer.initSign(privateKey);
signer.update(getData().getBytes());
this.signature = signer.sign();
this.senderKey = publicKey;
```

**isValid():**
```java
if (signature == null || senderKey == null) return false;
Signature verifier = Signature.getInstance("SHA256withECDSA");
verifier.initVerify(senderKey);
verifier.update(getData().getBytes());
return verifier.verify(signature);
```

---

### Parte C — Ejecutar los tres experimentos

Después de implementar ambos métodos, ejecuta `make ex5` y asegúrate de entender por qué cada resultado es lo que es:

| Experimento | Resultado esperado | Por qué |
|---|---|---|
| Alice firma, Alice verifica | `true` | La firma coincide con la clave y los datos |
| Carol firma, se presenta como Alice | `false` | `senderKey` es de Carol — clave incorrecta para la dirección de Alice |
| Monto manipulado después de firmar | `false` | La firma fue calculada sobre `":0.01"`, no sobre `":999.0"` |

La firma no es simplemente "Alice aprobó algo". Es "Alice aprobó **exactamente este mensaje**". Cambiar un solo byte — en cualquier parte — la rompe.

---

## Referencia

### Compilar y ejecutar manualmente (sin make)

```bash
# Session 1
javac -d out/session1 session1/HashUtil.java
java  -cp out/session1 HashUtil

# Session 2 (TransactionExercise is standalone — no -cp needed)
javac -d out/session2 session2/TransactionExercise.java
java  -cp out/session2 TransactionExercise
```

> **Nota para Windows:** si no usas WSL, reemplaza `:` con `;` en las rutas de classpath.

### Si te quedas atascado

Cada ejercicio se apoya en el anterior. Si estás bloqueado y necesitas seguir avanzando, pídele al instructor la solución de ese método — puedes pegarla y continuar sin perder el hilo.

---

## Preguntas de Discusión

Reflexiona sobre estas mientras trabajas:

1. SHA-256 siempre produce 64 caracteres sin importar qué tan larga sea la entrada. ¿Por qué es importante la longitud fija para almacenar datos en una blockchain?
2. Cada cero adicional en la minería es ~16 veces más difícil. ¿Cuántos ceros iniciales harían que minar en tu máquina tardara aproximadamente un segundo?
3. ¿Por qué volver a minar un bloque manipulado no repara la blockchain?
4. Puedes verificar una firma digital sin conocer la clave privada. ¿Qué propiedad matemática hace eso posible?
5. Una dirección real de Bitcoin es un *hash* de la clave pública, no la clave pública en sí. ¿Por qué ese paso adicional?
