package com.tictactoe.backend.Controller;

import com.tictactoe.backend.Entity.Game;
import com.tictactoe.backend.Entity.Player;
import com.tictactoe.backend.Enum.GameStatus;
import com.tictactoe.backend.Repository.IGameRepository;
import com.tictactoe.backend.Repository.IPlayerRepository;
import com.tictactoe.backend.Request.AddGameRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game")
public class GameController {

    @Autowired
    IGameRepository gameRepository;

    @Autowired
    IPlayerRepository playerRepository;

    //@RequestMapping(value = "/create", method = { RequestMethod.POST, RequestMethod.GET })
    @PostMapping(path = "/create")
    public Game createNewGame(@RequestBody AddGameRequest addGameRequest) {
        Player player = new Player(addGameRequest.getUserName());
        //нужно добавить - если имя существует, то следует
        playerRepository.save(player);
        Game game = new Game(
                player,
                addGameRequest.getSelectedPiece(),
                addGameRequest.getGameType(),
                GameStatus.Waiting_Player2
        );
        gameRepository.save(game);
        return game;
    }
}
