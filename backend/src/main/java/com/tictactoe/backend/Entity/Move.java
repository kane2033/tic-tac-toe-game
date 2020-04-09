package com.tictactoe.backend.Entity;

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

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(name = "board_row", nullable = false)
    private int boardRow;

    @Column(name = "board_column", nullable = false)
    private int boardColumn;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;
}
