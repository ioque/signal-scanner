import React, {useEffect, useState} from 'react';
import {useNavigate} from "react-router-dom";
import {Exchange, InstrumentInList} from "../../entities/Exchange";
import {fetchExchange, fetchInstruments} from "../../api/dataSourceRestClient";
import Table from 'react-bootstrap/Table';
import {Accordion, Spinner} from "react-bootstrap";

export default function InstrumentList() {
    const navigate = useNavigate();
    const handleClick = (id: string) => navigate(`${id}`);
    const [exchange, setExchange] = useState<Exchange>();
    const [instruments, setInstruments] = useState<Array<InstrumentInList>>([]);

    useEffect(() => {
        fetchExchange().then((data) => setExchange(data));
        fetchInstruments().then((data) => setInstruments(data));
    }, []);

    const listItems = instruments.map((instrument, index) =>
        <tr key={index} onClick={() => handleClick(instrument.id)}>
            <td>{index}</td>
            <td>{instrument.ticker}</td>
            <td>{instrument.shortName}</td>
        </tr>
    );

    if (!exchange || !instruments || instruments.length === 0) {
        return <Spinner animation="border" role="status">
            <span className="visually-hidden">Loading...</span>
        </Spinner>
    }

    const exchangeItem =
        <Accordion defaultActiveKey="1">
            <Accordion.Item eventKey="0">
                <Accordion.Header id="exchangeHeader">{exchange.name}</Accordion.Header>
                <Accordion.Body>
                    <h5>Адрес шлюза</h5>
                    <p>{exchange.url}</p>
                    <h5>Описание</h5>
                    <p>{exchange.description}</p>
                </Accordion.Body>
            </Accordion.Item>
        </Accordion>;

    return (
        <>
            {exchangeItem}
            <Table striped bordered hover id="instrumentTable">
                <thead>
                <tr>
                    <th>#</th>
                    <th>Тикер</th>
                    <th>Краткое наименование</th>
                </tr>
                </thead>
                <tbody>
                {listItems}
                </tbody>
            </Table>
        </>
    );
}