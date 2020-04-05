import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';

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
        <button className="restart" onClick={props.onClick}>
            Перезапуск
        </button>
    );
}

//класс доски, хранящий массив крестиков и ноликов
class Board extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            squares: Array(9).fill(null),
            xIsNext: true,
        }
    }

    //метод при нажатии на квадрат:
    //ставит Х или О в зависимости от того, кто ходит
    handleClick(i) {
        const squares = this.state.squares.slice();
        //если есть победитель или поле заполнено, клик игнорируется
        if (calculateWinner(squares) || squares[i]) {
            return;
        }
        squares[i] = this.state.xIsNext ? 'X' : 'O';
        this.setState({
            squares: squares,
            xIsNext: !this.state.xIsNext,
        });
    }

    restartGame() {
        this.setState(this.state.squares.fill(null));
    }

    //метод при создании клетки передает значение
    //из массива значений Х и О в эту клетку, и
    //передается метод, обрабатывающий клик
    renderSquare(i) {
        return (
            <Square
                value={this.state.squares[i]}
                onClick={() => this.handleClick(i)}
            />
        );
    }

    renderRestartButton(disabled) {
        return disabled ? null : (
          <RestartButton
              onClick={() => this.restartGame()}
              //disabled={disabled}
          />
        );
    }

    //отрисовывает игровое поле из 9 клеток
    render() {
        const winner = calculateWinner(this.state.squares);
        let status;
        if (winner) { // если winner != null
            status = 'Выиграл ' + winner;
            //make restart button visible!!!
        }
        else {
            status = 'Следующий ход: ' + (this.state.xIsNext ? 'X' : 'O');
        }

        return (
            <div>
                <div className="status">{status}</div>
                <div className="board-row">
                    {this.renderSquare(0)}
                    {this.renderSquare(1)}
                    {this.renderSquare(2)}
                </div>
                <div className="board-row">
                    {this.renderSquare(3)}
                    {this.renderSquare(4)}
                    {this.renderSquare(5)}
                </div>
                <div className="board-row">
                    {this.renderSquare(6)}
                    {this.renderSquare(7)}
                    {this.renderSquare(8)}
                </div>
                {this.renderRestartButton(calculateWinner(this.state.squares) == null)}
            </div>
        );
    }
}

class Game extends React.Component {
    render() {
        return (
            <div className="game">
                <div className="game-board">
                    <Board />
                </div>
                {/*<div className="game-info">*/}
                {/*    <div>/!* status *!/</div>*/}
                {/*    <ol>/!* TODO *!/</ol>*/}
                {/*</div>*/}
            </div>
        );
    }
}

//метод проверки победителя:
//возвращает X, O или null
//(для бесконечного поля следует поменять метод)
function calculateWinner(squares) {
    const lines = [
        [0, 1, 2],
        [3, 4, 5],
        [6, 7, 8],
        [0, 3, 6],
        [1, 4, 7],
        [2, 5, 8],
        [0, 4, 8],
        [2, 4, 6],
    ];
    for (let i = 0; i < lines.length; i++) {
        const [a, b, c] = lines[i];
        if (squares[a] && squares[a] === squares[b] && squares[a] === squares[c]) {
            return squares[a];
        }
    }
    return null;
}

// ========================================

ReactDOM.render(
    <Game />,
    document.getElementById('root')
);
