package com.josue.pedidos_ms.shared.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase base que proporciona un logger configurado para cualquier clase que la
 * extienda.
 * Incluye métodos de conveniencia para logging estructurado con eventos.
 */
public abstract class BaseLogger {

  /**
   * Logger configurado automáticamente con el nombre de la clase que extiende
   * BaseLogger.
   */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * Registra un mensaje de información.
   * 
   * @param mensaje Mensaje con placeholders {}
   * @param args    Argumentos para los placeholders
   */
  protected void logInfo(String mensaje, Object... args) {
    logger.info(mensaje, args);
  }

  /**
   * Registra un mensaje de advertencia.
   * 
   * @param mensaje Mensaje con placeholders {}
   * @param args    Argumentos para los placeholders
   */
  protected void logWarn(String mensaje, Object... args) {
    logger.warn(mensaje, args);
  }

  /**
   * Registra un mensaje de debug.
   * 
   * @param mensaje Mensaje con placeholders {}
   * @param args    Argumentos para los placeholders
   */
  protected void logDebug(String mensaje, Object... args) {
    logger.debug(mensaje, args);
  }

  /**
   * Registra un mensaje de error con excepción.
   * 
   * @param mensaje   Mensaje descriptivo del error
   * @param throwable Excepción que causó el error
   */
  protected void logError(String mensaje, Throwable throwable) {
    logger.error(mensaje, throwable);
  }

  // Métodos con eventos estructurados

  /**
   * Registra un mensaje de información con evento.
   * 
   * @param evento  Tipo de evento (constante de LogEvents)
   * @param mensaje Mensaje con placeholders {}
   * @param args    Argumentos para los placeholders
   */
  protected void logInfo(String evento, String mensaje, Object... args) {
    logger.info("[{}] " + mensaje, prependEvent(evento, args));
  }

  /**
   * Registra un mensaje de advertencia con evento.
   * 
   * @param evento  Tipo de evento (constante de LogEvents)
   * @param mensaje Mensaje con placeholders {}
   * @param args    Argumentos para los placeholders
   */
  protected void logWarn(String evento, String mensaje, Object... args) {
    logger.warn("[{}] " + mensaje, prependEvent(evento, args));
  }

  /**
   * Registra un mensaje de debug con evento.
   * 
   * @param evento  Tipo de evento (constante de LogEvents)
   * @param mensaje Mensaje con placeholders {}
   * @param args    Argumentos para los placeholders
   */
  protected void logDebug(String evento, String mensaje, Object... args) {
    logger.debug("[{}] " + mensaje, prependEvent(evento, args));
  }

  /**
   * Registra un mensaje de error con evento y excepción.
   * 
   * @param evento  Tipo de evento (constante de LogEvents)
   * @param mensaje Mensaje con placeholders {}
   * @param args    Argumentos para los placeholders (último puede ser Throwable)
   */
  protected void logError(String evento, String mensaje, Object... args) {
    if (args.length > 0 && args[args.length - 1] instanceof Throwable) {
      Throwable throwable = (Throwable) args[args.length - 1];
      Object[] messageArgs = new Object[args.length - 1];
      System.arraycopy(args, 0, messageArgs, 0, args.length - 1);
      logger.error("[{}] " + mensaje, prependEvent(evento, messageArgs), throwable);
    } else {
      logger.error("[{}] " + mensaje, prependEvent(evento, args));
    }
  }

  /**
   * Utilidad para agregar el evento como primer parámetro.
   */
  private Object[] prependEvent(String evento, Object[] args) {
    Object[] result = new Object[args.length + 1];
    result[0] = evento;
    System.arraycopy(args, 0, result, 1, args.length);
    return result;
  }
}
