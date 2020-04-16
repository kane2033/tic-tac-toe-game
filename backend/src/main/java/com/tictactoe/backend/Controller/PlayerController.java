package com.tictactoe.backend.Controller;

import com.tictactoe.backend.Entity.Player;
import com.tictactoe.backend.Repository.IPlayerRepository;
import com.tictactoe.backend.Repository.ISpringSessionAttributesRepository;
import com.tictactoe.backend.Request.AddPlayerRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.SerializationUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/player")
public class PlayerController {

    @Autowired
    IPlayerRepository playerRepository;

    @Autowired
    ISpringSessionAttributesRepository sessionAttributesRepository;

    //вход с использованием юзернейма - имя запоминается в сессии
    @PostMapping(path = "/login")
    public ResponseEntity<?> createNewPlayer(@RequestBody AddPlayerRequest addPlayerRequest, HttpServletRequest request) {
        //если введенного юзернейма не существует, в бд создается новый пользователь и добавляется в сессию
        Player player = playerRepository.findOneByUserName(addPlayerRequest.getUserName());
        if (player == null) {
            player = new Player(addPlayerRequest.getUserName());
            playerRepository.save(player);
            request.getSession().setAttribute("player", player);
            return ResponseEntity.status(HttpStatus.OK).body("Новый игрок добавлен и создан в сессии: " + request.getSession().getId());
        }
        //если юзер уже зашел под именем, которое отправляется в запросе, возвращается ошибка
        Player currentPlayer = (Player)request.getSession().getAttribute("player");
        if (currentPlayer.getId() == player.getId()) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Вы уже зашли под этим именем. Ваша сессия: " + request.getSession().getId());
        }

        //если есть другая сессия с таким именем, возвращается ошибка
        byte[] bytePlayer = SerializationUtils.serialize(player);
        boolean exists = sessionAttributesRepository.existsSpringSessionAttributesEntityByAttributeBytes(bytePlayer);
        if (exists) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Кто-то уже зашел под этим именем. Ваша сессия: " + request.getSession().getId());
        }
        request.getSession().setAttribute("player", player);
        return ResponseEntity.status(HttpStatus.OK).body("Игрок создан в сессии: " + request.getSession().getId());
    }
}
