import React, {useEffect, useState} from 'react';
import {useNavigate} from "react-router-dom";
import {Datasource, InstrumentInList} from "../../entities/Datasource";
import {fetchDatasource, fetchInstrumentList} from "../../api/dataSourceRestClient";
import Table from 'react-bootstrap/Table';
import {Accordion, Spinner} from "react-bootstrap";

export type InstrumentListParams = {
    datasourceId: string,
}

export default function InstrumentList(params: InstrumentListParams) {
    const navigate = useNavigate();
    const handleClick = (id: string) => navigate(`${id}`);
    const [datasource, setDatasource] = useState<Datasource>();
    const [instruments, setInstruments] = useState<Array<InstrumentInList>>([]);

    useEffect(() => {
        fetchDatasource(params.datasourceId).then((data) => setDatasource(data));
        fetchInstrumentList(params.datasourceId).then((data) => setInstruments(data));
    }, [params.datasourceId]);

    const listItems = instruments.map(instrument =>
        <tr key={instrument.id} onClick={() => handleClick(instrument.ticker)}>
            <td>{instrument.ticker}</td>
            <td>{instrument.shortName}</td>
        </tr>
    );

    if (!datasource || !instruments || instruments.length === 0) {
        return <Spinner animation="border" role="status">
            <span className="visually-hidden">Loading...</span>
        </Spinner>
    }

    const exchangeItem =
        <Accordion defaultActiveKey="1">
            <Accordion.Item eventKey="0">
                <Accordion.Header id="exchangeHeader">{datasource.name}</Accordion.Header>
                <Accordion.Body>
                    <h5>Адрес шлюза</h5>
                    <p>{datasource.url}</p>
                    <h5>Описание</h5>
                    <p>{datasource.description}</p>
                </Accordion.Body>
            </Accordion.Item>
        </Accordion>;

    return (
        <>
            {exchangeItem}
            <Table striped bordered hover>
                <thead>
                <tr>
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