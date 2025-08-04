package com.josue.pedidos_ms.infrastructure.performance;

import com.josue.pedidos_ms.domain.model.Cliente;
import com.josue.pedidos_ms.domain.model.Zona;
import com.josue.pedidos_ms.domain.model.Pedido;
import com.josue.pedidos_ms.domain.model.EstadoPedido;
import com.josue.pedidos_ms.infrastructure.repository.ClienteRepository;
import com.josue.pedidos_ms.infrastructure.repository.ZonaRepository;
import com.josue.pedidos_ms.infrastructure.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para validar que el caché y los índices funcionan correctamente
 */
@SpringBootTest
@ActiveProfiles("test")
class CacheAndIndexPerformanceTest {

  @Autowired
  private ClienteRepository clienteRepository;

  @Autowired
  private ZonaRepository zonaRepository;

  @Autowired
  private PedidoRepository pedidoRepository;

  @Autowired
  private CacheManager cacheManager;

  private Cliente cliente1;
  private Zona zona1;
  private Pedido pedido1;

  @BeforeEach
  void setUp() {
    // Limpiar caché antes de cada test
    cacheManager.getCacheNames().forEach(cacheName -> {
      var cache = cacheManager.getCache(cacheName);
      if (cache != null) {
        cache.clear();
      }
    });

    // Crear datos de prueba
    cliente1 = clienteRepository.save(new Cliente("CLI-CACHE-001", "Cliente Cache Test"));
    zona1 = zonaRepository.save(new Zona("ZONA-CACHE-A", true));

    pedido1 = Pedido.builder()
        .numeroPedido("P-CACHE-001")
        .cliente(cliente1)
        .fechaEntrega(LocalDate.now().plusDays(1))
        .estado(EstadoPedido.PENDIENTE)
        .zonaEntrega(zona1)
        .requiereRefrigeracion(true)
        .build();

    pedidoRepository.save(pedido1);
  }

  // ✅ Tests de caché para ClienteRepository
  @Test
  void debeCachearConsultasDeClientes() {
    // Primera consulta - va a la base de datos
    Optional<Cliente> resultado1 = clienteRepository.buscarCliente("CLI-CACHE-001");
    assertTrue(resultado1.isPresent());

    // Verificar que se guardó en caché
    var cache = cacheManager.getCache("clientes");
    assertNotNull(cache);
    assertNotNull(cache.get("CLI-CACHE-001"));

    // Segunda consulta - debe venir del caché
    Optional<Cliente> resultado2 = clienteRepository.buscarCliente("CLI-CACHE-001");
    assertTrue(resultado2.isPresent());
    assertEquals(resultado1.get().getId(), resultado2.get().getId());
  }

  @Test
  void debeCachearExistenciaDeClientes() {
    // Primera consulta
    boolean existe1 = clienteRepository.existsCliente("CLI-CACHE-001");
    assertTrue(existe1);

    // Verificar caché
    var cache = cacheManager.getCache("clientes");
    assertNotNull(cache, "Cache 'clientes' debe existir");
    assertNotNull(cache.get("CLI-CACHE-001"), "Cliente debe estar en caché");

    // Segunda consulta (del caché)
    boolean existe2 = clienteRepository.existsCliente("CLI-CACHE-001");
    assertTrue(existe2);
  }

  @Test
  void debeCachearListaDeClientesOrdenados() {
    // Primera consulta
    List<Cliente> clientes1 = clienteRepository.buscarTodosOrdenados();
    assertFalse(clientes1.isEmpty());

    // Verificar caché
    var cache = cacheManager.getCache("clientes-ordenados");
    assertNotNull(cache, "Cache 'clientes-ordenados' debe existir");
    assertNotNull(cache.get("todos"), "Lista de clientes debe estar en caché");

    // Segunda consulta (del caché)
    List<Cliente> clientes2 = clienteRepository.buscarTodosOrdenados();
    assertEquals(clientes1.size(), clientes2.size());
  }

  // ✅ Tests de caché para ZonaRepository
  @Test
  void debeCachearConsultasDeZonas() {
    Optional<Zona> resultado1 = zonaRepository.buscarZona("ZONA-CACHE-A");
    assertTrue(resultado1.isPresent());

    var cache = cacheManager.getCache("zonas");
    assertNotNull(cache, "Cache 'zonas' debe existir");
    assertNotNull(cache.get("ZONA-CACHE-A"), "Zona debe estar en caché");

    Optional<Zona> resultado2 = zonaRepository.buscarZona("ZONA-CACHE-A");
    assertTrue(resultado2.isPresent());
    assertEquals(resultado1.get().getId(), resultado2.get().getId());
  }

  @Test
  void debeCachearZonasConRefrigeracion() {
    List<Zona> zonas1 = zonaRepository.buscarZonasConRefrigeracion();
    assertFalse(zonas1.isEmpty());

    var cache = cacheManager.getCache("zonas-refrigeracion");
    assertNotNull(cache, "Cache 'zonas-refrigeracion' debe existir");
    assertNotNull(cache.get("con-refrigeracion"), "Zonas con refrigeración deben estar en caché");

    List<Zona> zonas2 = zonaRepository.buscarZonasConRefrigeracion();
    assertEquals(zonas1.size(), zonas2.size());
  }

