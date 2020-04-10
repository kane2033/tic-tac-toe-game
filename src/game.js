import React from 'react';
//import ReactDOM from 'react-dom';
import './game.css';

const GameExport = () => {
    return <Game />
};
export default GameExport;

//клетка, которая будет содержать Х или О
function Square(props) {
    return (
        <button className="square" onClick={props.onClick}>
            {props.value}
        </button>
    );
}

//кнопка перезапуска игры, появляется при завершении игры
function RestartButton(props) {
    return (
        <button className="button" onClick={props.onClick}>
            Перезапуск
        </button>
    );
}

//класс доски, хранящий массив крестиков и ноликов
//и состояние игры
class Board extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            boardLength: props.boardLength,
            winCondition: props.winCondition,
            xIsNext: true,
            squares: this.instantiate2DArray(props.boardLength),
            winner: null,
            isDraw: false,
        }
    }

    //метод инициализации двумерного массива и заполнение Null
    instantiate2DArray(length) {
        let array = new Array(length);
        for (let i = 0; i < length; i++) {
            array[i] = new Array(length);
            array[i].fill(null);
        }
        return array;
    }

    //метод при нажатии на квадрат:
    //ставит Х или О в зависимости от того, кто ходит
    //а также меняет состояние игрового поля
    handleClick(i, j) {
        let squares = this.state.squares;
        //если есть победитель или поле заполнено, клик игнорируется
        if (this.state.winner || squares[i][j]) {
            return;
        }
        squares[i][j] = this.state.xIsNext ? 'X' : 'O'; //вставка соответствующего символа
        this.setState({ //изменение состояния игры
            squares: squares, //новая версия массива клеток с только что поставленным символом
            xIsNext: !this.state.xIsNext, //меняет очередь
            winner: this.calculateWinner(squares, i, j, this.state.winCondition), //проверка на победителя
            isDraw: this.isDraw(squares), //проверка на ничью
        });
    }

    //метод проверки на ничью:
    //если все клетки заняты, то это считается ничьей
    isDraw(squares) {
        for (let i = 0; i < squares.length; i++) {
            for (let j = 0; j < squares.length; j++) {
                if (squares[i][j] === null) {
                    return false;
                }
            }
        }
        return true;
    }

    //метод перезапуска игры:
    //возвращает состояние игры в изначальное положение
    restartGame() {
        this.setState({
            squares: this.instantiate2DArray(this.state.boardLength),
            winner: null,
            xIsNext: true,
            isDraw: false,
        });
    }

    //метод отрисовки клетки
    renderSquare(i, j) {
        return (
            <Square
                value={this.state.squares[i][j]}
                onClick={() => this.handleClick(i, j)}
            />
        );
    }

    //метод отрисовки кнопки перезапуска;
    //кнопка отрисовывается при нахождении победителя или ничьи
    renderRestartButton(disabled) {
        return disabled ? null : (
          <RestartButton
              onClick={() => this.restartGame()}
          />
        );
    }

    //возвращает игровое поле с размером, указанном в состоянии
    renderBoard() {
        let len = this.state.boardLength;
        let boardArray = new Array(len);
        let lineArray = new Array(len);
        for (let i = 0; i < len; i++) {
            for (let j = 0; j < len; j++) {
                lineArray[j] = this.renderSquare(i, j);
            }
            boardArray[i] = <div className="board-row">{lineArray.slice()}</div>;

        }
        return boardArray;
    }

    //отрисовывает игровое поле каждый клик
    render() {
        let status = this.state.winner ? 'Выиграл ' + this.state.winner :
            this.state.isDraw ? 'Ничья!' :
        'Следующий ход: ' + (this.state.xIsNext ? 'X' : 'O');

        return (
            <div>
                <div className="status">{status}</div>
                {this.renderBoard()}
                {this.renderRestartButton(this.state.winner == null && this.state.isDraw === false)}
            </div>
        );
    }

    //проверка победителя в заданном направлении
    //d1 и d2 - это направление проверки
    //пр.: d1 = 0; d2 = 1 => проверка идет по горизонтали вправо
    //d1 = -1; d2 = -1; => проверка идет по диагонали влево вверх
    checkWinDirection(squares, i, j, d1, d2, winCondition) {
        let score = 1;
        let symbol = squares[i][j];
        let x = i;
        let y = j;
        x += d1;
        y += d2;
        //пока не вышли из границ поля и проверяемый на победу символ присутствует, считаются очки и проверяется дальше
        while ((x >= 0 && y >= 0 && x < squares.length && y < squares.length) && (squares[x][y] === symbol)) {
            score++;
            x += d1;
            y += d2;
        }
        //если в ряд набралось достаточно победителей, возвращает победителя
        if (score === winCondition) {
            return symbol === 'X' ? 'X' : 'O';
        }
        else return null; //означает отсутствие победителя
    }

    //проверка победителя
    calculateWinner(squares, i, j, winCondition) {
        let winner = null;
        let directions = [ //массив всех направлений проверки по часовой стрелке
            [-1, -1],
            [-1, 0],
            [-1, 1],
            [0, 1],
            [1, 1],
            [1, 0],
            [1, -1],
            [0, -1]
        ];
        //перебор всех возможных вариантов (их 8):
        for (let k = 0; winner == null && k < directions.length; k++) {
            winner = this.checkWinDirection(squares, i, j, directions[k][0], directions[k][1], winCondition);
        }
        return winner;
    }
}

class Game extends React.Component {

    render() {
        return (
            <div className="game">
                <div className="game-board">
                    <Board
                        boardLength={19}//размер поля
                        winCondition={5}//фигур для победы
                    />
                </div>
            </div>
        );
    }
}



// //рендер всех компонентов
// ReactDOM.render(
//     <Game />,
//     document.getElementById('game')
// );
