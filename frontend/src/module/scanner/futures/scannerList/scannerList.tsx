import React, {useEffect, useState} from 'react';
import {ScannerInList} from "../../entities/Scanner";
import {useNavigate} from "react-router-dom";
import {fetchScanners} from "../../api/scannerRestClient";
import Table from "react-bootstrap/Table";
import {Spinner} from "react-bootstrap";

export default function ScannerList() {
    const navigate = useNavigate();
    const handleClick = (id: string) => navigate(`${id}`);
    const [scanners, setScanners] = useState<Array<ScannerInList>>([]);

    useEffect(() => {
        fetchScanners().then((data) => setScanners(data));
    }, []);

    const listItems = scanners.map((scanner, index) =>
        <tr key={index} onClick={() => handleClick(scanner.id)}>
            <td>{index}</td>
            <td>{scanner.id}</td>
            <td>{scanner.workPeriodInMinutes}</td>
            <td>{scanner.description}</td>
            <td>{scanner.signalCounts}</td>
            <td>{scanner.lastExecutionDateTime}</td>
        </tr>
    );

    if (!scanners || scanners.length === 0) {
        return <Spinner animation="border" role="status">
            <span className="visually-hidden">Loading...</span>
        </Spinner>
    }

    return (
        <Table striped bordered hover>
            <thead>
            <tr>
                <th>#</th>
                <th>Идентификатор</th>
                <th>Период работы в минутах</th>
                <th>Описание</th>
                <th>Кол-во сигналов</th>
                <th>Последний запуск</th>
            </tr>
            </thead>
            <tbody>
            {listItems}
            </tbody>
        </Table>
    );
}