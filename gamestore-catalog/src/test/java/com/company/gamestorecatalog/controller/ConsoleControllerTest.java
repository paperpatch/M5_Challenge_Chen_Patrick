package com.company.gamestorecatalog.controller;

import com.company.gamestorecatalog.service.GameStoreServiceLayer;
import com.company.gamestorecatalog.viewModel.ConsoleViewModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(ConsoleController.class)
public class ConsoleControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    GameStoreServiceLayer service;

    private ConsoleViewModel inputConsole;
    private String inputConsoleJsonString;
    private ConsoleViewModel outputConsole;
    private String outputConsoleJsonString;
    private List<ConsoleViewModel> consoleViewModelList;
    private String outputConsoleListJsonString;
    private static final long CONSOLE_ID = 42;

    ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {
        inputConsole = new ConsoleViewModel();
        inputConsole.setModel("PS5");
        inputConsole.setManufacturer("Sony");
        inputConsole.setPrice(new BigDecimal(599.99));
        inputConsole.setQuantity(78);
        inputConsole.setProcessor("AMD");
        inputConsole.setMemoryAmount("1 TB");

        outputConsole = new ConsoleViewModel();
        outputConsole.setModel("PS5");
        outputConsole.setManufacturer("Sony");
        outputConsole.setPrice(new BigDecimal(599.99));
        outputConsole.setQuantity(78);
        outputConsole.setProcessor("AMD");
        outputConsole.setMemoryAmount("1 TB");

        consoleViewModelList = Arrays.asList(outputConsole);

        inputConsoleJsonString = objectMapper.writeValueAsString(inputConsole);
        outputConsoleJsonString = objectMapper.writeValueAsString(outputConsole);
        outputConsoleListJsonString = objectMapper.writeValueAsString(consoleViewModelList);

        when(service.getAllConsoles()).thenReturn(consoleViewModelList);
        when(service.createConsole(inputConsole)).thenReturn(outputConsole);
        when(service.getConsoleById(CONSOLE_ID)).thenReturn(outputConsole);
    }

    
}