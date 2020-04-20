package com.tictactoe.backend.Controller;

import com.tictactoe.backend.Entity.Game;
import com.tictactoe.backend.Entity.Move;
import com.tictactoe.backend.Entity.Player;
import com.tictactoe.backend.Repository.IGameRepository;
import com.tictactoe.backend.Repository.IMoveRepository;
import com.tictactoe.backend.Request.AddMoveRequest;
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

    //запрос на получение сделанных ходов в определенной игре
    @GetMapping(path = "/list")
    public List<Move> getMovesByGame(@RequestParam int gameId) {
        return moveRepository.findByGameOrderByIdAsc(gameRepository.findById(gameId));
    }

    //запрос на добавление X/O в поле
    @PostMapping(path = "/create")
    public ResponseEntity<?> createMove(@RequestBody AddMoveRequest addMoveRequest, HttpSession session) {
        //проверка, можно ли поставить символ
        boolean cantPlace = moveRepository.existsMoveByXAndY(addMoveRequest.getX(), addMoveRequest.getY());
        if (cantPlace) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Клетка занята.");
        }

        Game currentGame = gameRepository.findById(addMoveRequest.getGameId());
        Move lastMove = moveRepository.findTopByGameOrderByIdDesc(currentGame);
        if (lastMove != null) { //равно null, если игра новая и поле пустое
            //если сейчас ход игрока, сделавшего запрос (прошлый символ противоположный)
            if (lastMove.getPiece() == addMoveRequest.getPiece()) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Сейчас не ваш ход.");
            }
        }


        //проверка, принадлежит ли игрок этой игре
        Player sessionPlayer = (Player)session.getAttribute("player");
        if (sessionPlayer.getId() != currentGame.getFirstPlayer().getId() &&
                sessionPlayer.getId() != currentGame.getSecondPlayer().getId()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Вы наблюдатель, поэтому не можете ходить.");
        }

        //все проверки пройдены, добавление символа
        Move newMove = new Move(
                currentGame,
                addMoveRequest.getX(),
                addMoveRequest.getY(),
                addMoveRequest.getPiece(),
                sessionPlayer
        );
        moveRepository.save(newMove);
        return ResponseEntity.status(HttpStatus.OK).body(newMove);
    }
}
