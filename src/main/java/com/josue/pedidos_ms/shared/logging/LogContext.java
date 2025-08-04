package com.josue.pedidos_ms.shared.logging;

import org.slf4j.MDC;

/**
 * Utilidades para manejo de contexto en logs usando MDC (Mapped Diagnostic
 * Context).
 * Permite agregar información contextual (como número de pedido) a todos los
 * logs
 * del thread actual automáticamente.
 */
public class LogContext {

  private static final String PEDIDO_KEY = "pedido";
  private static final String OPERACION_KEY = "operacion";

  /**
   * Establece el número de pedido en el contexto de logging.
   * Todos los logs subsecuentes mostrarán este número automáticamente.
   * 
   * @param numeroPedido El número del pedido a agregar al contexto
   */
  public static void setPedido(String numeroPedido) {
    MDC.put(PEDIDO_KEY, numeroPedido);
  }

  /**
   * Establece la operación actual en el contexto de logging.
   * 
   * @param operacion Nombre de la operación (ej: "CARGA_CSV", "VALIDACION")
   */
  public static void setOperacion(String operacion) {
    MDC.put(OPERACION_KEY, operacion);
  }

  /**
   * Limpia todo el contexto de logging.
   * Debe llamarse al final de cada operación para evitar contaminar otros
   * threads.
   */
  public static void clear() {
    MDC.clear();
  }

  /**
   * Obtiene el número de pedido actual del contexto.
   * 
   * @return El número de pedido o null si no está establecido
   */
  public static String getPedido() {
    return MDC.get(PEDIDO_KEY);
  }

  /**
   * Ejecuta una operación con contexto de pedido y la limpia automáticamente.
   * 
   * @param numeroPedido Número del pedido
   * @param operacion    Operación a ejecutar
   */
  public static void withPedido(String numeroPedido, Runnable operacion) {
    try {
      setPedido(numeroPedido);
      operacion.run();
    } finally {
      clear();
    }
  }
}
