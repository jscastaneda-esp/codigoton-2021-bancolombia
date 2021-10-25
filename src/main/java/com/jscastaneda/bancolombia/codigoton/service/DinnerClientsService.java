package com.jscastaneda.bancolombia.codigoton.service;

import com.jscastaneda.bancolombia.codigoton.dto.DeskInfo;
import com.jscastaneda.bancolombia.codigoton.dto.FilterClient;
import com.jscastaneda.bancolombia.codigoton.integration.rest.EvalartAppRestClient;
import com.jscastaneda.bancolombia.codigoton.persistent.dao.ClientDAO;
import com.jscastaneda.bancolombia.codigoton.persistent.model.ClientModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Clase para implementar las funcionalidades correspondientes al procesamiento del archivo y generar
 * el listado de clientes para la cena
 *
 * @author Jonathan Castañeda <jcaatanedaesp@gmail.com>
 * @since 23/10/2021
 */
@Service
public class DinnerClientsService {

    private static final String DESK_REGEX = "^(<[\\w\\s]*>)";
    private static final Short ONE = BigInteger.ONE.shortValue();

    @Autowired
    private ClientDAO clientDAO;

    @Autowired
    private EvalartAppRestClient evalartAppRestClient;

    /**
     * Metodo encargado de procesar el archivo y generar los organizar los clientes por mesa para la cena
     *
     * @param file (Archivo con los datos de entrada para la cena)
     * @return List<DeskInfo> (Listado de mesas con su informacion correspondiente)
     * @throws IOException
     */
    public List<DeskInfo> generateDinner(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            List<DeskInfo> dinner = new LinkedList<>();

            String line = reader.readLine();
            while (StringUtils.hasText(line)) {
                // Verificacion de los clientes para la mesa
                Pair<Optional<String>, Optional<DeskInfo>> pair = createDeskInfo(line, reader);

                line = null;
                if (pair.getFirst().isPresent()) {
                    line = pair.getFirst().get();
                }

                if (pair.getSecond().isPresent()) {
                    dinner.add(pair.getSecond().get());
                }
            }

            return dinner;
        }
    }

    /**
     * Metodo encargado de verificar la informacion para una mesa
     *
     * @param desk (Nombre de la mesa)
     * @param reader (Lector para el archivo)
     * @return Pair<Optional<String>, Optional<DeskInfo>> (Informacion asociada al procesamiento para la mesa)
     * @throws IOException
     */
    private Pair<Optional<String>, Optional<DeskInfo>> createDeskInfo(String desk, BufferedReader reader) throws IOException {
        if (desk.matches(DESK_REGEX)) {
            FilterClient.FilterClientBuilder filterClientBuilder = FilterClient.builder();

            // Verificacion de los filtros para busqueda de los clientes de la mesa
            String line;
            while ((line = reader.readLine()) != null) {
                if (StringUtils.hasText(line) && line.matches(DESK_REGEX)) {
                    break;
                }

                line = line.trim().replace(" ", "");
                if (StringUtils.hasText(line)) {
                    String[] pairsLine = line.split(":");
                    if (pairsLine.length == 2) {
                        if (pairsLine[0].equals("TC")) {
                            filterClientBuilder.type(Integer.parseInt(pairsLine[1]));
                        } else if (pairsLine[0].equals("UG")) {
                            filterClientBuilder.location(pairsLine[1]);
                        } else if (pairsLine[0].equals("RI")) {
                            filterClientBuilder.initialRangeBalance(Double.parseDouble(pairsLine[1]));
                        } else if (pairsLine[0].equals("RF")) {
                            filterClientBuilder.finalRangeBalance(Double.parseDouble(pairsLine[1]));
                        }
                    }
                }
            }

            // Generacion del listado de clientes para la mesa
            List<String> codeClients = generateCodeClients(filterClientBuilder.build());
            DeskInfo deskInfo = DeskInfo.builder()
                    .name(desk)
                    .codeClients(codeClients)
                    .canceled(CollectionUtils.isEmpty(codeClients))
                    .build();

            Optional<String> lineOpt = Optional.empty();
            if (line != null) {
                lineOpt = Optional.of(line);
            }

            return Pair.of(lineOpt, Optional.of(deskInfo));
        }

        String line = reader.readLine();
        Optional<String> lineOpt = Optional.empty();
        if (line != null) {
            lineOpt = Optional.of(line);
        }

        return Pair.of(lineOpt, Optional.empty());
    }

    /**
     * Metodo encargado de generar el listado de clientes para una mesa
     *
     * @param filterClient (Filtros para la busqueda de clientes)
     * @return List<String> (Listado de codigos de los clientes para la mesa)
     */
    private List<String> generateCodeClients(FilterClient filterClient) {
        // Busqueda de los clientes y validacion del requisito minimo para la verificacion de los clientes opcionados
        List<ClientModel> clients = clientDAO.findWithBalanceByFilter(filterClient);
        if (CollectionUtils.isEmpty(clients) || clients.size() < 4) {
            return null;
        }

        /*
        Validaciones principales:
        1. No se puede incluir mas de un cliente por compania
        2. Se valida que la cantidad de hombres y mujeres sea maximo de 4 clientes por cada uno
        3. El listado final de clientes no puede ser mayor a 8
         */
        int maleCount = 0;
        int femaleCount = 0;
        Set<String> companias = new HashSet<>();
        List<ClientModel> clientsTmp = new LinkedList<>();
        for (ClientModel client : clients) {
            if (!companias.contains(client.getCompany()) &&
                    ((client.getMale().equals(ONE) && maleCount < 4) || (!client.getMale().equals(ONE) && femaleCount < 4))) {

                clientsTmp.add(client);
                companias.add(client.getCompany());

                if (client.getMale().equals(ONE)) {
                    maleCount += 1;
                } else {
                    femaleCount += 1;
                }
            }

            if (clientsTmp.size() == 8) {
                break;
            }
        }

        // Se valida si la cantidad de hombres y mujeres es diferente para igualar la cantidad
        if (maleCount != femaleCount) {
            for (int i = clientsTmp.size() - 1; i >= 0; i--) {
                if (maleCount > femaleCount && clientsTmp.get(i).getMale().equals(ONE)) {
                    clientsTmp.remove(i);
                    maleCount -= 1;
                } else if (femaleCount > maleCount && !clientsTmp.get(i).getMale().equals(ONE)) {
                    clientsTmp.remove(i);
                    femaleCount -= 1;
                }
            }
        }

        // Si la cantidad final es menor a 4 se cancela la mesa
        if (clientsTmp.size() < 4) {
            return null;
        }

        // Se verifica si el codigo de los clientes esta encriptado para desencriptar por medio del servicio web
        return clientsTmp.stream().map(client -> {
            if (client.getEncrypt().equals(ONE)) {
                return evalartAppRestClient.invokeCodeDecrypt(client.getCode());
            }

            return client.getCode();
        }).collect(Collectors.toList());
    }
}
