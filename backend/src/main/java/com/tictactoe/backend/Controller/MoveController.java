package com.tictactoe.backend.Controller;

import com.tictactoe.backend.Entity.Game;
import com.tictactoe.backend.Entity.Move;
import com.tictactoe.backend.Entity.Player;
import com.tictactoe.backend.Enum.Piece;
import com.tictactoe.backend.Repository.IGameRepository;
import com.tictactoe.backend.Repository.IMoveRepository;
import com.tictactoe.backend.Request.AddMoveRequest;
import com.tictactoe.backend.Service.MoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/move")
public class MoveController {

    @Autowired
    IMoveRepository moveRepository;

    @Autowired
    IGameRepository gameRepository;

    @Autowired
    MoveService moveService;

    //запрос на получение сделанных ходов в определенной игре
    @GetMapping(path = "/list")
    public List<Move> getMovesByGame(@RequestParam int gameId) {
        return moveRepository.findByGameOrderByIdAsc(gameRepository.findById(gameId));
    }

    //запрос на получение последнего символа (запрашивается в качестве обновления,
    // когда настала очередь ходить другому игроку
    @GetMapping(path="/last")
    public Move getLastPiece(@RequestParam int gameId) {
        return moveRepository.findTopByGameOrderByIdDesc(gameRepository.findById(gameId));
    }

    //возвращает http 200, если сейчас ход отправившего игрока
    //и http 406, если нет
    @GetMapping(path="/turn")
    public ResponseEntity<?> checkTurn(@RequestParam int gameId, HttpSession session) {
        Player sessionPlayer = (Player) session.getAttribute("player");
        Game currentGame = gameRepository.findById(gameId);
        Move lastMove = moveRepository.findTopByGameOrderByIdDesc(gameRepository.findById(gameId));
        int playerPlace = moveService.getPlayerPlace(sessionPlayer.getId(), currentGame.getFirstPlayer().getId(), currentGame.getSecondPlayer().getId());
        Piece newPiece = moveService.getNewPiece(playerPlace, currentGame.getFirstPlayerPiece(), currentGame.getSecondPlayerPiece());
        boolean isTurn = moveService.isSessionPlayerTurn(newPiece, lastMove);

        return isTurn ? new ResponseEntity<>(HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }

    //запрос на добавление X/O в поле
    @PostMapping(path = "/create")
    public ResponseEntity<?> createMove(@RequestBody AddMoveRequest addMoveRequest, HttpSession session) {
        //проверка, принадлежит ли игрок этой игре
        Player sessionPlayer = (Player)session.getAttribute("player");
        if (session.getId() == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Вы не вошли на стартовой странице.");
        }

        Game currentGame = gameRepository.findById(addMoveRequest.getGameId());
        //получаем, каким конкретно игроком является отправивший запрос игрок (нужно для определения символа)
        int sessionPlayerPlace = moveService.getPlayerPlace(sessionPlayer.getId(),
                currentGame.getFirstPlayer().getId(), currentGame.getSecondPlayer().getId());

        //если это наблюдатель
        if (sessionPlayerPlace == 3) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Вы наблюдатель, поэтому не можете ходить.");
        }

        //проверка, можно ли поставить символ
        boolean cantPlace = moveRepository.existsMoveByXAndY(addMoveRequest.getX(), addMoveRequest.getY());
        if (cantPlace) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Клетка занята.");
        }

        //если сейчас ход игрока, сделавшего запрос (прошлый символ противоположный)
        Move lastMove = moveRepository.findTopByGameOrderByIdDesc(currentGame);
        Piece newPiece = moveService.getNewPiece(sessionPlayerPlace, currentGame.getFirstPlayerPiece(), currentGame.getSecondPlayerPiece());
        boolean isSessionPlayerTurn = moveService.isSessionPlayerTurn(newPiece, lastMove);

        if (!isSessionPlayerTurn) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Сейчас не ваш ход.");
        }

        //все проверки пройдены, добавление символа
        Move newMove = new Move(
                currentGame,
                addMoveRequest.getX(),
                addMoveRequest.getY(),
                newPiece,
                sessionPlayer
        );
        moveRepository.save(newMove);
        return ResponseEntity.status(HttpStatus.OK).body(newMove);
    }
}
