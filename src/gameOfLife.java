package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

// Clase principal extendida de JFrame
public class GameOfLife extends JFrame {
    private int numCellsY;
    private int numCellsX;
    private int buttonSize;
    private int millisGen;
    private int g;
    private String populationString;

    private JButton[][] boardButs;
    private boolean[][] boardState;
    private volatile boolean running; // volatile garantiza el buen funcionamiento de la variable en cuanto a los hilos

    // Constructor de la clase
    public GameOfLife(String[] args) {

        validateArgs(args);

        boardButs = new JButton[numCellsY][numCellsX];
        boardState = new boolean[numCellsY][numCellsX];
        running = false;

        interfaceInit();
        boardInit();

        setTitle("Juego de la Vida Jala");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Metodo para validar los argumentos de la linea de comandos y asignar variables
    private void validateArgs(String[] args) {
        for (String arg : args) {
            String[] parts = arg.split("=");

            if (parts.length != 2) {
                System.out.println("Formato de argumento no valido: " + arg);
                System.exit(1);
            }

            String key = parts[0];
            String value = parts[1];

            switch (key) {
                case "w": // width ancho
                    try {
                        int width = Integer.parseInt(value);
                        if (width != 10 && width != 20 && width != 40 && width != 80) {
                            System.out.println("Width (w) debe ser uno de los siguientes valores: 10, 20, 40 o 80.");
                            System.exit(1);
                        }
                        numCellsX = width;
                        System.out.println("Width = [" + numCellsX + "]");
                    } catch (NumberFormatException e) {
                        System.out.println("Width (w) debe ser un numero entero.");
                        System.exit(1);
                    }
                    break;
                case "h": // height alto
                    try {
                        int height = Integer.parseInt(value);
                        if (height != 10 && height != 20 && height != 40) {
                            System.out.println("Height (h) debe ser uno de los siguientes valores: 10, 20 o 40.");
                            System.exit(1);
                        }
                        numCellsY = height;
                        System.out.println("Height = [" + numCellsY + "]");
                    } catch (NumberFormatException e) {
                        System.out.println("Height (h) debe ser un numero entero.");
                        System.exit(1);
                    }
                    break;
                case "g": // generaciones
                    try {
                        int generations = Integer.parseInt(value);
                        if (generations < 0 && generations != 0) {
                            System.out.println("Generations (g) debe ser un numero no negativo o 0 para infinito.");
                            System.exit(1);
                        }
                        g = generations;
                        System.out.println("Generations = [" + g + "]");
                    } catch (NumberFormatException e) {
                        System.out.println("Generations (g) debe ser un numero entero.");
                        System.exit(1);
                    }
                    break;
                case "s": // speed velocidad
                    try {
                        int speed = Integer.parseInt(value);
                        if (speed < 250 || speed > 1000) {
                            System.out.println("Speed (s) debe ser un numero entre 250 y 1000 (inclusive).");
                            System.exit(1);
                        }
                        millisGen = speed;
                        System.out.println("Speed = [" + millisGen + "]");
                    } catch (NumberFormatException e) {
                        System.out.println("Speed (s) debe ser un numero entero.");
                        System.exit(1);
                    }
                    break;
                case "t": // t tamaño del boton
                    try {
                        buttonSize = Integer.parseInt(value);
                        System.out.println("Button size = [" + buttonSize + "]");
                    } catch (NumberFormatException e) {
                        System.out.println("Button size (t) debe ser un numero entero.");
                        System.exit(1);
                    }
                    break;
                case "p": // population poblacion
                    populationString = value; // guarda el valor en variable para despues procesarlo en un metodo
                    System.out.println("Population string = [" + populationString + "]");
                    break;
            }
        }
    }

    // Metodo para inicializar la interfaz grafica
    private void interfaceInit() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());

        JPanel panelTablero = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(1, 1, 1, 1);

        for (int i = 0; i < numCellsY; i++) {
            for (int j = 0; j < numCellsX; j++) {
                JButton boton = new JButton();
                boton.addActionListener(new BotonClickListener(i, j));
                boton.setPreferredSize(new Dimension(buttonSize, buttonSize));
                boton.setMaximumSize(new Dimension(buttonSize, buttonSize));
                boton.setMinimumSize(new Dimension(buttonSize, buttonSize));
                panelTablero.add(boton, gbc);
                boardButs[i][j] = boton;
                gbc.gridx++;
            }
            gbc.gridx = 0;
            gbc.gridy++;
        }

        panelPrincipal.add(panelTablero, BorderLayout.CENTER);
        //se agregan los botones para iniciar y detener el juego
        JButton iniciarBtn = new JButton("Iniciar");
        iniciarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
        panelPrincipal.add(iniciarBtn, BorderLayout.WEST);

