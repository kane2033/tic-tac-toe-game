package com.tictactoe.backend.Controller;

import com.tictactoe.backend.Entity.Game;
import com.tictactoe.backend.Entity.Player;
import com.tictactoe.backend.Enum.GameStatus;
import com.tictactoe.backend.Enum.Piece;
import com.tictactoe.backend.Repository.IGameRepository;
import com.tictactoe.backend.Repository.IPlayerRepository;
import com.tictactoe.backend.Request.AddGameRequest;
import com.tictactoe.backend.Request.AddMoveRequest;
import com.tictactoe.backend.Service.GameService;
import com.tictactoe.backend.Service.MoveService;
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

    @Autowired
    GameService gameService;

    @Autowired
    MoveService moveService;

//    @Autowired
//    IMoveRepository moveRepository;
//
//    @Autowired
//    GameService gameService = new GameService(gameRepository);
//
//    @Autowired
//    MoveService moveService = new MoveService(moveRepository);

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
                addGameRequest.getSelectedPiece(), //символ игрока 1
                addGameRequest.getSelectedPiece() == Piece.X ? Piece.O : Piece.X, //символ игрока 2
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

        Game selectedGame = gameService.getGameById(gameId);
        Player firstPlayerFromGame = selectedGame.getFirstPlayer();
        Player secondPlayerFromGame = selectedGame.getSecondPlayer();

        //если нет второго игрока
        if (selectedGame.getGameStatus() == GameStatus.Waiting_Player2) {
            if (firstPlayerFromGame.getId() == sessionPlayer.getId()) {
                return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Второй игрок пока не подключился к игре №" + selectedGame.getId());
            }
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

    //запрос на получение сущности игры
    @GetMapping(path = "/")
    public Game getGameById(@RequestParam int gameId) {
        return gameService.getGameById(gameId);
    }

    //запрос на поиск победителя
    @PostMapping(path = "/winner")
    public Piece getWinner(@RequestBody AddMoveRequest checkMoveRequest) {
        Game game = gameService.getGameById(checkMoveRequest.getGameId());
        Piece[][] pieces = moveService.getPiecesByGame(game);
        Piece winner = gameService.calculateWinner(pieces, checkMoveRequest.getX(), checkMoveRequest.getY());
        if (winner != null) { //если есть победитель, статус игры меняется
            GameStatus gameWinnerStatus = winner == game.getFirstPlayerPiece() ? GameStatus.Player1_Won :
                    GameStatus.Player2_Won;
            game.setGameStatus(gameWinnerStatus);
            gameRepository.save(game);
        }
        return gameService.calculateWinner(pieces, checkMoveRequest.getX(), checkMoveRequest.getY());
    }

    @GetMapping(path = "/draw")
    public boolean isDraw(@RequestParam int gameId) {
        Game game = gameService.getGameById(gameId);
        Piece[][] pieces = moveService.getPiecesByGame(game);
        boolean isDraw = gameService.isDraw(pieces);
        return isDraw;
    }
}
