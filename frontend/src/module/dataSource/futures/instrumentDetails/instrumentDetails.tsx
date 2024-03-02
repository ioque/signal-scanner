import React, {useEffect, useState} from 'react';
import './style.scss'
import {Instrument} from "../../entities/Instrument";
import {fetchInstrumentDetails} from "../../../../share/api/restClient";
import ErrorPage from "../../../../pages/ErrorPage";

export type InstrumentDetailsParams = {
    id: string
}

export default function InstrumentDetails(params: InstrumentDetailsParams) {
    const [instrument, setInstrument] = useState<Instrument>();
    useEffect(() => {
        fetchInstrumentDetails(params.id).then((data) => setInstrument(data));
    }, []);

    if (!instrument) {
        return <ErrorPage />
    }

    return <>
        <div className="instrument-item-list">
            <p>{instrument.ticker}</p>
            <p>{instrument.shortName}</p>
        </div>
    </>
}