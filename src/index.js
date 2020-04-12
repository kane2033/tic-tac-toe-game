import React from 'react';
import ReactDOM from 'react-dom';
//import { BrowserRouter } from 'react-router-dom';
import Game from './game.js';
import {BrowserRouter, Switch, Link, Route} from "react-router-dom";
import axios from "axios";

const Router = BrowserRouter;

class StartGameForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            userName: '',
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

    handleSubmit(event) {
        //POST запрос на создание сервера
        axios
            .post('http://localhost:8080/api/game/create', {
                userName: this.state.userName,
                gameType: this.state.gameType,
                selectedPiece: this.state.selectedPiece,
            })
            .then(response => console.log(response.data))
            .catch(error => console.log(error));
        event.preventDefault();
    }

    render() {
        return (
            <form onSubmit={this.handleSubmit}>
                <label>Имя:</label><br />
                <input name="userName" type="text" value={this.state.userName} onChange={this.handleChange} /> <br/>
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
                <input type="submit" value="Начать игру" />
            </form>
        );
    }
}

class Menu extends React.Component {

    render() {
        return (
            <div>
                <div>
                    <StartGameForm />
                </div>
                <div>

                </div>
                <Link to="/game" className="button">Начать игру</Link>
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