package com.tictactoe.backend.Controller;

import com.tictactoe.backend.Entity.Player;
import com.tictactoe.backend.Repository.IPlayerRepository;
import com.tictactoe.backend.Request.AddPlayerRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/player")
public class PlayerController {

    @Autowired
    IPlayerRepository playerRepository;

    @Autowired
    HttpSession newSession;

    //вход с использованием юзернейма - имя запоминается в сессии
    @PostMapping(path = "/login")
    public ResponseEntity<?> createNewPlayer(@RequestBody AddPlayerRequest addPlayerRequest, HttpServletRequest request) {
        Player player = playerRepository.findOneByUserName(addPlayerRequest.getUserName());
        if (player == null) { //если введенного юзернейма не существует, создается новый пользователь
            player = new Player(addPlayerRequest.getUserName());
            playerRepository.save(player);
        }
        System.out.println(player.getUserName());
        request.getSession().setAttribute("player", player);
        return ResponseEntity.status(HttpStatus.OK).body("Игрок создан в сессии: " + request.getSession().getId());
    }
}
