import React from 'react';
import './style.css'

const instruments = [
    { ticker: 'AFKS', shortName: "ао Система", isGrow: true, lastPrice: 18.41 },
    { ticker: 'SBER', shortName: "Сбербанк", isGrow: false, lastPrice: 260.45  },
    { ticker: 'ROSN', shortName: "Роснефть", isGrow: false, lastPrice: 350.11 },
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
            </div>
        </li>
    );

    return (
        <ul>{listItems}</ul>
    );
}