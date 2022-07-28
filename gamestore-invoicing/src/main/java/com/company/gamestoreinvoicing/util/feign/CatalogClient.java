package com.company.gamestoreinvoicing.util.feign;

import com.company.gamestoreinvoicing.model.Console;
import com.company.gamestoreinvoicing.model.Game;
import com.company.gamestoreinvoicing.model.TShirt;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@FeignClient(name = "gamestore-catalog-service", url = "http://localhost:7474")
public interface CatalogClient {
    @GetMapping(value = "/console/{id}")
    Optional<Console> getConsoleById(@PathVariable long id);

    @PostMapping(value = "/console")
    public Console createConsole(Console console);

    @GetMapping(value = "/tshirt/{id}")
    Optional<TShirt> getTshirtById(@PathVariable long id);

    @PostMapping(value = "/tshirt")
    public TShirt createTshirt(TShirt tShirt);

    @GetMapping(value = "/game/{id}")
    Optional<Game> getGameById(@PathVariable long id);

    @PostMapping(value = "/game")
    public Game createGame(Game game);
}
