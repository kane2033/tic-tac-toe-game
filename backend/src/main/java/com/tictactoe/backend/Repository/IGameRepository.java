package com.tictactoe.backend.Repository;

import com.tictactoe.backend.Entity.Game;
import com.tictactoe.backend.Enum.GameStatus;
import com.tictactoe.backend.Enum.GameType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IGameRepository extends CrudRepository<Game, Long> {
    //возвращает список игр по статусу(поиск игр со статусом "в прогрессе")
    List<Game> findByGameStatus(GameStatus gameStatus);

    List<Game> findTop50ByOrderByGameStatusDesc();

    Game findById(int id);
}
