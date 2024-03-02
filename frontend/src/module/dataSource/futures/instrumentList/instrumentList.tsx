import React, {useEffect, useState} from 'react';
import './style.scss'
import {Instrument} from "../../entities/Instrument";
import {Link} from "react-router-dom";
import {fetchInstruments} from "../../../../share/api/restClient";

export default function InstrumentList() {
    const [instruments, setInstruments] = useState<Array<Instrument>>([]);

    useEffect(() => {
        fetchInstruments().then((data) => setInstruments(data));
    }, []);

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