package com.tictactoe.backend.Service;

import com.tictactoe.backend.Entity.Game;
import com.tictactoe.backend.Enum.Piece;
import com.tictactoe.backend.Repository.IGameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    private final IGameRepository gameRepository;

    @Autowired
    public GameService(IGameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Game getGameById(int gameId) {
        return gameRepository.findById(gameId);
    }

    //проверка победителя:
    //если таковой есть, возвращает его фигуру
    public Piece calculateWinner(Piece[][] pieces, int i, int j) {
        Piece winner = null;
        int[][] directions = { //массив всех направлений проверки по часовой стрелке
                {-1, -1},
                {-1, 0},
                {-1, 1},
                {0, 1},
                {1, 1},
                {1, 0},
                {1, -1},
                {0, -1}
        };
        //перебор всех возможных вариантов (их 8):
        for (int k = 0; winner == null && k < directions.length; k++) {
            winner = checkWinDirection(pieces, i, j, directions[k][0], directions[k][1]);
        }
        return winner;
    }

    //проверка победителя в заданном направлении
    //d1 и d2 - это направление проверки
    //пр.: d1 = 0; d2 = 1 => проверка идет по горизонтали вправо
    //d1 = -1; d2 = -1; => проверка идет по диагонали влево вверх
    private Piece checkWinDirection(Piece[][] pieces, int i, int j, int d1, int d2) {
        int score = 1;
        Piece piece = pieces[i][j];
        int x = i;
        int y = j;
        x += d1;
        y += d2;
        //пока не вышли из границ поля и проверяемый на победу символ присутствует, считаются очки и проверяется дальше
        while ((x >= 0 && y >= 0 && x < pieces.length && y < pieces.length) && (pieces[x][y] == piece)) {
            score++;
            x += d1;
            y += d2;
        }
        //если в ряд набралось достаточно победителей, возвращает победителя
        if (score == 5) {
            return piece == Piece.X ? Piece.X : Piece.O;
        }
        else return null; //означает отсутствие победителя
    }

    //метод проверки на ничью:
    //если все клетки заняты, то это считается ничьей
    public boolean isDraw(Piece[][] squares) {
        for (int i = 0; i < squares.length; i++) {
            for (int j = 0; j < squares.length; j++) {
                if (squares[i][j] == null) {
                    return false;
                }
            }
        }
        return true;
    }
}
