package com.josue.pedidos_ms.shared.logging;

/**
 * Constantes para eventos de logging estructurado.
 * Facilita la búsqueda y filtrado de logs en producción.
 */
public class LogEvents {

  // Eventos de procesamiento de archivos
  public static final String INICIO_CARGA_CSV = "INICIO_CARGA_CSV";
  public static final String FIN_CARGA_CSV = "FIN_CARGA_CSV";
  public static final String ERROR_LECTURA_CSV = "ERROR_LECTURA_CSV";
  public static final String ARCHIVO_VACIO = "ARCHIVO_VACIO";

  // Eventos de validación
  public static final String INICIO_VALIDACION = "INICIO_VALIDACION";
  public static final String PEDIDO_VALIDO = "PEDIDO_VALIDO";
  public static final String PEDIDO_INVALIDO = "PEDIDO_INVALIDO";
  public static final String ERROR_VALIDACION = "ERROR_VALIDACION";

  // Eventos de procesamiento de pedidos
  public static final String PEDIDO_PROCESADO = "PEDIDO_PROCESADO";
  public static final String CLIENTE_CREADO = "CLIENTE_CREADO";
  public static final String ZONA_CREADA = "ZONA_CREADA";
  public static final String PEDIDO_GUARDADO = "PEDIDO_GUARDADO";

  // Eventos de errores críticos
  public static final String ERROR_BASE_DATOS = "ERROR_BASE_DATOS";
  public static final String ERROR_SISTEMA = "ERROR_SISTEMA";
  public static final String ERROR_CONFIGURACION = "ERROR_CONFIGURACION";

  // Eventos de estadísticas
  public static final String RESUMEN_PROCESAMIENTO = "RESUMEN_PROCESAMIENTO";
  public static final String ESTADISTICAS_VALIDACION = "ESTADISTICAS_VALIDACION";
}
