package com.josue.pedidos_ms.infrastructure.config;

import com.josue.pedidos_ms.domain.model.Cliente;
import com.josue.pedidos_ms.domain.model.Zona;
import com.josue.pedidos_ms.infrastructure.repository.ClienteRepository;
import com.josue.pedidos_ms.infrastructure.repository.ZonaRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

  @Autowired
  private ClienteRepository clienteRepository;

  @Autowired
  private ZonaRepository zonaRepository;

  @PostConstruct
  public void initializeData() {
    // Insertar clientes de prueba
    if (clienteRepository.count() == 0) {
      clienteRepository.save(Cliente.builder()
          .id("CLI-123")
          .nombre("Juan Pérez")
          .build());

      clienteRepository.save(Cliente.builder()
          .id("CLI-999")
          .nombre("María García")
          .build());
    }

    // Insertar zonas de prueba
    if (zonaRepository.count() == 0) {
      zonaRepository.save(Zona.builder()
          .id("ZONA1")
          .soporteRefrigeracion(true)
          .build());

      zonaRepository.save(Zona.builder()
          .id("ZONA5")
          .soporteRefrigeracion(false)
          .build());
    }
  }
}
