package com.tictactoe.backend.Repository;

import com.tictactoe.backend.Entity.Game;
import com.tictactoe.backend.Entity.Move;
import com.tictactoe.backend.Entity.Player;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IMoveRepository extends CrudRepository<Move, Long> {
    List<Move> findByGame(Game game);
    List<Move> findByGameOrderByIdAsc(Game game);
    boolean existsMoveByXAndY(int x, int y);
    Move findTopByGameOrderByIdDesc(Game game);
    List<Move> findByGameAndPlayer(Game game, Player player);
    int countByGameAndPlayer(Game game, Player player);
}
