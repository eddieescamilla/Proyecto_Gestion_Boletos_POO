package ui;

import java.util.Scanner;

/** Gestiona la entrada de datos por consola. */
public class ConsolaUI {

  private final Scanner scanner;

  /**
   * Crea la interfaz de consola.
   *
   * @param scanner lector de la entrada estándar
   */
  public ConsolaUI(Scanner scanner) {
    this.scanner = scanner;
  }

  /**
   * Muestra el menú de inicio y lee la opción elegida.
   *
   * @return la opción elegida
   * @throws NumberFormatException si el texto ingresado no es un número
   */
  public int leerOpcionInicio() {
    System.out.println("\n=== Bienvenido ===");
    System.out.println("1. Iniciar sesion");
    System.out.println("2. Registrarme");
    System.out.print("Opcion: ");
    return Integer.parseInt(scanner.nextLine());
  }

  /**
   * Lee el nombre completo para el registro.
   *
   * @return el nombre ingresado
   */
  public String leerNombreRegistro() {
    System.out.print("Nombre completo: ");
    return scanner.nextLine();
  }

  /**
   * Lee el correo electrónico.
   *
   * @return el correo ingresado
   */
  public String leerCorreo() {
    System.out.print("Correo: ");
    return scanner.nextLine();
  }

  /**
   * Lee la contraseña.
   *
   * @return la contraseña ingresada
   */
  public String leerClave() {
    System.out.print("Clave: ");
    return scanner.nextLine();
  }

  /**
   * Lee una opción numérica de un menú.
   *
   * @return la opción elegida
   * @throws NumberFormatException si el texto ingresado no es un número
   */
  public int leerOpcion() {
    System.out.print("Opcion: ");
    return Integer.parseInt(scanner.nextLine());
  }

  /**
   * Lee la cantidad de boletos a comprar.
   *
   * @return la cantidad ingresada
   * @throws NumberFormatException si el texto ingresado no es un número
   */
  public int leerCantidadBoletos() {
    System.out.print("Ingrese la cantidad de boletos a comprar: ");
    return Integer.parseInt(scanner.nextLine());
  }

  /**
   * Lee el código de descuento, o "no" si no tiene.
   *
   * @return el texto ingresado
   */
  public String leerCodigoDescuento() {
    System.out.print("Tiene un codigo de descuento? Ingreselo o escriba no: ");
    return scanner.nextLine();
  }

  /**
   * Pregunta si el usuario desea realizar otra compra.
   *
   * @return {@code true} si responde que sí
   */
  public boolean leerContinuar() {
    System.out.print("\nDesea realizar otra compra? (s/n): ");
    String respuesta = scanner.nextLine();
    char c = respuesta.isBlank() ? 'n' : respuesta.charAt(0);
    return c == 's' || c == 'S';
  }

  /**
   * Lee la categoría para el reporte de ventas.
   *
   * @return la categoría ingresada
   */
  public String leerCategoriaReporte() {
    System.out.print("Ingrese la categoria para el reporte: ");
    return scanner.nextLine();
  }
}