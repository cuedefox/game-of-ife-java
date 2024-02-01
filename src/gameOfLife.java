package src;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Clase principal extendida de JFrame
public class gameOfLife extends JFrame {
    private int numCeldasY;
    private int numCeldasX;
    private int tamBut;
    private  int milisGen;

    private JButton[][] tableroBotones;
    private boolean[][] tableroEstado;
    private volatile boolean ejecutando;

    // Constructor de la clase
    public gameOfLife(String[] args) {

        validarArgs(args);

        tableroBotones = new JButton[numCeldasY][numCeldasX];
        tableroEstado = new boolean[numCeldasY][numCeldasX];
        ejecutando = false;

        inicializarInterfaz();
        inicializarTablero();

        setTitle("Juego de la Vida Jala");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Metodo para validar los argumentos de la linea de comandos y asignando variables, w= ancho, h=alto, s=velocidad, t=tamaño del boton
    private void validarArgs(String[] args) {
        for (String arg : args) {
            String[] parts = arg.split("=");

            if (parts.length != 2) {
                System.out.println("Formato de argumento no valido: " + arg);
                System.exit(1);
            }

            String key = parts[0];
            String value = parts[1];

            switch (key) {
                case "w":
                    try {
                        numCeldasY = Integer.parseInt(value);
                        System.out.println("width = [" + numCeldasY + "]");
                    } catch (NumberFormatException e) {
                        System.out.println("Ancho (w) debe ser un numero entero.");
                        System.exit(1);
                    }
                    break;
                case "h":
                    try {
                        numCeldasX = Integer.parseInt(value);
                        System.out.println("height = [" + numCeldasX + "]");
                    } catch (NumberFormatException e) {
                        System.out.println("Altura (h) debe ser un numero entero.");
                        System.exit(1);
                    }
                    break;
                case "s":
                    try {
                        milisGen = Integer.parseInt(value);
                        System.out.println("speed = [" + milisGen + "]");
                    } catch (NumberFormatException e) {
                        System.out.println("Generaciones (s) debe ser un numero entero.");
                        System.exit(1);
                    }
                    break;
                case "t":
                    try {
                        tamBut = Integer.parseInt(value);
                        System.out.println("button = [" + tamBut + "]");
                    } catch (NumberFormatException e) {
                        System.out.println("Button (b) debe ser un numero entero.");
                        System.exit(1);
                    }
                    break;
            }
        }
    }

    // Metodo para inicializar la interfaz grafica
    private void inicializarInterfaz() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());

        JPanel panelTablero = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(1, 1, 1, 1);

        for (int i = 0; i < numCeldasY; i++) {
            for (int j = 0; j < numCeldasX; j++) {
                JButton boton = new JButton();
                boton.addActionListener(new BotonClickListener(i, j));
                boton.setPreferredSize(new Dimension(tamBut, tamBut));
                boton.setMaximumSize(new Dimension(tamBut, tamBut));
                boton.setMinimumSize(new Dimension(tamBut, tamBut));
                panelTablero.add(boton, gbc);
                tableroBotones[i][j] = boton;
                gbc.gridx++;
            }
            gbc.gridx = 0;
            gbc.gridy++;
        }

        panelPrincipal.add(panelTablero, BorderLayout.CENTER);

        JButton iniciarBtn = new JButton("Iniciar");
        iniciarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarJuego();
            }
        });
        panelPrincipal.add(iniciarBtn, BorderLayout.WEST);

        JButton detenerBtn = new JButton("Detener");
        detenerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                detenerJuego();
            }
        });
        panelPrincipal.add(detenerBtn, BorderLayout.EAST);

        add(panelPrincipal);
    }

    // Metodo para inicializar el estado inicial del tablero
    private void inicializarTablero() {
        for (int i = 0; i < numCeldasY; i++) {
            for (int j = 0; j < numCeldasX; j++) {
                tableroEstado[i][j] = false;
                tableroBotones[i][j].setBackground(Color.BLACK);
            }
        }
    }

    // Metodo para iniciar el hilo que ejecuta el juego
    private void iniciarJuego() {
        ejecutando = true;
        new Thread(() -> {
            while (ejecutando) {
                actualizarTablero();
                actualizarInterfaz();
                try {
                    Thread.sleep(milisGen);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    // Metodo para detener el juego
    private void detenerJuego() {
        ejecutando = false;
    }

    // Metodo para actualizar el estado del tablero
    private void actualizarTablero() {
        boolean[][] nuevoTablero = new boolean[numCeldasY][numCeldasX];

        for (int i = 0; i < numCeldasY; i++) {
            for (int j = 0; j < numCeldasX; j++) {
                int vecinos = contarVecinos(i, j);
                if (tableroEstado[i][j]) {
                    nuevoTablero[i][j] = (vecinos == 2 || vecinos == 3);
                } else {
                    nuevoTablero[i][j] = (vecinos == 3);
                }
            }
        }

        tableroEstado = nuevoTablero;
    }

    // Metodo para contar los vecinos de una celda (puede presentar errores todavia o no estar optimizado del todo)
    private int contarVecinos(int x, int y) {
        int vecinos = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;

                int nx = (x + i + numCeldasY) % numCeldasY;
                int ny = (y + j + numCeldasX) % numCeldasX;

                if (tableroEstado[nx][ny]) {
                    vecinos++;
                }
            }
        }

        return vecinos;
    }

    // Metodo para actualizar la interfaz grafica según el estado del tablero
    private void actualizarInterfaz() {
        for (int i = 0; i < numCeldasY; i++) {
            for (int j = 0; j < numCeldasX; j++) {
                Color color = tableroEstado[i][j] ? Color.RED : Color.BLACK;
                tableroBotones[i][j].setBackground(color);
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
            tableroEstado[x][y] = !tableroEstado[x][y];
            Color color = tableroEstado[x][y] ? Color.RED : Color.BLACK;
            tableroBotones[x][y].setBackground(color);
        }
    }

    // Metodo principal para ejecutar el programa
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new gameOfLife(args));
    }
}
// Recomiendo ejecutar el programa por consola con el comando java src/GameOfLife.java w=10 h=20 s=500 t=30 