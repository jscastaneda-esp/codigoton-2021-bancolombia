package com.jscastaneda.bancolombia.codigoton.persistent.dao;

import com.jscastaneda.bancolombia.codigoton.dto.FilterClient;
import com.jscastaneda.bancolombia.codigoton.persistent.model.ClientModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Repositorio de persistencia para la entidad ClientEntity
 *
 * @author Jonathan Castañeda <jcaatanedaesp@gmail.com>
 * @since 23/10/2021
 */
@Repository
public class ClientDAO {

    private static final String PATTERN_REPLACE_WHERE = "${WHERE}";
    private static final String PARAMS_TYPE = "TYPE";
    private static final String PARAMS_LOCATION = "LOCATION";
    private static final String PARAMS_INITIAL_BALANCE = "INITIAL_BALANCE";
    private static final String PARAMS_FINAL_BALANCE = "FINAL_BALANCE";

    private static final String GET_CLIENTS_WITH_BALANCE = "SELECT T.*\n" +
            "FROM (\n" +
            "  SELECT C.*, SUM(A.balance) totalBalance\n" +
            "  FROM client C LEFT JOIN account A ON C.id = A.client_id\n" +
            "  GROUP BY C.id\n" +
            ") T\n" +
            PATTERN_REPLACE_WHERE + "\n" +
            "ORDER BY T.totalBalance DESC, T.code ASC";

    @Autowired
    private NamedParameterJdbcTemplate template;

    /**
     * Metodo encargado de consultar clientes por los filtros ingresados
     *
     * @param filterClient (Filtros para la busqueda de clientes)
     * @return List<ClientModel> (Lista de clientes encontrados)
     */
    public List<ClientModel> findWithBalanceByFilter(FilterClient filterClient) {
        // Construccion de la sentencia WHERE
        MapSqlParameterSource params = new MapSqlParameterSource();
        List<String> whereData = new ArrayList<>();

        if (Objects.nonNull(filterClient.getType())) {
            params.addValue(PARAMS_TYPE, filterClient.getType());
            whereData.add("T.type = :" + PARAMS_TYPE);
        }

        if (StringUtils.hasText(filterClient.getLocation())) {
            params.addValue(PARAMS_LOCATION, filterClient.getLocation());
            whereData.add("T.location = :" + PARAMS_LOCATION);
        }

        if (Objects.nonNull(filterClient.getInitialRangeBalance())) {
            params.addValue(PARAMS_INITIAL_BALANCE, filterClient.getInitialRangeBalance());
            whereData.add("T.totalBalance > :" + PARAMS_INITIAL_BALANCE);
        }

        if (Objects.nonNull(filterClient.getFinalRangeBalance())) {
            params.addValue(PARAMS_FINAL_BALANCE, filterClient.getFinalRangeBalance());
            whereData.add("T.totalBalance < :" + PARAMS_FINAL_BALANCE);
        }

        // Construccion de la sentencia SELECT
        String where = "";
        if (!whereData.isEmpty()) {
            where = "WHERE " + String.join(" AND ", whereData);
        }

        String sql = GET_CLIENTS_WITH_BALANCE.replace(PATTERN_REPLACE_WHERE, where);

        return template.query(sql, params, new BeanPropertyRowMapper<>(ClientModel.class));
    }
}
