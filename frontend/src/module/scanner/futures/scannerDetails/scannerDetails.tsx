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

    return <>
        <div className="instrument-item-list">
            <p>{scanner.id}</p>
            <p>{scanner.description}</p>
        </div>
    </>
}