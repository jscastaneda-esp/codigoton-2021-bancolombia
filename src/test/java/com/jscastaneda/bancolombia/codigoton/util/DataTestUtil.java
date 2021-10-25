package com.jscastaneda.bancolombia.codigoton.util;

import com.jscastaneda.bancolombia.codigoton.dto.FilterClient;
import com.jscastaneda.bancolombia.codigoton.persistent.model.ClientModel;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

public class DataTestUtil {

    private static final Short ZERO = BigInteger.ZERO.shortValue();
    private static final Short ONE = BigInteger.ONE.shortValue();

    public static File getFileIn() throws IOException {
        ClassPathResource resource = new ClassPathResource("entrada.txt");
        return resource.getFile();
    }

    public static File getFileInFailed() throws IOException {
        ClassPathResource resource = new ClassPathResource("entrada-failed.txt");
        return resource.getFile();
    }

    public static List<ClientModel> getClientModels(int desk) {
        List<ClientModel> clients = new LinkedList<>();
        if (desk == 0) {
            clients.add(new ClientModel(186, "C10186", ZERO, 11, "9", "37", ZERO, 1826764.0));
            clients.add(new ClientModel(116, "C10116", ZERO, 11, "5", "92", ZERO, 1189079.0));
            clients.add(new ClientModel(88, "C10088", ZERO, 11, "4", "27", ZERO, 1175414.0));
            clients.add(new ClientModel(196, "C10196", ZERO, 11, "8", "91", ZERO, 830376.0));
            clients.add(new ClientModel(169, "C10169", ONE, 11, "9", "89", ZERO, 595067.0));
            clients.add(new ClientModel(209, "QzEwMjA5", ONE, 11, "9", "1", ONE, 351058.0));
            clients.add(new ClientModel(178, "C10178", ONE, 11, "8", "88", ZERO, 128212.0));
        } else if (desk == 1) {
            clients.add(new ClientModel(144, "C10144", ZERO, 3, "1", "35", ZERO, 458575.0));
            clients.add(new ClientModel(70, "C10070", ONE, 2, "1", "79", ZERO, 237941.0));
            clients.add(new ClientModel(76, "C10076", ZERO, 1, "1", "33", ZERO, 234318.0));
            clients.add(new ClientModel(134, "C10134", ONE, 9, "1", "23", ZERO, 197401.0));
            clients.add(new ClientModel(151, "C10151", ZERO, 8, "1", "55", ZERO, 127257.0));
            clients.add(new ClientModel(90, "C10090", ONE, 2, "1", "82", ZERO, 88529.0));
        } else if (desk == 2) {
            clients.add(new ClientModel(201, "C10201", ONE, 12, "99", "5", ZERO, 0.0));
            clients.add(new ClientModel(202, "C10202", ONE, 12, "99", "1", ZERO, 0.0));
            clients.add(new ClientModel(203, "C10203", ONE, 12, "99", "2", ZERO, 0.0));
            clients.add(new ClientModel(204, "C10204", ONE, 12, "99", "3", ZERO, 0.0));
            clients.add(new ClientModel(205, "C10205", ZERO, 1, "99", "4", ZERO, 0.0));
            clients.add(new ClientModel(206, "C10206", ZERO, 1, "99", "6", ZERO, 0.0));
            clients.add(new ClientModel(207, "C10207", ZERO, 1, "99", "7", ZERO, 0.0));
            clients.add(new ClientModel(208, "C10208", ZERO, 1, "99", "8", ZERO, 0.0));
        }

        return clients;
    }
}
