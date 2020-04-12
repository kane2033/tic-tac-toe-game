package com.tictactoe.backend.Entity;

import com.tictactoe.backend.Enum.GameStatus;
import com.tictactoe.backend.Enum.GameType;
import com.tictactoe.backend.Enum.Piece;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;

import javax.persistence.*;

@Entity
@Getter
@Setter
//@Check(constraints = "first_player_piece_code = 'O' or first_player_piece_code = 'X' " +
//        "and game_type = 'VS_Computer' or game_type = 'VS_Player' " +
//        "and game_status = 'In_Progress' or game_status = 'Player1_Won' or game_status = 'Player2_Won'" +
//        "or game_status = 'Tie' or game_status = 'Waiting_Player2' ")
@NoArgsConstructor
@AllArgsConstructor
public class Game {

    public Game(Player firstPlayer, Piece firstPlayerPieceCode, GameType gameType, GameStatus gameStatus) {
        this.firstPlayer = firstPlayer;
        this.firstPlayerPieceCode = firstPlayerPieceCode;
        this.gameType = gameType;
        this.gameStatus = gameStatus;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "first_player_id", nullable = false)
    private Player firstPlayer;

    @ManyToOne
    @JoinColumn(name = "second_player_id")
    private Player secondPlayer;

    @Enumerated(EnumType.STRING)
    private Piece firstPlayerPieceCode;

    @Enumerated(EnumType.STRING)
    private GameType gameType;

    @Enumerated(EnumType.STRING)
    private GameStatus gameStatus;

}
