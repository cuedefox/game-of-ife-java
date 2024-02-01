# Juego de la Vida

Implementación del "Juego de la Vida" utilizando Java Swing y JFrame.

## Descripción

Esta aplicación simula la evolución de células en un tablero bidimensional según las reglas del juego de la vida de Conway. Fue desarrollada como proyecto final para la materia Programación 1 de Jala University.

## Configuración

La aplicación acepta argumentos de línea de comandos para personalizar el juego. Estos argumentos incluyen:

- `w`: Ancho del tablero.
- `h`: Altura del tablero.
- `s`: Velocidad de generación en milisegundos.
- `t`: Tamaño de los botones en el tablero.

Ejemplo de uso:

```bash
java gameOfLife w=50 h=50 s=100 t=20
