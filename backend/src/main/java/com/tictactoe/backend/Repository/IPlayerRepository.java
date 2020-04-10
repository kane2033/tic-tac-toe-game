package com.tictactoe.backend.Repository;

import com.tictactoe.backend.Entity.Player;
import org.springframework.data.repository.CrudRepository;

public interface IPlayerRepository extends CrudRepository<Player, Long> {
    //поиск игрока по имени - нужно для проверки наличия игрока
    Player findOneByUserName(String userName);
}
