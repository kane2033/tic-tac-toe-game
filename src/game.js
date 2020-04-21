import React from 'react';
//import ReactDOM from 'react-dom';
import './game.css';
import axios from "axios";
import { gameStatus } from "./enum/gameStatus";

const GameExport = (props) => {
    return <Game id={props.location.state.gameId}/>
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
            gameId: props.gameId,
            boardLength: props.boardLength,
            winCondition: props.winCondition,
            firstPlayerName: null,
            secondPlayerName: null,
            xIsNext: true,
            squares: this.instantiate2DArray(props.boardLength),
            winner: null,
            isDraw: false,
            state: null
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

    getRequest(path, paramValue) {
        return axios.get('http://localhost:8080/api' + path, {
            params: {
                gameId: paramValue
            }
        })
            .then(response => {
                console.log("возврат значения: " + JSON.stringify(response.data));
                return response.data;
            })
            .catch(error => {
                if (error.response) {
                    console.log(error.response.data);
                }
                return null;
            });
    }

    postRequest(path, gameId, x, y) { //add async??
        return axios
            .post('http://localhost:8080/api' + path, {
                gameId : gameId,
                x: x,
                y: y,
            }, {withCredentials: true})
            .then(response => {
                console.log(response.data);
                return response.data;
            })
            .catch(error => {
                console.log(error.response.data);
            });
    }

    //метод, проверяющий какой сейчас ход
    //(выполняется циклично)
    isPlayerTurn = () => {
        axios.get('http://localhost:8080/api/move/turn',{
            params: {
                gameId: this.state.gameId
            }
        })
            //при возврате http 200 обновление останавливается
            .then(response => {
                console.log(response.status);
                clearInterval(this.interval);
                this.getLastMove();
            })
            //цикл запрос продолжает циклично отправляться
            .catch(error => {
                console.log(error.status);
            })
    };

    //запрос на получение и установку поставленного другим игроком символа
    getLastMove() {
        this.getRequest('/move/last', this.state.gameId).then(move => {
            console.log("lastMove = " + move);
            if (move != null) {
                let squares = this.state.squares;
                squares[move.x][move.y] = move.piece;
                this.setState({
                    squares: squares,
                    xIsNext: !this.state.xIsNext
                });
            }
        });
    }

    interval = 0; //переменная интервала

    //возобновление игры
    async componentDidMount() {
        //установка значений поля
        let moves = await this.getRequest('/move/list', this.state.gameId);
        console.log('moves:' + JSON.stringify(moves));

        let squares = this.state.squares;
        let x = 0;
        let y = 0;
        let lastPiece;
        if (Array.isArray(moves) && moves.length) { //если массив существует и не пустой
            moves.forEach(move => {
                x = move.x;
                y = move.y;
                squares[x][y] = move.piece;
            });
            lastPiece = moves.pop();
            console.log(lastPiece);
        }
        else {
            lastPiece = 'O'; //для первого хода
        }

        let currentGame = await this.getRequest('/game/', this.state.gameId);
        //let currentGame =  this.getRequest('/game/', this.state.gameId);
        console.log('currentGame:' + JSON.stringify(currentGame));

        //установка состояний поля
        let winner = currentGame.gameStatus === gameStatus.Player1_Won ? currentGame.firstPlayerPiece :
            currentGame.gameStatus === gameStatus.Player2_Won ? currentGame.secondPlayerPiece : null;
        this.setState({
            squares: squares,
            firstPlayerName: currentGame.firstPlayer.userName,
            secondPlayerName: currentGame.secondPlayer.userName,
            xIsNext: lastPiece === 'O',
            winner: winner,
            isDraw: currentGame.gameStatus === gameStatus.Tie,
            status: currentGame.gameStatus
        });

        this.isPlayerTurn();
        this.interval = setInterval(this.isPlayerTurn, 500);
    }

    componentWillUnmount() {
        clearInterval(this.interval);
    }

    //метод при нажатии на квадрат:
    //ставит Х или О в зависимости от того, кто ходит
    //а также меняет состояние игрового поля
    async handleClick(i, j) {
        let squares = this.state.squares.slice();
        //если есть победитель или поле заполнено, клик игнорируется
        if (this.state.winner || squares[i][j]) {
            return;
        }

        //запрос на поставку знака
        let newMove = await this.postRequest('/move/create', this.state.gameId, i, j);
        if (newMove != null) {
            console.log('handleClick - setState; newMove = ' + newMove.piece);
            squares[i][j] = newMove.piece; //вставка соответствующего символа
            this.setState({ //изменение состояния игры
                squares: squares, //новая версия массива клеток с только что поставленным символом
                xIsNext: !this.state.xIsNext, //Должно меняться сервером
                winner: this.calculateWinner(squares, i, j, this.state.winCondition), //проверка на победителя
                isDraw: this.isDraw(squares), //проверка на ничью
            });

            this.interval = setInterval(this.isPlayerTurn, 500);
        }
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
        let nextPiece = this.state.xIsNext ? 'X' : 'O';
        let status = this.state.firstPlayerName + ' VS ' + this.state.secondPlayerName
            + ', Статус: ' + this.state.status + ', ходит: ' + nextPiece;

        let isRestartDisabled = this.state.winner == null && this.state.isDraw === false;

        return (
            <div>
                <div className="status">{status}</div>
                {this.renderBoard()}
                {this.renderRestartButton(isRestartDisabled)}
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
    constructor(props) {
        super(props);
        this.state = {
            gameId: props.id
        }
    }

    render() {
        //alert(this.state.gameId)
        return (
            <div className="game">
                <div className="game-board">
                    <Board
                        gameId={this.state.gameId}
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
