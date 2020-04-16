package com.tictactoe.backend.Controller;

import com.tictactoe.backend.Entity.Game;
import com.tictactoe.backend.Entity.Player;
import com.tictactoe.backend.Enum.GameStatus;
import com.tictactoe.backend.Repository.IGameRepository;
import com.tictactoe.backend.Repository.IPlayerRepository;
import com.tictactoe.backend.Request.AddGameRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/game")
public class GameController {

    @Autowired
    IGameRepository gameRepository;

    @Autowired
    IPlayerRepository playerRepository;

    //создается игра от имени игрока сессии с параметрами, переданными в пост запрос
    @PostMapping(path = "/create")
    public ResponseEntity<?> createNewGame(@RequestBody AddGameRequest addGameRequest, HttpSession session) {
        Player player = (Player)session.getAttribute("player");
        if (player == null) { //если
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Имя не введено");
        }
        Game game = new Game(
                player,
                addGameRequest.getSelectedPiece(),
                addGameRequest.getGameType(),
                GameStatus.Waiting_Player2
        );
        gameRepository.save(game);
        return ResponseEntity.status(HttpStatus.OK).body("Игра успешно создана!");
    }

    //возвращает список игр, где ожидается игрок 2
    @RequestMapping( value = "/list", method = RequestMethod.GET, produces = "application/json")
    public List<Game> getWaitingGames() {
        return gameRepository.findByGameStatus(GameStatus.Waiting_Player2);
    }
}
