package com.jscastaneda.bancolombia.codigoton;

import com.jscastaneda.bancolombia.codigoton.dto.DeskInfo;
import com.jscastaneda.bancolombia.codigoton.service.DinnerClientsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.*;
import java.util.List;

/**
 * @author Jonathan Castañeda <jcaatanedaesp@gmail.com>
 * @since 23/10/2021
 */
@Service
public class InitApplication implements CommandLineRunner {

    @Autowired
    private DinnerClientsService service;

    @Override
    public void run(String... args) throws Exception {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.print("Ingrese la ruta del archivo a procesar (entrada.txt): ");
            String filename = br.readLine();

            /*
             Por defecto si no ingresa la ruta del archivo se toma un archivo con el nombre 'entrada.txt'
             en la ubicacion en donde se ejecute el programa
             */
            if (ObjectUtils.isEmpty(filename)) {
                filename = "entrada.txt";
            }

            // Se ejecuta la generacion de la cena para los clientes y se guardar el resultado
            File fileIn = new File(filename);
            List<DeskInfo> dinner = service.generateDinner(fileIn);

            File fileOut = new File(fileIn.getAbsoluteFile().getParent(), "salida.txt");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileOut))) {
                for (DeskInfo deskInfo : dinner) {
                    writer.write(deskInfo.getName());
                    writer.newLine();

                    if (deskInfo.isCanceled()) {
                        writer.write("CANCELADA");
                        writer.newLine();
                    } else {
                        writer.write(String.join(",", deskInfo.getCodeClients()));
                        writer.newLine();
                    }
                }
            }

            System.out.print("El resultado se ha guardado en el archivo: ");
            System.out.println(fileOut.getAbsolutePath());
        }
    }
}
