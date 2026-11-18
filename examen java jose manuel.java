// Random genera valores aleatorios y Scanner lee la entrada de la consola.
import java.util.Random;
import java.util.Scanner;

// La meta exige derrotar a todos los enemigos, recoger la llave y abrir el cofre.
class TableroPuntos {
    // Dimensiones fijas de la matriz: 10 filas y 20 columnas.
    static final int FILAS = 10;
    static final int COLUMNAS = 20;

    // Cada símbolo representa un tipo de casilla del tablero.
    static final char PUNTO = '.';
    static final char JUGADOR = 'P';
    static final char PARED = '#';
    static final char SALIDA = 'E';
    static final char ENEMIGO = 'X';
    static final char MONEDA = 'M';
    static final char POCION = '+';
    static final char LLAVE = 'L';
    static final char COFRE = 'C';
    static final char TRAMPA = 'T';
    static final int PRECIO_COFRE = 3;

    // Límite de vidas: también es la cantidad con la que empieza el jugador.
    static final int VIDAS_MAXIMAS = 3;

    // El juego usa el mismo generador para la posición inicial y las jugadas enemigas.
    static final Random azar = new Random();
    // El primer índice es la fila y el segundo es la columna.
    static final char[][] tablero = new char[FILAS][COLUMNAS];
    // Estas variables guardan las coordenadas de P para mover al jugador sin buscarlo en la matriz.
    static int filaJugador;
    static int colJugador;
    // Estas variables guardan el estado de la partida entre un turno y el siguiente.
    static int vidas;
    static int monedas;
    static int monedasRecogidas;
    static int pasos;
    static int enemigosRestantes;
    // La llave se guarda como un booleano porque solo importa si el jugador ya la recogió.
    static boolean tieneLlave;
    static boolean cofreAbierto;