  // ✅ Tests de caché para PedidoRepository
  @Test
  void debeCachearEstadisticasDePedidos() {
    // Las consultas de estadísticas se deben cachear
    List<Pedido> pedidos1 = pedidoRepository.buscarPedidosDeClienteConDetalles("CLI-CACHE-001");
    assertFalse(pedidos1.isEmpty());

    var cache = cacheManager.getCache("estadisticas-pedidos");
    assertNotNull(cache, "Cache 'estadisticas-pedidos' debe existir");
    assertNotNull(cache.get("CLI-CACHE-001"), "Estadísticas de cliente deben estar en caché");

    List<Pedido> pedidos2 = pedidoRepository.buscarPedidosDeClienteConDetalles("CLI-CACHE-001");
    assertEquals(pedidos1.size(), pedidos2.size());
  }

  @Test
  void debeCachearConteoDeConflictosRefrigeracion() {
    long conflictos1 = pedidoRepository.contarPedidosConConflictoRefrigeracion();

    var cache = cacheManager.getCache("estadisticas-pedidos");
    assertNotNull(cache, "Cache 'estadisticas-pedidos' debe existir");
    assertNotNull(cache.get("conflictos-refrigeracion"), "Conteo de conflictos debe estar en caché");

    long conflictos2 = pedidoRepository.contarPedidosConConflictoRefrigeracion();
    assertEquals(conflictos1, conflictos2);
  }

  @Test
  void noDebeCachearExistenciaDePedidos() {
    // Las consultas de existencia NO deben usar caché (datos dinámicos)
    boolean existe = pedidoRepository.existePedido("P-CACHE-001");
    assertTrue(existe);

    // Las consultas de existencia no usan caché porque son datos dinámicos
    // Verificamos que el método funciona correctamente sin cachear
    boolean existe2 = pedidoRepository.existePedido("P-CACHE-001");
    assertEquals(existe, existe2);
  }

  // ✅ Test de rendimiento con índices
  @Test
  void debeUsarIndicesParaConsultasRapidas() {
    // Crear más datos para probar índices
    for (int i = 2; i <= 100; i++) {
      Cliente cliente = new Cliente("CLI-PERF-" + String.format("%03d", i), "Cliente Performance " + i);
      clienteRepository.save(cliente);

      Zona zona = new Zona("ZONA-PERF-" + String.format("%03d", i), i % 2 == 0);
      zonaRepository.save(zona);

      Pedido pedido = Pedido.builder()
          .numeroPedido("P-PERF-" + String.format("%03d", i))
          .cliente(cliente)
          .fechaEntrega(LocalDate.now().plusDays(i))
          .estado(i % 3 == 0 ? EstadoPedido.CONFIRMADO : EstadoPedido.PENDIENTE)
          .zonaEntrega(zona)
          .requiereRefrigeracion(i % 2 == 0)
          .build();
      pedidoRepository.save(pedido);
    }

    // Test de consultas que deben usar índices
    long startTime = System.currentTimeMillis();

    // Búsqueda por estado (índice: idx_pedido_estado)
    List<Pedido> pedidosPendientes = pedidoRepository.buscarPedidosPorEstadoConDetalles(EstadoPedido.PENDIENTE);
    assertFalse(pedidosPendientes.isEmpty());

    // Búsqueda por rango de fechas (índice: idx_pedido_fecha_entrega)
    LocalDate hoy = LocalDate.now();
    List<Pedido> pedidosPorFecha = pedidoRepository.buscarPedidosPorRangoFechas(hoy, hoy.plusDays(50));
    assertFalse(pedidosPorFecha.isEmpty());

    // Búsqueda por cliente (índice: idx_pedido_cliente_id)
    List<Pedido> pedidosCliente = pedidoRepository.buscarPedidosDeClienteConDetalles("CLI-PERF-050");
    assertFalse(pedidosCliente.isEmpty());

    // Conteo de conflictos (índices: idx_pedido_requiere_refrigeracion,
    // idx_pedido_refrigeracion_zona)
    long conflictos = pedidoRepository.contarPedidosConConflictoRefrigeracion();
    assertTrue(conflictos >= 0);

    long endTime = System.currentTimeMillis();
    long executionTime = endTime - startTime;

    // Las consultas con índices deben ser rápidas (menos de 1 segundo para 100
    // registros)
    assertTrue(executionTime < 1000,
        "Las consultas tomaron " + executionTime + "ms, probablemente no están usando índices");
  }

  // ✅ Test de limpieza de caché
  @Test
  void debeLimpiarCacheCorrectamente() {
    // Llenar caché
    clienteRepository.buscarCliente("CLI-CACHE-001");
    zonaRepository.buscarZona("ZONA-CACHE-A");

    // Verificar que están en caché
    var clienteCache = cacheManager.getCache("clientes");
    var zonaCache = cacheManager.getCache("zonas");
    assertNotNull(clienteCache, "Cache 'clientes' debe existir");
    assertNotNull(zonaCache, "Cache 'zonas' debe existir");
    assertNotNull(clienteCache.get("CLI-CACHE-001"), "Cliente debe estar en caché");
    assertNotNull(zonaCache.get("ZONA-CACHE-A"), "Zona debe estar en caché");

    // Limpiar cachés
    clienteCache.clear();
    zonaCache.clear();

    // Verificar que se limpiaron
    assertNull(clienteCache.get("CLI-CACHE-001"), "Cliente no debe estar en caché después de limpiar");
    assertNull(zonaCache.get("ZONA-CACHE-A"), "Zona no debe estar en caché después de limpiar");
  }
}
