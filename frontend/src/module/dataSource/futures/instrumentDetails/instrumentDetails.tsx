import React, {useEffect, useState} from 'react';
import './style.scss'
import {fetchInstrumentDetails} from "../../../../share/api/restClient";
import ErrorPage from "../../../../pages/ErrorPage";
import {Instrument} from "../../entities/Exchange";

export type InstrumentDetailsParams = {
    id: string
}

export default function InstrumentDetails(params: InstrumentDetailsParams) {
    const [instrument, setInstrument] = useState<Instrument>();
    useEffect(() => {
        fetchInstrumentDetails(params.id).then((data) => setInstrument(data));
    }, [params.id]);

    if (!instrument) {
        return <ErrorPage />
    }

    const history = instrument.dailyValues.map(dailyValue =>
        <li
            key={dailyValue.tradeDate.toString()}
        >
            <div className="instrument-item-list">
                <p>{dailyValue.tradeDate.toString()}</p>
                <p>{dailyValue.value}</p>
                <p>{dailyValue.closePrice}</p>
                <p>{dailyValue.openPrice}</p>
            </div>
        </li>
    );

    const intradayValues = instrument.intradayValues.map(intradayValue =>
        <li
            key={intradayValue.tradeNumber}
        >
            <div className="instrument-item-list">
                <p>{intradayValue.dateTime.toString()}</p>
                <p>{intradayValue.price}</p>
            </div>
        </li>
    );

    return <>
        <div className="instrument-item-list">
            <p>{instrument.ticker}</p>
            <p>{instrument.shortName}</p>
            <p>История торгов:</p>
            <ul>{history}</ul>
            <p>Ход торгов:</p>
            <ul>{intradayValues}</ul>
        </div>
    </>
}