    // Guarda el récord mientras el programa sigue abierto.
    public static void main(String[] args) {
        mostrarIntroduccion();
        int record = 0;

        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                inicializarTablero();
                boolean partidaTerminada = jugarPartida(sc);
                record = Math.max(record, monedasRecogidas);
                System.out.println("Récord de monedas recogidas: " + record);

                if (!partidaTerminada) {
                    break;
                }
                System.out.println("¿Jugar otra partida? S/N");
                if (!sc.hasNextLine() || !sc.nextLine().trim().equalsIgnoreCase("s")) {
                    break;
                }
            }
        }
    }

    // Lee un movimiento por turno. Devuelve true si hay victoria o se agotan las vidas.
    static boolean jugarPartida(Scanner sc) {
        boolean victoria = false;
        while (!victoria && vidas > 0) {
            // Al inicio de cada turno se muestran el mapa, el marcador y los controles.
            mostrarTablero();
            System.out.printf("Vidas: %d/%d | Monedas: %d | Enemigos: %d%n",
                    vidas, VIDAS_MAXIMAS, monedas, enemigosRestantes);
            System.out.println("Llave: " + (tieneLlave ? "Sí" : "No"));
            System.out.println("Cofre abierto: " + (cofreAbierto ? "Sí" : "No"));
            System.out.println("X enemigo | M moneda | + poción | L llave | # pared | E puerta | C cofre | T trampa");
            System.out.println("Mover: W S A D | H para pedir una pista | Q para salir");
            System.out.print("Opción: ");

            if (!sc.hasNextLine()) {
                break;
            }
            String opcion = sc.nextLine().trim().toLowerCase();
            if (opcion.equals("q")) {
                break;
            }

            // W y S cambian la fila. A y D cambian la columna.
            switch (opcion) {
                case "w": victoria = mover(-1, 0, sc); break;
                case "s": victoria = mover(1, 0, sc); break;
                case "a": victoria = mover(0, -1, sc); break;
                case "d": victoria = mover(0, 1, sc); break;
                case "h": mostrarPista(); break;
                default: System.out.println("Opción no válida");
            }
        }
        if (victoria) {
            mostrarTablero();
            System.out.println("¡Has escapado de la mazmorra!");
            System.out.println("Pasos: " + pasos);
        } else if (vidas == 0) {
            System.out.println("Te has quedado sin vidas.");
        }
        System.out.println("Fin de la partida. Monedas recogidas: " + monedasRecogidas);
        return victoria || vidas == 0;
    }

    // Indica una condición que todavía falta para llegar a la meta.
    static void mostrarPista() {
        if (enemigosRestantes > 0) {
            System.out.println("Pista: te quedan " + enemigosRestantes + " enemigos por derrotar.");
        } else if (!tieneLlave) {
            System.out.println("Pista: busca la llave (L) para abrir la puerta.");
        } else if (!cofreAbierto) {
            if (monedas < PRECIO_COFRE) {
                System.out.println("Pista: te faltan " + (PRECIO_COFRE - monedas) + " monedas para el cofre.");
            } else {
                System.out.println("Pista: ya puedes abrir el cofre (C).");
            }
        } else {
            System.out.println("Pista: ya puedes entrar por la puerta (E).");
        }
    }

    // Imprime las reglas una sola vez, antes de mostrar el primer tablero.
    static void mostrarIntroduccion() {
        System.out.println("=== Escapa de la mazmorra ===");
        System.out.println("¡Bienvenido a la mazmorra, profe Víctor!");
        System.out.println("Para escapar, derrota a los cinco enemigos en piedra, papel o tijera,");
        System.out.println("recoge la llave (L) y gasta tres monedas (M) para abrir el cofre (C).");
        System.out.println("Cuando hayas cumplido los tres objetivos, entra por la puerta (E).");
        System.out.println();
        System.out.println("Eres P. Muévete con W, A, S y D. Pulsa H para una pista o Q para salir.");
        System.out.println("Las paredes (#) bloquean el paso.");
        System.out.println("Enfréntate a los enemigos (X) en duelos de piedra, papel o tijera.");
        System.out.println("Empiezas con 3 vidas. Cada derrota te quita una y cada poción (+) recupera una.");
        System.out.println("Recoge " + PRECIO_COFRE + " monedas (M) para abrir el cofre (C).");
        System.out.println("Al abrirlo, recuperas una vida si te falta alguna.");
        System.out.println("Las trampas (T) te quitan una vida y se desactivan al pisarlas.");
    }

    // Construye el mapa y prepara los datos de una partida nueva.
    static void inicializarTablero() {
        // El bucle exterior recorre las filas y el interior recorre sus columnas.
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                tablero[i][j] = PUNTO;
            }
        }
        // Dos filas de paredes dejan huecos por los que se puede pasar.
        for (int col = 6; col <= 14; col++) {
            tablero[4][col] = PARED;
        }
        tablero[4][12] = PUNTO;
        for (int col = 3; col <= 10; col++) {
            tablero[6][col] = PARED;
        }
        tablero[6][8] = PUNTO;
        tablero[5][12] = SALIDA;

        // Coloca los cinco enemigos en casillas distintas.
        tablero[2][3] = ENEMIGO;
        tablero[5][9] = ENEMIGO;
        tablero[7][15] = ENEMIGO;
        tablero[1][15] = ENEMIGO;
        tablero[8][5] = ENEMIGO;

        // Reparte las ocho monedas por el mapa.
        tablero[1][9] = MONEDA;
        tablero[5][11] = MONEDA;
        tablero[8][17] = MONEDA;
        tablero[0][2] = MONEDA;
        tablero[2][16] = MONEDA;
        tablero[4][4] = MONEDA;
        tablero[6][13] = MONEDA;
        tablero[9][6] = MONEDA;

        // Coloca la llave y las dos pociones en sus casillas.
        tablero[8][2] = LLAVE;
        tablero[3][5] = POCION;
        tablero[7][11] = POCION;
        tablero[2][6] = COFRE;
        tablero[3][13] = TRAMPA;
        tablero[7][9] = TRAMPA;
        tablero[1][4] = TRAMPA;
        tablero[5][16] = TRAMPA;
        tablero[9][13] = TRAMPA;
        // Estado inicial de la partida.
        enemigosRestantes = 5;
        vidas = VIDAS_MAXIMAS;
        monedas = 0;
        monedasRecogidas = 0;
        pasos = 0;
        tieneLlave = false;
        cofreAbierto = false;

        // nextInt genera un índice desde 0 hasta el límite excluido.
        // Si la casilla está ocupada, el bucle elige otras coordenadas.
        do {
            filaJugador = azar.nextInt(FILAS);
            colJugador = azar.nextInt(COLUMNAS);
        } while (tablero[filaJugador][colJugador] != PUNTO);
        tablero[filaJugador][colJugador] = JUGADOR;
    }

    // Imprime la matriz fila por fila, con un espacio entre casillas.
    static void mostrarTablero() {
        System.out.println();
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                System.out.print(tablero[i][j] + " ");
            }
            // Imprime un salto de línea al terminar cada fila.
            System.out.println();
        }
        System.out.println();
    }

    // Intenta mover a P una casilla. Devuelve true solamente si entra por la puerta.
    static boolean mover(int deltaFila, int deltaCol, Scanner sc) {
        // Los incrementos indican cuánto cambia cada coordenada.
        // Calcula el destino antes de modificar la posición actual.
        int nuevaFila = filaJugador + deltaFila;
        int nuevaCol = colJugador + deltaCol;

        // Basta con que falle uno de los cuatro límites para cancelar el movimiento.
        // Esta comprobación evita acceder a un índice inválido de la matriz.
        if (nuevaFila < 0 || nuevaFila >= FILAS
                || nuevaCol < 0 || nuevaCol >= COLUMNAS) {
            System.out.println("No puedes salir del tablero");
            return false;
        }

        // Guarda el símbolo de destino antes de reemplazarlo por P.
        char destino = tablero[nuevaFila][nuevaCol];
        // Una pared bloquea el movimiento y P se queda donde estaba.
        if (destino == PARED) {
            System.out.println("Hay una pared");
            return false;
        }

        // Cada símbolo tiene su regla. Si el destino es un punto, el jugador puede avanzar directamente.
        switch (destino) {
            case SALIDA:
                // La puerta exige la llave, todos los enemigos vencidos y el cofre abierto.
                if (!tieneLlave) {
                    System.out.println("Necesitas la llave para abrir la puerta.");
                    return false;
                }
                if (enemigosRestantes > 0) {
                    System.out.println("Derrota a todos los enemigos para abrir la salida.");
                    return false;
                }
                if (!cofreAbierto) {
                    System.out.println("Abre el cofre con " + PRECIO_COFRE + " monedas antes de llegar a la meta.");
                    return false;
                }
                break;
            case LLAVE:
                // Guarda la llave en el estado del jugador aunque su símbolo desaparezca.
                tieneLlave = true;
                System.out.println("Has recogido la llave.");
                break;
            case ENEMIGO:
                // Si el jugador pierde o se retira, P y el enemigo conservan sus casillas.
                if (!ganarDuelo(sc)) {
                    return false;
                }
                // Reduce el contador de enemigos cuando el jugador gana el duelo.
                enemigosRestantes--;
                break;
            case MONEDA:
                // Cada moneda recogida suma un punto al marcador.
                monedas++;
                monedasRecogidas++;
                System.out.println("Has recogido una moneda.");
                break;
            case COFRE:
                // Abrir el cofre gasta las monedas y guarda el requisito de la meta.
                if (monedas < PRECIO_COFRE) {
                    System.out.println("Para abrir el cofre necesitas " + PRECIO_COFRE + " monedas.");
                    return false;
                }
                monedas -= PRECIO_COFRE;
                cofreAbierto = true;
                System.out.println("Has abierto el cofre.");
                if (vidas < VIDAS_MAXIMAS) {
                    vidas++;
                    System.out.println("Has recuperado una vida.");
                }
                break;
            case TRAMPA:
                // La trampa desaparece cuando P ocupa su casilla.
                vidas--;
                System.out.println("Has pisado una trampa y perdido una vida.");
                break;
            case POCION:
                // La poción añade una vida si el jugador tiene menos vidas que el máximo.
                if (vidas < VIDAS_MAXIMAS) {
                    vidas++;
                    System.out.println("Has recuperado una vida.");
                } else {
                    System.out.println("Has recogido una poción con la vida al máximo.");
                }
                break;
        }

        // Tras validar el movimiento, vacía la casilla anterior y coloca a P en la nueva.
        // Los objetos recogidos y los enemigos vencidos desaparecen al ocupar su casilla.
        tablero[filaJugador][colJugador] = PUNTO;
        filaJugador = nuevaFila;
        colJugador = nuevaCol;
        tablero[filaJugador][colJugador] = JUGADOR;

        // Cuenta solo los movimientos que cambian la posición del jugador.
        pasos++;
        // main usa este resultado para saber si el movimiento terminó la partida.
        return destino == SALIDA;
    }

    // Devuelve true si el jugador gana el duelo y puede ocupar la casilla del enemigo.
    static boolean ganarDuelo(Scanner sc) {
        String[] jugadas = {"", "Piedra", "Papel", "Tijera"};

        // Una entrada inválida o un empate repiten la ronda.
        while (true) {
            System.out.println("Enemigo: 1 Piedra | 2 Papel | 3 Tijera | Q volver");
            if (!sc.hasNextLine()) {
                return false;
            }

            // Q cancela el duelo y devuelve el control al tablero.
            String opcion = sc.nextLine().trim();
            if (opcion.equalsIgnoreCase("q")) {
                return false;
            }
            // Valida el texto antes de convertirlo a un número.
            if (!opcion.matches("[123]")) {
                System.out.println("Elige 1, 2 o 3.");
                continue;
            }

            int jugador = Integer.parseInt(opcion);
            int enemigo = azar.nextInt(3) + 1;
            System.out.println("Tú: " + jugadas[jugador] + " | Enemigo: " + jugadas[enemigo]);

            // Si ambas jugadas coinciden, el bucle repite el duelo.
            if (jugador == enemigo) {
                System.out.println("Empate. Volved a jugar.");
                continue;
            }
            // Piedra vence a tijera, papel a piedra y tijera a papel.
            if ((jugador == 1 && enemigo == 3)
                    || (jugador == 2 && enemigo == 1)
                    || (jugador == 3 && enemigo == 2)) {
                System.out.println("¡Has derrotado al enemigo!");
                return true;
            }

            // Si no hubo empate ni victoria, el jugador pierde una vida y el duelo termina.
            vidas--;
            System.out.println("Has perdido una vida.");
            return false;
        }
    }
}
