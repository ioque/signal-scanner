import React from 'react';
import './style.scss'
import {Scanner} from "../../entities/Scanner";

const scanners: Array<Scanner> = [
    { id: "1", description: "Аномальные объемы" },
    { id: "2", description: "Секторальные отстающий, сектор нефтянки"  },
    { id: "3", description: "Дельта-анализ пар преф-обычка" },
];

export default function ScannerList() {
    const listItems = scanners.map(instrument =>
        <li
            key={instrument.id}
        >
            <div className="instrument-item-list">
                <p>{instrument.id}</p>
                <p>{instrument.description}</p>
            </div>
        </li>
    );

    return (
        <ul>{listItems}</ul>
    );
}