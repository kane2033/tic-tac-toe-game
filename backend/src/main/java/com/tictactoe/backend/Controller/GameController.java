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
        //если игрок еще не зашел в сессию
        if (player == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Имя не введено");
        }
        //создание игры при наличии игрока в сессии
        Game game = new Game(
                player,
                addGameRequest.getSelectedPiece(),
                addGameRequest.getGameType(),
                GameStatus.Waiting_Player2
        );
        gameRepository.save(game);
        return ResponseEntity.status(HttpStatus.OK).body("Игра успешно создана!");
    }

    //возвращает все игры
    @RequestMapping(path = "/list", method = RequestMethod.GET, produces = "application/json")
    public List<Game> getWaitingGames() {
        return gameRepository.findTop50ByOrderByGameStatusDesc();
    }

    //запрос на вход в игру
    @GetMapping(path = "/join")
    public ResponseEntity<?> joinGame(@RequestParam int gameId, HttpSession session) {
        Player sessionPlayer = (Player) session.getAttribute("player");
        //если игрок еще не зашел в сессию
        if (sessionPlayer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Имя не введено");
        }

        Game selectedGame = gameRepository.findById(gameId);
        Player firstPlayerFromGame = selectedGame.getFirstPlayer();
        Player secondPlayerFromGame = selectedGame.getSecondPlayer();

        //если нет второго игрока
        if (selectedGame.getGameStatus() == GameStatus.Waiting_Player2) {
            selectedGame.setSecondPlayer(sessionPlayer);
            selectedGame.setGameStatus(GameStatus.In_Progress);
            gameRepository.save(selectedGame);
            return  ResponseEntity.status(HttpStatus.OK).body("Вы успешно присоединились к игре №" + selectedGame.getId());
        }

        //если игрок перезаходит в игру
        if (sessionPlayer.getId() == firstPlayerFromGame.getId() ||
                sessionPlayer.getId() == secondPlayerFromGame.getId()) {
            return ResponseEntity.status(HttpStatus.OK).body("Вы перезашли в игру №" + selectedGame.getId());
        }

        //если есть игрок, который хочет присоединиться к игре в процессе и не считается как игрок,
        //он добавляется как наблюдатель
        return ResponseEntity.status(HttpStatus.OK).body("Вы зашли как наблюдатель в игру №" + selectedGame.getId());
    }
}
