package persistencia;

public class ConfigBD {

    public static String url() {
        return System.getenv().getOrDefault("BOLETOS_DB_URL", "jdbc:postgresql://localhost:5432/boletos");
    }

    public static String usuario() {
        return System.getenv().getOrDefault("BOLETOS_DB_USER", "boletos");
    }

    public static String clave() {
        return System.getenv().getOrDefault("BOLETOS_DB_PASSWORD", "boletos123");
    }
}
