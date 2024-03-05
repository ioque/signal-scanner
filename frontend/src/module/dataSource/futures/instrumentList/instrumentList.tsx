import React, {useEffect, useState} from 'react';
import './style.scss'
import {Link} from "react-router-dom";
import {Exchange, InstrumentInList} from "../../entities/Exchange";
import ErrorPage from "../../../../pages/ErrorPage";
import {fetchExchange, fetchInstruments} from "../../api/dataSourceRestClient";

export default function InstrumentList() {
    const [exchange, setExchange] = useState<Exchange>();
    const [instruments, setInstruments] = useState<Array<InstrumentInList>>([]);

    useEffect(() => {
        fetchExchange().then((data) => setExchange(data));
        fetchInstruments().then((data) => setInstruments(data));
    }, []);

    const listItems = instruments.map(instrument =>
        <li
            key={instrument.ticker}
        >
            <div className="instrument-item-list">
                <Link to={`${instrument.id}`}>
                    <p>{instrument.shortName}</p>
                </Link>
                <p>{instrument.ticker}</p>
                <p>
                    <Link to={`${instrument.id}/statistic`}>Статистика</Link>
                </p>
            </div>
        </li>
    );

    if (!exchange) {
        return <ErrorPage />
    }

    const exchangeItem = <div className="instrument-item-list">
        <p>{exchange.id}</p>
        <p>{exchange.name}</p>
        <p>{exchange.url}</p>
        <p>{exchange.description}</p>
    </div>;

    return (
        <>
            {exchangeItem}
            <ul>{listItems}</ul>
        </>
    );
}