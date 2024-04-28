import React, {useEffect, useState} from 'react';
import {Instrument} from "../../entities/Datasource";
import {fetchInstrumentDetails} from "../../api/dataSourceRestClient";
import {Accordion, Spinner} from "react-bootstrap";
import Table from "react-bootstrap/Table";

export type InstrumentDetailsParams = {
    datasourceId: string,
    instrumentId: string,
}

export default function InstrumentDetails(params: InstrumentDetailsParams) {
    const [instrument, setInstrument] = useState<Instrument>();
    useEffect(() => {
        fetchInstrumentDetails(params.datasourceId, params.instrumentId).then((data) => setInstrument(data));
    }, [params.datasourceId, params.instrumentId]);

    if (!instrument) {
        return <Spinner animation="border" role="status">
            <span className="visually-hidden">Loading...</span>
        </Spinner>
    }

    const instrumentItem =
        <Accordion defaultActiveKey="1">
            <Accordion.Item eventKey="0">
                <Accordion.Header>{instrument.shortName} ({instrument.ticker})</Accordion.Header>
                <Accordion.Body>
                    а пока пусто
                </Accordion.Body>
            </Accordion.Item>
        </Accordion>;

    const dailyValues = instrument.historyValues.map((dailyValue, index) =>
        <tr key={index}>
            <td>{new Date(dailyValue.tradeDate).toDateString()}</td>
            <td>{dailyValue.value}</td>
            <td>{dailyValue.openPrice}</td>
            <td>{dailyValue.closePrice}</td>
        </tr>
    );

    return (
        <>
            {instrumentItem}
            <h4>Итоги торгов</h4>
            <Table striped bordered hover id="dailyValueTable">
                <thead>
                <tr>
                    <th>Дата</th>
                    <th>Объем</th>
                    <th>Цена открытия</th>
                    <th>Цена закрытия</th>
                </tr>
                </thead>
                <tbody>
                {dailyValues}
                </tbody>
            </Table>
        </>
    );
}