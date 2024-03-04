import React, {useEffect, useState} from 'react';
import './style.scss'
import {fetchScanner} from "../../../../share/api/restClient";
import ErrorPage from "../../../../pages/ErrorPage";
import {Scanner} from "../../entities/Scanner";

export type ScannerDetailsParams = {
    id: string
}

export default function ScannerDetails(params: ScannerDetailsParams) {
    const [scanner, setScanner] = useState<Scanner>();
    useEffect(() => {
        fetchScanner(params.id).then((data) => setScanner(data));
    }, []);

    if (!scanner) {
        return <ErrorPage />
    }

    const signals = scanner.signals.map(signal =>
        <li
            key={signal.dateTime.toString()}
        >
            <div className="instrument-item-list">
                <p>{signal.ticker}</p>
                <p>{signal.dateTime.toString()}</p>
                <p>{signal.isBuy ? "Сигнал к покупке" : "Сигнал к продаже"}</p>
            </div>
        </li>
    );

    const logs = scanner.reports.map(report => report.logs).flat().map(log =>
        <li key={log.dateTime.toString()}>
            <div className="instrument-item-list">
                <p>{log.dateTime.toString()}</p>
                <p>{log.message}</p>
            </div>
        </li>
    )

    return <>
        <div className="instrument-item-list">
            <p>{scanner.id}</p>
            <p>{scanner.description}</p>
            <p>{JSON.stringify(scanner.config)}</p>
            <p>Зарегистрированные сигналы:</p>
            <ul>{signals}</ul>
            <p>Лог работы</p>
            <ul>{logs}</ul>
        </div>
    </>
}