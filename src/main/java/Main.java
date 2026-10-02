import catalogo.RolUsuario;
import hilos.HiloMensaje;
import java.util.Scanner;
import model.Compra;
import model.Comprador;
import model.Evento;
import model.GestorUsuarios;
import model.HistorialCompras;
import model.SistemaGestionBoletos;
import model.Usuario;
import persistencia.CompraPersistencia;
import ui.ConsolaUI;

/** Punto de entrada de la versión por consola del sistema de gestión de boletos. */
public class Main {

  /**
   * Inicia el sistema por consola: autentica al usuario y muestra el menú según su rol.
   *
   * @param args argumentos de la línea de comandos (no se usan)
   */
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    ConsolaUI consola = new ConsolaUI(scanner);

    SistemaGestionBoletos sistemaEventos = new SistemaGestionBoletos();
    GestorUsuarios gestorUsuarios = new GestorUsuarios();
    CompraPersistencia compraPersistencia = new CompraPersistencia();
    HistorialCompras historialCompras = new HistorialCompras(compraPersistencia);

    ejecutarHilosDemo();

    try {
      sistemaEventos.cargarEventos();
      gestorUsuarios.cargarUsuarios();
      System.out.println("Sistema cargado correctamente.");
    } catch (RuntimeException e) {
      System.out.println("Error critico del sistema: " + e.getMessage());
      scanner.close();
      return;
    }

    Usuario usuarioActual = autenticar(consola, gestorUsuarios);
    if (usuarioActual == null) {
      System.out.println("No se pudo iniciar sesion. Cerrando el sistema.");
      scanner.close();
      return;
    }

    if (usuarioActual.getRol() == RolUsuario.ADMINISTRADOR) {
      ejecutarMenuAdministrador(consola, compraPersistencia, usuarioActual);
      scanner.close();
      return;
    }

    Comprador comprador = (Comprador) usuarioActual;
    ejecutarFlujoCompra(consola, sistemaEventos, compraPersistencia, historialCompras, comprador);

    System.out.println("Gracias por su compra. Hasta luego!");
    scanner.close();
  }

  private static Usuario autenticar(ConsolaUI consola, GestorUsuarios gestorUsuarios) {
    while (true) {
      int opcion;
      try {
        opcion = consola.leerOpcionInicio();
      } catch (NumberFormatException e) {
        System.out.println("Opcion invalida.");
        continue;
      }

      if (opcion == 1) {
        String correo = consola.leerCorreo();
        String clave = consola.leerClave();
        Usuario usuario = gestorUsuarios.iniciarSesion(correo, clave);
        if (usuario != null) {
          return usuario;
        }
        System.out.println("Credenciales invalidas.");
      } else if (opcion == 2) {
        String nombre = consola.leerNombreRegistro();
        String correo = consola.leerCorreo();
        String clave = consola.leerClave();
        boolean exito = gestorUsuarios.registrar(nombre, correo, clave);
        if (exito) {
          System.out.println("Registro exitoso. Ahora inicie sesion.");
        } else {
          System.out.println(
              "No se pudo registrar: el correo ya existe o los datos son invalidos.");
        }
      } else {
        System.out.println("Opcion invalida.");
      }
    }
  }

  private static void ejecutarMenuAdministrador(ConsolaUI consola,
      CompraPersistencia compraPersistencia,
      Usuario admin) {
    System.out.println("\nBienvenido, " + admin.getNombre() + " (Administrador).");
    System.out.println("1. Generar reporte de ventas por categoria");
    System.out.println("2. Salir");
    int opcion;
    try {
      opcion = consola.leerOpcion();
    } catch (NumberFormatException e) {
      System.out.println("Opcion invalida. Cerrando el sistema.");
      return;
    }

    if (opcion == 1) {
      String categoria = consola.leerCategoriaReporte();
      System.out.println("\n" + compraPersistencia.generarReportePorCategoria(categoria));
    }
    System.out.println(
        "El resto de funciones de administracion se implementaran en las proximas semanas.");
  }

  private static void ejecutarFlujoCompra(ConsolaUI consola, SistemaGestionBoletos sistema,
      CompraPersistencia compraPersistencia,
      HistorialCompras historialCompras, Comprador comprador) {
    boolean continuar = true;

    do {
      try {
        System.out.println("\n=== Sistema de Gestion de Boletos ===");
        System.out.println("1. Ver eventos y comprar");
        System.out.println("2. Ver mi historial de compras");
        int menu = consola.leerOpcion();

        if (menu == 2) {
          System.out.println(historialCompras.mostrarHistorial(comprador.getCorreo()));
          continuar = consola.leerContinuar();
          continue;
        }

        sistema.mostrarEventos();

        int opcion = consola.leerOpcion();
        Evento evento = sistema.seleccionarEvento(opcion);

        int cantidad = consola.leerCantidadBoletos();

        Compra compra = new Compra(evento, comprador, cantidad);
        double total = compra.calcularTotal();
        System.out.println("\nTotal a pagar: $" +