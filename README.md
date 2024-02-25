# Juego de la Vida

Implementación del "Juego de la Vida" utilizando Java Swing y JFrame.

## Descripción

Este proyecto consiste en una aplicación que simula la evolución de células en un tablero bidimensional siguiendo las reglas del famoso "Juego de la Vida" de Conway. Fue desarrollada como proyecto final para la materia de Programación 1 en Jala University.

## Configuración

La aplicación acepta varios argumentos de línea de comandos para personalizar la configuración del juego:

- `w`: Define el ancho del tablero.
- `h`: Define la altura del tablero.
- `g`: Especifica el número de generaciones.
- `s`: Especifica la velocidad de generación en milisegundos.
- `t`: Establece el tamaño de los botones en el tablero.
- `p`: Define la población inicial del juego.

### Ejemplo de Uso

Para ejecutar el juego con un tablero de 50x50 celdas, 100 generaciones, una velocidad de generación de 100 milisegundos, botones de tamaño 20 y una población inicial específica, puedes usar el siguiente comando:

```bash
java GameOfLife w=50 h=50 g=100 s=100 t=20 p="101#010#111"

