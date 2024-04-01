import React, {useEffect, useState} from 'react';
import {Instrument} from "../../entities/Exchange";
import {fetchInstrumentDetails} from "../../api/dataSourceRestClient";
import {Accordion, Spinner} from "react-bootstrap";
import Table from "react-bootstrap/Table";

export type InstrumentDetailsParams = {
    id: string
}

export default function InstrumentDetails(params: InstrumentDetailsParams) {
    const [instrument, setInstrument] = useState<Instrument>();
    useEffect(() => {
        fetchInstrumentDetails(params.id).then((data) => setInstrument(data));
    }, [params.id]);

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
            <td>{dailyValue.tradeDate}</td>
            <td>{dailyValue.value}</td>
            <td>{dailyValue.openPrice}</td>
            <td>{dailyValue.closePrice}</td>
        </tr>
    );

    const intradayValues = instrument.intradayValues.map((intradayValue, index) =>
        <tr key={index}>
            <td>{intradayValue.number}</td>
            <td>{intradayValue.price}</td>
            <td>{intradayValue.dateTime}</td>
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
            <h4>Ход текущих торгов</h4>
            <Table striped bordered hover id="intradayValueTable">
                <thead>
                <tr>
                    <th>Номер сделки</th>
                    <th>Дата и время</th>
                    <th>Цена</th>
                </tr>
                </thead>
                <tbody>
                {intradayValues}
                </tbody>
            </Table>
        </>
    );
}