        JButton detenerBtn = new JButton("Detener");
        detenerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopGame();
            }
        });
        panelPrincipal.add(detenerBtn, BorderLayout.EAST);

        add(panelPrincipal);
    }

    // Metodo para inicializar el estado inicial del tablero
    private void boardInit() {
        if (populationString == null) {
            // Si no se proporciona una población inicial, se inicializa con celdas muertas
            for (int i = 0; i < numCellsY; i++) {
                for (int j = 0; j < numCellsX; j++) {
                    boardState[i][j] = false;
                    boardButs[i][j].setBackground(Color.BLACK);
                }
            }
        } else if (populationString.equals("rnd")) {
            // Si se solicita una población inicial aleatoria
            Random random = new Random();
            for (int i = 0; i < numCellsY; i++) {
                for (int j = 0; j < numCellsX; j++) {
                    boardState[i][j] = random.nextBoolean();
                    Color color = boardState[i][j] ? Color.RED : Color.BLACK;
                    boardButs[i][j].setBackground(color);
                }
            }
        } else {
            // Si se proporciona una cadena de población inicial especifica
            String[] sections = populationString.split("#");
            if (sections.length > numCellsY || sections[0].length() > numCellsX) {
                System.out.println("¡La poblacion inicial no cabe en la cuadricula!");
                System.exit(1);
            }
            for (int i = 0; i < numCellsY; i++) {
                if (i < sections.length) {
                    String section = sections[i];
                    if (section.length() > numCellsX) {
                        System.out.println("¡La poblacion inicial no cabe en la cuadricula!");
                        System.exit(1);
                    }
                    for (int j = 0; j < numCellsX; j++) {
                        if (j < section.length()) {
                            boardState[i][j] = section.charAt(j) == '1'; // Si es un 1 o true pasa a ser rojo si no negro (muerto)
                            Color color = boardState[i][j] ? Color.RED : Color.BLACK; // operador ternario
                            boardButs[i][j].setBackground(color);
                        } else {
                            boardState[i][j] = false;
                            boardButs[i][j].setBackground(Color.BLACK);
                        }
                    }
                } else {
                    for (int j = 0; j < numCellsX; j++) {
                        boardState[i][j] = false;
                        boardButs[i][j].setBackground(Color.BLACK);
                    }
                }
            }
        }
    }

    // Metodo para iniciar el hilo que ejecuta el juego
    private void startGame() {
        running = true;
        final int[] currentGeneration = {0};
        new Thread(() -> {
            while (running && (g == 0 || currentGeneration[0] < g)) { // Ejecutar mientras esté activo y no se haya alcanzado el limite de generaciones
                updateBoard();
                updateInterface();
                try {
                    Thread.sleep(millisGen);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                currentGeneration[0]++; // Incrementar el contador de generaciones
            }
            running = false;
        }).start();
    }

    // Metodo para detener el juego
    private void stopGame() {
        running = false;
    }

    // Metodo para actualizar el estado del tablero
    private void updateBoard() {
        boolean[][] newBoard = new boolean[numCellsY][numCellsX];

        for (int i = 0; i < numCellsY; i++) {
            for (int j = 0; j < numCellsX; j++) {
                int neighbors = countNeighbors(i, j);
                if (boardState[i][j]) {
                    newBoard[i][j] = (neighbors == 2 || neighbors == 3);
                } else {
                    newBoard[i][j] = (neighbors == 3);
                }
            }
        }

        boardState = newBoard;
    }

    // Metodo para contar los neighbors de una celda
    private int countNeighbors(int x, int y) {
        int neighbors = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;

                int nx = (x + i + numCellsY) % numCellsY;
                int ny = (y + j + numCellsX) % numCellsX;

                if (boardState[nx][ny]) {
                    neighbors++;
                }
            }
        }

        return neighbors;
    }

    // Metodo para actualizar la interfaz grafica segun el estado del tablero
    private void updateInterface() {
        for (int i = 0; i < numCellsY; i++) {
            for (int j = 0; j < numCellsX; j++) {
                Color color = boardState[i][j] ? Color.RED : Color.BLACK;
                boardButs[i][j].setBackground(color);
            }
        }
    }

    // Clase interna para manejar el evento de clic en un boton del tablero
    private class BotonClickListener implements ActionListener {
        private int x, y;

        public BotonClickListener(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Cambiar el estado de la celda al hacer clic en el boton
            boardState[x][y] = !boardState[x][y];
            Color color = boardState[x][y] ? Color.RED : Color.BLACK;
            boardButs[x][y].setBackground(color);
        }
    }

    // Metodo principal para ejecutar el programa
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameOfLife(args));
    }
}
// Recomiendo ejecutar el programa por consola con el comando java src/GameOfLife.java w=10 h=20 s=500 t=30 g=100 p=rnd