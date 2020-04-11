import React from 'react';
import ReactDOM from 'react-dom';
//import { BrowserRouter } from 'react-router-dom';
import Game from './game.js';
import {BrowserRouter, Switch, Link, Route} from "react-router-dom";

const Router = BrowserRouter;

class StartGameForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            userName: '',
            gameType: '',
            selectedPiece: '',
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
        //должна быть  серверу имени
        alert('Отправленное имя: ' + this.state.userName + ', Тип: ' + this.state.gameType + ', Как:' + this.state.selectPiece);
        event.preventDefault();
    }

    render() {
        return (
            <form onSubmit={this.handleSubmit}>
                <label>Имя:</label><br />
                <input name="userName" type="text" value={this.state.userName} onChange={this.handleChange} /> <br/>
                <label>Тип игры:</label>
                <select name="gameType" value={this.state.gameType} onChange={this.handleChange}>
                    <option value="VSPlayer">VS Игрок</option>
                    <option value="VSComputer">VS Компьютер</option>
                </select> <br/>
                <label>Зайти как:</label>
                <select name="selectPiece" value={this.state.selectedPiece} onChange={this.handleChange}>
                    <option value="X">X</option>
                    <option value="O">O</option>
                </select> <br/>
                <input type="submit" value="Отправить" />
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