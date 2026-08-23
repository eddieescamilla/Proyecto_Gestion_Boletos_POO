import model.Compra;
import model.Comprador;
import model.CompradorVIP;
import model.Evento;
import model.SistemaGestionBoletos;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SistemaGestionBoletos sistema = new SistemaGestionBoletos("eventos.txt");

        try {
            sistema.cargarEventos();
            System.out.println("Eventos cargados desde 'eventos.txt': "
                    + sistema.getListaEventos().size() + " evento(s).");
        } catch (RuntimeException e) {
            System.out.println("Error critico del sistema: " + e.getMessage());
            return;
        }

        char continuar = 's';

        do {
            try {
                System.out.println("\n=== Sistema de Gestion de Boletos ===");
                sistema.mostrarEventos();

                System.out.print("Opcion: ");
                int opcion = Integer.parseInt(scanner.nextLine());
                Evento evento = sistema.seleccionarEvento(opcion);

                System.out.print("\nIngrese su nombre: ");
                String nombre = scanner.nextLine();

                System.out.print("Es un comprador VIP? (s/n): ");
                String esVip = scanner.nextLine();
                Comprador comprador = esVip.equalsIgnoreCase("s")
                        ? new CompradorVIP(nombre)
                        : new Comprador(nombre);

                System.out.print("Ingrese la cantidad de boletos a comprar: ");
                int cantidad = Integer.parseInt(scanner.nextLine());

                Compra compra = new Compra(evento, comprador, cantidad);
                double total = compra.calcularTotal();
                System.out.println("\nTotal a pagar: $" + total);

                System.out.print("Tiene un codigo de descuento? Ingreselo o escriba no: ");
                String codigo = scanner.nextLine();
                if (!codigo.equalsIgnoreCase("no") && !codigo.isBlank()) {
                    boolean descuentoAplicado = compra.aplicarDescuento(codigo);
                    if (descuentoAplicado) {
                        System.out.println("Total con descuento: $" + compra.getTotal());
                    } else {
                        System.out.println("Codigo de descuento invalido. No se aplico ningun descuento.");
                    }
                }

                boolean exito = compra.confirmarPago();
                if (exito) {
                    sistema.guardarEventos();
                    System.out.println("\n=== Confirmacion de Compra ===");
                    System.out.println("Comprador          : " + comprador.getNombre());
                    System.out.println("Evento             : " + evento.getNombreEvento());
                    System.out.println("Boletos comprados  : " + compra.getCantidadBoletos());
                    System.out.println("Total pagado       : $" + compra.getTotal());
                    System.out.println("Inventario restante: " + evento.getInventarioDisponible() + " boletos");
                    System.out.println("Inventario actualizado correctamente.");
                } else {
                    System.out.println("No se pudo completar la compra: stock insuficiente.");
                }

            } catch (NumberFormatException e) {
                System.out.println("\nError: debe ingresar solo numeros en ese campo.");
                System.out.println("La operacion no se completo, pero el sistema sigue funcionando.");
            } catch (Exception e) {
                System.out.println("\nError controlado: " + e.getMessage());
                System.out.println("La operacion no se completo, pero el sistema sigue funcionando.");
            }

            System.out.print("\nDesea realizar otra compra? (s/n): ");
            String respuesta = scanner.nextLine();
            continuar = respuesta.isBlank() ? 'n' : respuesta.charAt(0);

        } while (continuar == 's' || continuar == 'S');

        System.out.println("Gracias por su compra. Hasta luego!");
        scanner.close();
    }
}