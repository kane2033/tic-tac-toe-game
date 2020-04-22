package com.tictactoe.backend.Service;

import com.tictactoe.backend.Entity.Game;
import com.tictactoe.backend.Entity.Move;
import com.tictactoe.backend.Enum.Piece;
import com.tictactoe.backend.Repository.IMoveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MoveService {

    private final IMoveRepository moveRepository;

    @Autowired
    public MoveService(IMoveRepository moveRepository) {
        this.moveRepository = moveRepository;
    }

    private Piece[][] movesToPieces(List<Move> moves) {
        Piece[][] pieces = new Piece[19][19];
        for (Move move : moves) {
            pieces[move.getX()][move.getY()] = move.getPiece();
        }
        return pieces;
    }

    private List<Move> getMovesByGame(Game game) {
        return moveRepository.findByGameOrderByIdAsc(game);
    }

    public Piece[][] getPiecesByGame(Game game) {
        return movesToPieces(getMovesByGame(game));
    }

}
