import React from 'react';
import ReactDOM from 'react-dom';
import Game from './game.js';
import {BrowserRouter, Switch, Link, Route} from "react-router-dom";
import axios from "axios";
axios.defaults.withCredentials = true;

const Router = BrowserRouter;

class UserNameInput extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            userName: '',
        };
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleChange(event) {
        this.setState({
            userName: event.target.value
        });
    }

    handleSubmit(event) {
        if (this.state.userName === '') {
            alert("Поле имени пользователя пустое");
        }
        else {
            axios
                .post('http://localhost:8080/api/player/login',
                    {userName: this.state.userName},
                    {withCredentials: true})
                .then(response => {
                    console.log(response.data);
                    alert(JSON.stringify(response.data));
                })
                .catch(error => {
                    console.log(error);
                    alert(error);
                });
        }
        event.preventDefault();
    }

    render() {
        return (
            <form onSubmit={this.handleSubmit} className="form" id="login-form">
                <h2>Войти как:</h2>
                <input name="userName" type="text" value={this.state.userName} onChange={this.handleChange} />
                <input type="submit" value="Войти" />
            </form>
        );
    }
}


class StartGameForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            gameType: 'VS_Player',
            selectedPiece: 'X',
        };

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleChange(event) {
        this.setState({
            [event.target.name]: event.target.value
            });
    }

    //POST запрос на создание игры
    createGame() {
        axios //убрать имя
            .post('http://localhost:8080/api/game/create', {
                gameType: this.state.gameType,
                selectedPiece: this.state.selectedPiece,
            }, {withCredentials: true})
            .then(response => {
                console.log(response.data);
                alert(JSON.stringify(response.data));
            })
            .catch(error => {
                console.log(error);
                alert(error);
            });
        window.location.reload();
    }

    handleSubmit(event) {
        this.createGame();
        event.preventDefault();
    }

    render() {
        return (
            <form onSubmit={this.handleSubmit} className="form" id="start-form">
                <h2>Создать игру:</h2>
                {/*<input name="userName" type="text" value={this.state.userName} onChange={this.handleChange} /> <br/>*/}
                <label>Тип игры:</label>
                <select name="gameType" value={this.state.gameType} onChange={this.handleChange}>
                    <option value="VS_Player">VS_Player</option>
                    <option value="VS_Computer">VS_Computer</option>
                </select> <br/>
                <label>Зайти как:</label>
                <select name="selectedPiece" value={this.state.selectedPiece} onChange={this.handleChange}>
                    <option value="X">X</option>
                    <option value="O">O</option>
                </select> <br/>
                <input type="submit" value="Создать игру" />
            </form>
        );
    }
}

class GamesList extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            games : []
        }
    }

    componentDidMount() {
        axios
            .get('http://localhost:8080/api/game/list')
            .then(response => {
                this.setState({
                    games: response.data
                });
                console.log(response.data)
            })
            .catch(error => {
                console.log(error);
                alert(error);
            });
    }

    formTable(games) {
        let table = [];
        for (let i = 0; i < games.length; i++) {
            let rows = [];
            rows.push(<th>{games[i].firstPlayer.userName}</th>); //игроки
            rows.push(<th>{games[i].gameStatus}</th>); //статус игры
            rows.push(<th>{games[i].gameType}</th>); //тип игры
            rows.push(<th>{StartGameButton()}</th>);
            table.push(<tr>{rows}</tr>);
        }
        return table;
    }

    render () {
        return (
                <table id="games-table">
                    <thead>Список доступных игр:</thead>
                    <tr>
                        <th>Список игроков</th>
                        <th>Статус игры</th>
                        <th>Тип игры</th>
                        <th> </th>
                    </tr>
                    {this.formTable(this.state.games)}
                </table>
        );
    }
}

function StartGameButton() {
    return (
        <Link to="/game" className="button">Начать игру</Link>
    );
}

class Menu extends React.Component {

    render() {
        return (
            <div>
                <div id="forms">
                    <UserNameInput />
                    <StartGameForm />
                </div>
                    <GamesList />
            </div>
        );
    }
}

ReactDOM.render(
    <Router>
        <Switch>
            <Route exact path="/" component={Menu} />
            <Route path="/game" component={Game} />
        </Switch>
    </Router>,
    document.getElementById('root')
);