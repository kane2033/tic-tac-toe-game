package com.tictactoe.backend.Entity;

import com.tictactoe.backend.Enum.Piece;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Move {

    public Move(Game game, int x, int y, Piece piece, Player player) {
        this.game = game;
        this.x = x;
        this.y = y;
        this.piece = piece;
        this.player = player;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(name = "x", nullable = false)
    private int x;

    @Column(name = "y", nullable = false)
    private int y;

    @Enumerated(EnumType.STRING)
    @Column(name = "piece", nullable = false)
    private Piece piece;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;
}
