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
            winner: "",
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

    postRequest(path, gameId, x, y) {
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
                console.log("this is player's turn");
                this.getLastMove();
            })
            //цикл запрос продолжает циклично отправляться
            .catch(error => {
                console.log(error.status);
            })
    };

    //запрос на загрузку нового состояния,
    // измененного оппонентом
    async getLastMove() {
        let move = await this.getRequest('/move/last', this.state.gameId);
        console.log("lastMove = " + JSON.stringify(move));
        if (move !== "") {
            //проверка на победителя
            let winner = await this.postRequest('/game/winner',this.state.gameId, move.x, move.y);
            let isDraw = await this.getRequest('/game/draw', this.state.gameId);
            console.log("getLastMove winner = " + winner);
            let squares = this.state.squares;
            squares[move.x][move.y] = move.piece;
            this.setState({
                squares: squares,
                xIsNext: move.piece === 'X' ? 'X' : 'O',
                winner: winner,
                isDraw : isDraw
            });
        }
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
            lastPiece = moves.pop().piece;
            console.log(lastPiece);
        }
        else {
            lastPiece = 'O'; //для первого хода
        }
        console.log("lastPiece = " + lastPiece);

        let currentGame = await this.getRequest('/game/', this.state.gameId);
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

        //this.isPlayerTurn();
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
        if (!!this.state.winner || squares[i][j]) {
            return;
        }

        //запрос на поставку знака
        let newMove = await this.postRequest('/move/create', this.state.gameId, i, j);
        if (!!newMove) {
            squares[i][j] = newMove.piece; //вставка соответствующего символа
            //проверка на наличие победителя
            let winner = await this.postRequest('/game/winner', this.state.gameId, i, j);
            let status = this.state.status;
            if (winner !== "") {
                status = newMove.game.gameStatus;
            }
            console.log("winner = " + winner);
            let isDraw = await this.getRequest('/game/draw', this.state.gameId);
            this.setState({ //изменение состояния игры
                squares: squares, //новая версия массива клеток с только что поставленным символом
                xIsNext: !this.state.xIsNext,
                winner: winner, //проверка на победителя
                isDraw: isDraw, //проверка на ничью,
                status: status
            });

            this.interval = setInterval(this.isPlayerTurn, 500);
        }
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
        console.log("winner in render = " + this.state.winner);
        let status = this.state.firstPlayerName + ' VS ' + this.state.secondPlayerName
            + ', Статус: ' + this.state.status;
        status += !!this.state.winner ? (', Победил:' + this.state.winner) :
            '';

        return (
            <div>
                <div className="status">{status}</div>
                {this.renderBoard()}
            </div>
        );
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
