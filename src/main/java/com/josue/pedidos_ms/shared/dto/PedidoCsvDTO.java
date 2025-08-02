package com.josue.pedidos_ms.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoCsvDTO {

  private String numeroPedido;
  private String clienteId;
  private String fechaEntrega; // Formato: yyyy-MM-dd
  private String estado; // PENDIENTE, CONFIRMADO, ENTREGADO
  private String zonaEntrega;
  private String requiereRefrigeracion; // true / false
}
