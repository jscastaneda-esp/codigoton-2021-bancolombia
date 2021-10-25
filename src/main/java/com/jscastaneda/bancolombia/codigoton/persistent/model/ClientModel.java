package com.jscastaneda.bancolombia.codigoton.persistent.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Clase para el mapeo de la tabla 'client' de la base de datos
 *
 * @author Jonathan Castañeda <jcaatanedaesp@gmail.com>
 * @since 23/10/2021
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientModel implements Serializable {

    private Integer id;
    private String code;
    private Short male;
    private Integer type;
    private String location;
    private String company;
    private Short encrypt;
    private Double totalBalance;
}
