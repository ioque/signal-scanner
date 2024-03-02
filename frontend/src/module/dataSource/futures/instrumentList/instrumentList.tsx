import React from 'react';
import './style.scss'
import {Instrument} from "../../entities/Instrument";
import {Link} from "react-router-dom";

const instruments: Array<Instrument> = [
    { id: "1", ticker: 'AFKS', shortName: "ао Система", isGrow: true, lastPrice: 18.41 },
    { id: "2", ticker: 'SBER', shortName: "Сбербанк", isGrow: false, lastPrice: 260.45  },
    { id: "3", ticker: 'ROSN', shortName: "Роснефть", isGrow: false, lastPrice: 350.11 },
];

export default function InstrumentList() {
    const listItems = instruments.map(instrument =>
        <li
            key={instrument.ticker}
        >
            <div className="instrument-item-list">
                <p style={{ color: instrument.isGrow ? 'green' : 'darkred' }}>{instrument.shortName}</p>
                <p>{instrument.ticker}</p>
                <p>{instrument.lastPrice}</p>
                <p>
                    <Link to={`${instrument.ticker}/statistic`}>Your Name</Link>
                </p>
            </div>
        </li>
    );

    return (
        <ul>{listItems}</ul>
    );
}