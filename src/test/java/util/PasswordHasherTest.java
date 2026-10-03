package util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PasswordHasherTest {

  @Test
  void hashDevuelveUnaCadenaConFormatoBCrypt() {
    String hash = PasswordHasher.hash("clave-super-secreta");
    assertTrue(PasswordHasher.esHashBCrypt(hash));
  }

  @Test
  void dosHashesDeLaMismaClaveSonDistintos() {
    String primero = PasswordHasher.hash("misma-clave");
    String segundo = PasswordHasher.hash("misma-clave");
    assertNotEquals(primero, segundo);
  }

  @Test
  void verificarAceptaLaClaveOriginal() {
    String hash = PasswordHasher.hash("clave-correcta");
    assertTrue(PasswordHasher.verificar("clave-correcta", hash));
  }

  @Test
  void verificarRechazaUnaClaveEquivocada() {
    String hash = PasswordHasher.hash("clave-correcta");
    assertFalse(PasswordHasher.verificar("clave-equivocada", hash));
  }

  @Test
  void verificarRechazaValoresNulos() {
    String hash = PasswordHasher.hash("clave");
    assertFalse(PasswordHasher.verificar(null, hash));
    assertFalse(PasswordHasher.verificar("clave", null));
  }

  @Test
  void verificarRechazaUnHashInvalido() {
    assertFalse(PasswordHasher.verificar("clave", "no-es-un-hash"));
  }

  @Test
  void esHashBCryptDetectaTextoPlano() {
    assertFalse(PasswordHasher.esHashBCrypt("admin123"));
    assertFalse(PasswordHasher.esHashBCrypt(""));
    assertFalse(PasswordHasher.esHashBCrypt(null));
  }

  @Test
  void esHashBCryptAceptaHashesReales() {
    String hash = PasswordHasher.hash("cualquier-clave");
    assertTrue(PasswordHasher.esHashBCrypt(hash));
  }

  @Test
  void hashRechazaClavesVaciasONulas() {
    assertThrows(IllegalArgumentException.class, () -> PasswordHasher.hash(null));
    assertThrows(IllegalArgumentException.class, () -> PasswordHasher.hash(""));
  }
}
