package com.tictactoe.backend.Entities;

import com.tictactoe.backend.Enums.GameStatus;
import com.tictactoe.backend.Enums.GameType;
import com.tictactoe.backend.Enums.Piece;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Check(constraints = "first_player_piece_code = 'O' or first_player_piece_code = 'X' " +
        "and game_type = 'VS_Компьютер' or game_type = 'VS_Игрок' " +
        "and game_status = 'В_процессе' or game_status = 'Игрок1_победил' or game_status = 'Игрок2_победил'" +
        "or game_status = 'Ничья' or game_status = 'Ожидание_игрока' ")
@NoArgsConstructor
@AllArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "first_player_id", nullable = false)
    private Player firstPlayer;

    @ManyToOne
    @JoinColumn(name = "second_player_id", nullable = true)
    private Player secondPlayer;

    @Enumerated(EnumType.STRING)
    private Piece firstPlayerPieceCode;

    @Enumerated(EnumType.STRING)
    private GameType gameType;

    @Enumerated(EnumType.STRING)
    private GameStatus gameStatus;
}
