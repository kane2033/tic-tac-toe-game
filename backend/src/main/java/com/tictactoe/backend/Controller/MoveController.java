package com.tictactoe.backend.Controller;

import com.tictactoe.backend.Entity.Move;
import com.tictactoe.backend.Repository.IGameRepository;
import com.tictactoe.backend.Repository.IMoveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        return moveRepository.findByGame(gameRepository.findById(gameId));
    }
}
