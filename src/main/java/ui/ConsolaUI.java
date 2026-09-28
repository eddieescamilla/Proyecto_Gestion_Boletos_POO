package ui;

import java.util.Scanner;

public class ConsolaUI {

  private final Scanner scanner;

  public ConsolaUI(Scanner scanner) {
    this.scanner = scanner;
  }

  public int leerOpcionInicio() {
    System.out.println("\n=== Bienvenido ===");
    System.out.println("1. Iniciar sesion");
    System.out.println("2. Registrarme");
    System.out.print("Opcion: ");
    return Integer.parseInt(scanner.nextLine());
  }

  public String leerNombreRegistro() {
    System.out.print("Nombre completo: ");
    return scanner.nextLine();
  }

  public String leerCorreo() {
    System.out.print("Correo: ");
    return scanner.nextLine();
  }

  public String leerClave() {
    System.out.print("Clave: ");
    return scanner.nextLine();
  }

  public int leerOpcion() {
    System.out.print("Opcion: ");
    return Integer.parseInt(scanner.nextLine());
  }

  public int leerCantidadBoletos() {
    System.out.print("Ingrese la cantidad de boletos a comprar: ");
    return Integer.parseInt(scanner.nextLine());
  }

  public String leerCodigoDescuento() {
    System.out.print("Tiene un codigo de descuento? Ingreselo o escriba no: ");
    return scanner.nextLine();
  }

  public boolean leerContinuar() {
    System.out.print("\nDesea realizar otra compra? (s/n): ");
    String respuesta = scanner.nextLine();
    char c = respuesta.isBlank() ? 'n' : respuesta.charAt(0);
    return c == 's' || c == 'S';
  }

  public String leerCategoriaReporte() {
    System.out.print("Ingrese la categoria para el reporte: ");
    return scanner.nextLine();
  }
}