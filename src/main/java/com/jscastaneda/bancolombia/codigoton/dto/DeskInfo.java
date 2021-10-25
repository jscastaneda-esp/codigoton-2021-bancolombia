package com.jscastaneda.bancolombia.codigoton.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * Clase para el mapeo de los datos de una mesa para la cena
 *
 * @author Jonathan Castañeda <jcaatanedaesp@gmail.com>
 * @since 23/10/2021
 */
@Getter
@ToString
@Builder
public class DeskInfo {

    private String name;
    private List<String> codeClients;
    private boolean canceled;
}
