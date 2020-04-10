import React from 'react';
import ReactDOM from 'react-dom';
//import { BrowserRouter } from 'react-router-dom';
import Game from './game.js';
import {Route, BrowserRouter, Switch, Link} from "react-router-dom";

const Router = BrowserRouter;

class Menu extends React.Component {
    render() {

        return (
            <div>
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