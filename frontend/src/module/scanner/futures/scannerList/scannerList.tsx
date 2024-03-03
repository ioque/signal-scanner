import React, {useEffect, useState} from 'react';
import './style.scss'
import {Scanner} from "../../entities/Scanner";
import {fetchScanners} from "../../../../share/api/restClient";
import {Link} from "react-router-dom";

export default function ScannerList() {
    const [scanners, setScanners] = useState<Array<Scanner>>([]);

    useEffect(() => {
        fetchScanners().then((data) => setScanners(data));
    }, []);

    const listItems = scanners.map(scanner =>
        <li
            key={scanner.id}
        >
            <div className="instrument-item-list">
                <Link to={`${scanner.id}`}>
                    <p>{scanner.id}</p>
                </Link>
                <p>{scanner.description}</p>
            </div>
        </li>
    );

    return (
        <ul>{listItems}</ul>
    );
}