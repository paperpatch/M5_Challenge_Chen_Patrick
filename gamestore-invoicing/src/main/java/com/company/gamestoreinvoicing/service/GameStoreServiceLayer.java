package com.company.gamestoreinvoicing.service;

import com.company.gamestoreinvoicing.model.*;
import com.company.gamestoreinvoicing.repository.InvoiceRepository;
import com.company.gamestoreinvoicing.repository.ProcessingFeeRepository;
import com.company.gamestoreinvoicing.repository.TaxRepository;
import com.company.gamestoreinvoicing.util.feign.CatalogClient;
import com.company.gamestoreinvoicing.viewModel.ConsoleViewModel;
import com.company.gamestoreinvoicing.viewModel.GameViewModel;
import com.company.gamestoreinvoicing.viewModel.InvoiceViewModel;
import com.company.gamestoreinvoicing.viewModel.TShirtViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class GameStoreServiceLayer {

    private final BigDecimal PROCESSING_FEE = new BigDecimal("15.49");
    private final BigDecimal MAX_INVOICE_TOTAL = new BigDecimal("999.99");
    private final String GAME_ITEM_TYPE = "Game";
    private final String CONSOLE_ITEM_TYPE = "Console";
    private final String TSHIRT_ITEM_TYPE = "T-Shirt";
    private final CatalogClient catalogClient;

    Tax[] states = new Tax[]{
            new Tax("AK",new BigDecimal("0.06")),
            new Tax("AL",new BigDecimal("0.05")),
            new Tax("AR",new BigDecimal("0.06")),
            new Tax("AZ",new BigDecimal("0.04")),
            new Tax("CA",new BigDecimal("0.06")),
            new Tax("CO",new BigDecimal("0.04")),
            new Tax("CT",new BigDecimal("0.03")),
            new Tax("DE",new BigDecimal("0.05")),
            new Tax("FL",new BigDecimal("0.06")),
            new Tax("GA",new BigDecimal("0.07")),
            new Tax("HI",new BigDecimal("0.05")),
            new Tax("IA",new BigDecimal("0.04")),
            new Tax("ID",new BigDecimal("0.03")),
            new Tax("IL",new BigDecimal("0.05")),
            new Tax("IN",new BigDecimal("0.05")),
            new Tax("KS",new BigDecimal("0.06")),
            new Tax("KY",new BigDecimal("0.04")),
            new Tax("LA",new BigDecimal("0.05")),
            new Tax("MA",new BigDecimal("0.05")),
            new Tax("MD",new BigDecimal("0.07")),
            new Tax("ME",new BigDecimal("0.03")),
            new Tax("MI",new BigDecimal("0.06")),
            new Tax("MN",new BigDecimal("0.06")),
            new Tax("MO",new BigDecimal("0.05")),
            new Tax("MS",new BigDecimal("0.05")),
            new Tax("MT",new BigDecimal("0.03")),
            new Tax("NC",new BigDecimal("0.05")),
            new Tax("ND",new BigDecimal("0.05")),
            new Tax("NE",new BigDecimal("0.04")),
            new Tax("NH",new BigDecimal("0.06")),
            new Tax("NJ",new BigDecimal("0.05")),
            new Tax("NM",new BigDecimal("0.05")),
            new Tax("NV",new BigDecimal("0.04")),
            new Tax("NY",new BigDecimal("0.06")),
            new Tax("OH",new BigDecimal("0.04")),
            new Tax("OK",new BigDecimal("0.04")),
            new Tax("OR",new BigDecimal("0.07")),
            new Tax("PA",new BigDecimal("0.06")),
            new Tax("RI",new BigDecimal("0.06")),
            new Tax("SC",new BigDecimal("0.06")),
            new Tax("SD",new BigDecimal("0.06")),
            new Tax("TN",new BigDecimal("0.05")),
            new Tax("TX",new BigDecimal("0.03")),
            new Tax("UT",new BigDecimal("0.04")),
            new Tax("VA",new BigDecimal("0.06")),
            new Tax("VT",new BigDecimal("0.07")),
            new Tax("WA",new BigDecimal("0.05")),
            new Tax("WI",new BigDecimal("0.03")),
            new Tax("WV",new BigDecimal("0.05")),
            new Tax("WY",new BigDecimal("0.04"))
    };

    InvoiceRepository invoiceRepo;
    TaxRepository taxRepo;
    ProcessingFeeRepository processingFeeRepo;

    @Autowired
    public GameStoreServiceLayer(CatalogClient catalogClient, InvoiceRepository invoiceRepo, TaxRepository taxRepo, ProcessingFeeRepository processingFeeRepo) {
        this.catalogClient = catalogClient;
        this.invoiceRepo = invoiceRepo;
        this.taxRepo = taxRepo;
        this.processingFeeRepo = processingFeeRepo;
    }

    public InvoiceViewModel createInvoice(InvoiceViewModel invoiceViewModel) {

        //validation...
        if (invoiceViewModel==null)
            throw new NullPointerException("Create invoice failed. no invoice data.");

        if(invoiceViewModel.getItemType()==null)
            throw new IllegalArgumentException("Unrecognized Item type. Valid ones: Console or Game");

        //Check Quantity is > 0...
        if(invoiceViewModel.getQuantity()<=0){
            throw new IllegalArgumentException(invoiceViewModel.getQuantity() +
                    ": Unrecognized Quantity. Must be > 0.");
        }

        //start building invoice...
        Invoice invoice = new Invoice();
        invoice.setName(invoiceViewModel.getName());
        invoice.setStreet(invoiceViewModel.getStreet());
        invoice.setCity(invoiceViewModel.getCity());
        invoice.setState(invoiceViewModel.getState());
        invoice.setZipcode(invoiceViewModel.getZipcode());
        invoice.setItemType(invoiceViewModel.getItemType());
        invoice.setItemId(invoiceViewModel.getItemId());

        //Checks the item type and get the correct unit price
        //Check if we have enough quantity
        if (invoiceViewModel.getItemType().equals(CONSOLE_ITEM_TYPE)) {
            Console tempCon = null;
            Optional<Console> returnVal = catalogClient.getConsoleById(invoiceViewModel.getItemId());

            if (returnVal.isPresent()) {
                tempCon = returnVal.get();
            } else {
                throw new IllegalArgumentException("Requested item is unavailable.");
            }

            if (invoiceViewModel.getQuantity()> tempCon.getQuantity()){
                throw new IllegalArgumentException("Requested quantity is unavailable.");
            }

            invoice.setUnitPrice(tempCon.getPrice());

        } else if (invoiceViewModel.getItemType().equals(GAME_ITEM_TYPE)) {
            Game tempGame = null;
            Optional<Game> returnVal = catalogClient.getGameById(invoiceViewModel.getItemId());

            if (returnVal.isPresent()) {
                tempGame = returnVal.get();
            } else {
                throw new IllegalArgumentException("Requested item is unavailable.");
            }

            if(invoiceViewModel.getQuantity() >  tempGame.getQuantity()){
                throw new IllegalArgumentException("Requested quantity is unavailable.");
            }
            invoice.setUnitPrice(tempGame.getPrice());

        } else if (invoiceViewModel.getItemType().equals(TSHIRT_ITEM_TYPE)) {
            TShirt tempTShirt = null;
            Optional<TShirt> returnVal = catalogClient.getTshirtById(invoiceViewModel.getItemId());

            if (returnVal.isPresent()) {
                tempTShirt = returnVal.get();
            } else {
                throw new IllegalArgumentException("Requested item is unavailable.");
            }

            if(invoiceViewModel.getQuantity() >  tempTShirt.getQuantity()){
                throw new IllegalArgumentException("Requested quantity is unavailable.");
            }
            invoice.setUnitPrice(tempTShirt.getPrice());

        } else {
            throw new IllegalArgumentException(invoiceViewModel.getItemType()+
                    ": Unrecognized Item type. Valid ones: T-Shirt, Console, or Game");
        }

        invoice.setQuantity(invoiceViewModel.getQuantity());

        invoice.setSubtotal(
                invoice.getUnitPrice().multiply(
                        new BigDecimal(invoiceViewModel.getQuantity())).setScale(2, RoundingMode.HALF_UP));

        //Throw Exception if subtotal is greater than 999.99
        if ((invoice.getSubtotal().compareTo(new BigDecimal(999.99)) > 0)) {
            throw new IllegalArgumentException("Subtotal exceeds maximum purchase price of $999.99");
        }

        //Validate State and Calc tax...
        BigDecimal tempTaxRate;
        Optional<Tax> returnVal = taxRepo.findById(invoice.getState());

        if (returnVal.isPresent()) {
            tempTaxRate = returnVal.get().getRate();
        } else {
            throw new IllegalArgumentException(invoice.getState() + ": Invalid State code.");
        }

        if (!tempTaxRate.equals(BigDecimal.ZERO))
            invoice.setTax(tempTaxRate.multiply(invoice.getSubtotal()));
        else
            throw new IllegalArgumentException( invoice.getState() + ": Invalid State code.");

        BigDecimal processingFee;
        Optional<ProcessingFee> returnVal2 = processingFeeRepo.findById(invoiceViewModel.getItemType());

        if (returnVal2.isPresent()) {
            processingFee = returnVal2.get().getFee();
        } else {
            throw new IllegalArgumentException("Requested item is unavailable.");
        }

        invoice.setProcessingFee(processingFee);

        //Checks if quantity of items if greater than 10 and adds additional processing fee
        if (invoiceViewModel.getQuantity() > 10) {
            invoice.setProcessingFee(invoice.getProcessingFee().add(PROCESSING_FEE));
        }

        invoice.setTotal(invoice.getSubtotal().add(invoice.getProcessingFee()).add(invoice.getTax()));

        //checks total for validation
        if ((invoice.getTotal().compareTo(MAX_INVOICE_TOTAL) > 0)) {
            throw new IllegalArgumentException("Subtotal exceeds maximum purchase price of $999.99");
        }

        invoice = invoiceRepo.save(invoice);

        return buildInvoiceViewModel(invoice);
    }

    public InvoiceViewModel getInvoice(long id) {
        Optional<Invoice> invoice = invoiceRepo.findById(id);
        if (invoice == null)
            return null;
        else
            return buildInvoiceViewModel(invoice.get());
    }

    public List<InvoiceViewModel> getAllInvoices() {
        List<Invoice> invoiceList = invoiceRepo.findAll();
        List<InvoiceViewModel> ivmList = new ArrayList<>();
        List<InvoiceViewModel> exceptionList = null;

        if (invoiceList == null) {
            return exceptionList;
        } else {
            invoiceList.stream().forEach(i -> {
                ivmList.add(buildInvoiceViewModel(i));
            });
        }
        return ivmList;
    }

    public List<InvoiceViewModel> getInvoicesByCustomerName(String name) {
        List<Invoice> invoiceList = invoiceRepo.findByName(name);
        List<InvoiceViewModel> ivmList = new ArrayList<>();
        List<InvoiceViewModel> exceptionList = null;

        if (invoiceList == null) {
            return exceptionList;
        } else {
            invoiceList.stream().forEach(i -> ivmList.add(buildInvoiceViewModel(i)));
        }
        return ivmList;
    }

    public void deleteInvoice(long id){
        invoiceRepo.deleteById(id);
    }

    //Game service layer...
    public GameViewModel createGame(GameViewModel gameViewModel) {

        // Validate incoming Game Data in the view model.
        // All validations were done using JSR303
        if (gameViewModel==null) throw new IllegalArgumentException("No Game is passed! Game object is null!");

        Game game = new Game();
        game.setTitle(gameViewModel.getTitle());
        game.setEsrbRating(gameViewModel.getEsrbRating());
        game.setDescription(gameViewModel.getDescription());
        game.setPrice(gameViewModel.getPrice());
        game.setQuantity(gameViewModel.getQuantity());
        game.setStudio(gameViewModel.getStudio());

        gameViewModel.setId(catalogClient.createGame(game).getId());
        return gameViewModel;
    }

    //CONSOLE SERVICE LAYER METHODS...
    public ConsoleViewModel createConsole(ConsoleViewModel consoleViewModel) {

        // Remember viewModel data was validated using JSR 303
        // Validate incoming Console Data in the view model
        if (consoleViewModel==null) throw new IllegalArgumentException("No Console is passed! Game object is null!");

        Console console = new Console();
        console.setModel(consoleViewModel.getModel());
        console.setManufacturer(consoleViewModel.getManufacturer());
        console.setMemoryAmount(consoleViewModel.getMemoryAmount());
        console.setProcessor(consoleViewModel.getProcessor());
        console.setPrice(consoleViewModel.getPrice());
        console.setQuantity(consoleViewModel.getQuantity());

        return buildConsoleViewModel(catalogClient.createConsole(console));
    }

    //TSHIRT SERVICE LAYER

    public TShirtViewModel createTShirt(TShirtViewModel tShirtViewModel) {

        // Remember model view has already been validated through JSR 303
        // Validate incoming TShirt Data in the view model
        if (tShirtViewModel==null) throw new IllegalArgumentException("No TShirt is passed! TShirt object is null!");

        TShirt tShirt = new TShirt();
        tShirt.setSize(tShirtViewModel.getSize());
        tShirt.setColor(tShirtViewModel.getColor());
        tShirt.setDescription(tShirtViewModel.getDescription());
        tShirt.setPrice(tShirtViewModel.getPrice());
        tShirt.setQuantity(tShirtViewModel.getQuantity());

        tShirt = catalogClient.createTshirt(tShirt);

        return buildTShirtViewModel(tShirt);
    }

    //Helper Methods...

    public ConsoleViewModel buildConsoleViewModel(Console console) {
        ConsoleViewModel consoleViewModel = new ConsoleViewModel();
        consoleViewModel.setId(console.getId());
        consoleViewModel.setModel(console.getModel());
        consoleViewModel.setManufacturer(console.getManufacturer());
        consoleViewModel.setMemoryAmount(console.getMemoryAmount());
        consoleViewModel.setProcessor(console.getProcessor());
        consoleViewModel.setPrice(console.getPrice());
        consoleViewModel.setQuantity(console.getQuantity());

        return consoleViewModel;
    }

    public GameViewModel buildGameViewModel(Game game) {

        GameViewModel gameViewModel = new GameViewModel();
        gameViewModel.setId(game.getId());
        gameViewModel.setTitle(game.getTitle());
        gameViewModel.setEsrbRating(game.getEsrbRating());
        gameViewModel.setDescription(game.getDescription());
        gameViewModel.setPrice(game.getPrice());
        gameViewModel.setStudio(game.getStudio());
        gameViewModel.setQuantity(game.getQuantity());

        return gameViewModel;
    }

    public TShirtViewModel buildTShirtViewModel(TShirt tShirt) {
        TShirtViewModel tShirtViewModel = new TShirtViewModel();
        tShirtViewModel.setId(tShirt.getId());
        tShirtViewModel.setSize(tShirt.getSize());
        tShirtViewModel.setColor(tShirt.getColor());
        tShirtViewModel.setDescription(tShirt.getDescription());
        tShirtViewModel.setPrice(tShirt.getPrice());
        tShirtViewModel.setQuantity(tShirt.getQuantity());

        return tShirtViewModel;
    }

    public InvoiceViewModel buildInvoiceViewModel(Invoice invoice) {
        InvoiceViewModel invoiceViewModel = new InvoiceViewModel();
        invoiceViewModel.setId(invoice.getId());
        invoiceViewModel.setName(invoice.getName());
        invoiceViewModel.setStreet(invoice.getStreet());
        invoiceViewModel.setCity(invoice.getCity());
        invoiceViewModel.setState(invoice.getState());
        invoiceViewModel.setZipcode(invoice.getZipcode());
        invoiceViewModel.setItemType(invoice.getItemType());
        invoiceViewModel.setItemId(invoice.getItemId());
        invoiceViewModel.setUnitPrice(invoice.getUnitPrice());
        invoiceViewModel.setQuantity(invoice.getQuantity());
        invoiceViewModel.setSubtotal(invoice.getSubtotal());
        invoiceViewModel.setProcessingFee(invoice.getProcessingFee());
        invoiceViewModel.setTax(invoice.getTax());
        invoiceViewModel.setProcessingFee(invoice.getProcessingFee());
        invoiceViewModel.setTotal(invoice.getTotal());

        return invoiceViewModel;
    }

    public ProcessingFee findProcessingFee(Invoice invoice) {
        return processingFeeRepo.findByProductType(invoice.getItemType());
    }

    public void seedFees(){
        processingFeeRepo.deleteAll();
        processingFeeRepo.save(new ProcessingFee("Consoles", new BigDecimal(14.99)));
        processingFeeRepo.save(new ProcessingFee("T-shirts", new BigDecimal(1.98)));
        processingFeeRepo.save(new ProcessingFee("Games", new BigDecimal(1.49)));
    }

    public Tax findTaxByState(String state) {
        return taxRepo.findByState(state);
    }

    public void seedTaxes() {
        taxRepo.deleteAll();
        for (Tax state : states) {
            taxRepo.save(state);
        }
    }
}
