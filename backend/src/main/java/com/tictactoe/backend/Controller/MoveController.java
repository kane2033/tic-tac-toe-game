package com.tictactoe.backend.Controller;

import com.tictactoe.backend.Entity.Game;
import com.tictactoe.backend.Entity.Move;
import com.tictactoe.backend.Entity.Player;
import com.tictactoe.backend.Enum.Piece;
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
        //проверка, принадлежит ли игрок этой игре
        Player sessionPlayer = (Player)session.getAttribute("player");
        if (session.getId() == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Вы не вошли на стартовой странице.");
        }

        Game currentGame = gameRepository.findById(addMoveRequest.getGameId());
        //получаем, каким конкретно игроком является отправивший запрос игрок (нужно для определения символа)
        int sessionPlayerPlace = sessionPlayer.getId() == currentGame.getFirstPlayer().getId() ? 1 :
                sessionPlayer.getId() == currentGame.getSecondPlayer().getId() ? 2: 3;

        //если это наблюдатель
        if (sessionPlayerPlace == 3) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Вы наблюдатель, поэтому не можете ходить.");
        }

        //проверка, можно ли поставить символ
        boolean cantPlace = moveRepository.existsMoveByXAndY(addMoveRequest.getX(), addMoveRequest.getY());
        if (cantPlace) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Клетка занята.");
        }


        Move lastMove = moveRepository.findTopByGameOrderByIdDesc(currentGame);
        Piece newPiece = sessionPlayerPlace == 1 ? currentGame.getFirstPlayerPiece() : currentGame.getSecondPlayerPiece();
        Piece lastPiece = lastMove == null ? Piece.O : lastMove.getPiece(); // lastPiece = X, когда это первый ход и lastMove == null

        System.out.println("lastPiece = " + lastPiece);
        //если сейчас ход игрока, сделавшего запрос (прошлый символ противоположный)
        if (lastPiece == newPiece) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Сейчас не ваш ход.");
        }

        System.out.println("newPiece = " + newPiece);
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
