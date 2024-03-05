import React, {useEffect, useState} from 'react';
import './style.scss'
import {ScannerInList} from "../../entities/Scanner";
import {Link} from "react-router-dom";
import {fetchScanners} from "../../api/scannerRestClient";

export default function ScannerList() {
    const [scanners, setScanners] = useState<Array<ScannerInList>>([]);

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