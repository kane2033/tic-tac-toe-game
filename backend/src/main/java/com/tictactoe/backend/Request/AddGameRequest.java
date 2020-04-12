package com.tictactoe.backend.Request;

import com.tictactoe.backend.Enum.GameType;
import com.tictactoe.backend.Enum.Piece;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddGameRequest {

    private String userName;
    private GameType gameType;
    private Piece selectedPiece;
}
