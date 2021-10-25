package com.jscastaneda.bancolombia.codigoton.service;

import com.jscastaneda.bancolombia.codigoton.dto.DeskInfo;
import com.jscastaneda.bancolombia.codigoton.dto.FilterClient;
import com.jscastaneda.bancolombia.codigoton.integration.rest.EvalartAppRestClient;
import com.jscastaneda.bancolombia.codigoton.persistent.dao.ClientDAO;
import com.jscastaneda.bancolombia.codigoton.persistent.model.ClientModel;
import com.jscastaneda.bancolombia.codigoton.util.DataTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DinnerClientsServiceTest {

    @Mock
    private ClientDAO clientDAO;

    @Mock
    private EvalartAppRestClient evalartAppRestClient;

    @InjectMocks
    private DinnerClientsService service;

    @BeforeEach
    void initTest() {
        reset(clientDAO, evalartAppRestClient);
    }

    @Test
    void generateDinner_success_test() throws IOException {
        File fileIn = DataTestUtil.getFileIn();
        List<ClientModel> clientsGeneral = DataTestUtil.getClientModels(0);
        List<ClientModel> clientsDesk1 = DataTestUtil.getClientModels(1);
        List<ClientModel> clientsDesk2 = DataTestUtil.getClientModels(2);

        when(clientDAO.findWithBalanceByFilter(isA(FilterClient.class)))
                .thenReturn(clientsGeneral)
                .thenReturn(clientsDesk1)
                .thenReturn(clientsDesk2);
        when(evalartAppRestClient.invokeCodeDecrypt("QzEwMjA5"))
                .thenReturn("C10209");

        List<DeskInfo> dinner = service.generateDinner(fileIn);

        verify(clientDAO, times(3)).findWithBalanceByFilter(isA(FilterClient.class));
        verify(evalartAppRestClient).invokeCodeDecrypt(isA(String.class));
        assertFalse(CollectionUtils.isEmpty(dinner));
        assertEquals(3, dinner.size());

        boolean deskGeneral = false;
        boolean desk1 = false;
        boolean desk2 = false;
        for (DeskInfo deskInfo : dinner) {
            if (deskInfo.getName().equals("<General>") && String.join(",", deskInfo.getCodeClients()).equals("C10186,C10116,C10088,C10169,C10209,C10178")) {
                deskGeneral = true;
            } else if (deskInfo.getName().equals("<Mesa 1>") && String.join(",", deskInfo.getCodeClients()).equals("C10144,C10070,C10076,C10134,C10151,C10090")) {
                desk1 = true;
            } else if (deskInfo.getName().equals("<Mesa 2>") && String.join(",", deskInfo.getCodeClients()).equals("C10201,C10202,C10203,C10204,C10205,C10206,C10207,C10208")) {
                desk2 = true;
            }
        }

        assertTrue(deskGeneral);
        assertTrue(desk1);
        assertTrue(desk2);
    }

    @Test
    void generateDinner_structureFileInvalid_test() throws IOException {
        File fileIn = DataTestUtil.getFileInFailed();

        List<DeskInfo> dinner = service.generateDinner(fileIn);

        assertTrue(CollectionUtils.isEmpty(dinner));
    }

    @Test
    void generateDinner_unknownError_test1() throws IOException {
        File fileIn = DataTestUtil.getFileIn();

        when(clientDAO.findWithBalanceByFilter(isA(FilterClient.class)))
                .thenThrow(RuntimeException.class);

        try {
            service.generateDinner(fileIn);
        } catch (RuntimeException e) {
            verify(clientDAO).findWithBalanceByFilter(isA(FilterClient.class));

            assertThrows(RuntimeException.class, () -> {
                throw e;
            });
        }
    }

    @Test
    void generateDinner_unknownError_test2() throws IOException {
        File fileIn = DataTestUtil.getFileIn();
        List<ClientModel> clientsGeneral = DataTestUtil.getClientModels(0);
        List<ClientModel> clientsDesk1 = DataTestUtil.getClientModels(1);

        when(clientDAO.findWithBalanceByFilter(isA(FilterClient.class)))
                .thenReturn(clientsDesk1)
                .thenReturn(clientsGeneral);
        when(evalartAppRestClient.invokeCodeDecrypt("QzEwMjA5"))
                .thenThrow(RestClientException.class);

        try {
            service.generateDinner(fileIn);
        } catch (RuntimeException e) {
            verify(clientDAO, times(2)).findWithBalanceByFilter(isA(FilterClient.class));
            verify(evalartAppRestClient).invokeCodeDecrypt(isA(String.class));

            assertThrows(RestClientException.class, () -> {
                throw e;
            });
        }

    }
}
