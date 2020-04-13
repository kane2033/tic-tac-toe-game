package com.tictactoe.backend.Controller;

import com.tictactoe.backend.Entity.Game;
import com.tictactoe.backend.Entity.Player;
import com.tictactoe.backend.Enum.GameStatus;
import com.tictactoe.backend.Repository.IGameRepository;
import com.tictactoe.backend.Repository.IPlayerRepository;
import com.tictactoe.backend.Request.AddGameRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
        Player player = playerRepository.findOneByUserName(addGameRequest.getUserName());
        if (player == null) {
            player = new Player(addGameRequest.getUserName());
        }
        playerRepository.save(player);
        Game game = new Game(
                player,
                addGameRequest.getSelectedPiece(),
                addGameRequest.getGameType(),
                GameStatus.Waiting_Player2
        );
        gameRepository.save(game);
        //return ResponseEntity.status(HttpStatus.OK).body("Игра успешно создана!");
        return game;
    }

    //возвращает список игр, где ожидается игрок 2
    @RequestMapping( value = "/list", method = RequestMethod.GET, produces = "application/json")
    public List<Game> getWaitingGames() {
        return gameRepository.findByGameStatus(GameStatus.Waiting_Player2);
    }
}
