package com.jscastaneda.bancolombia.codigoton.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Clase para mapear los datos de los filtros para la busqueda de clientes
 *
 * @author Jonathan Castañeda <jcaatanedaesp@gmail.com>
 * @since 23/10/2021
 */
@Getter
@ToString
@Builder
public class FilterClient {

    private Integer type;
    private String location;
    private Double initialRangeBalance;
    private Double finalRangeBalance;
}
