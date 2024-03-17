import React, {useEffect, useState} from 'react';
import {
    AnomalyVolumeAlgorithmConfig,
    CorrelationSectoralAlgorithmConfig,
    isAnomalyVolumeAlgorithmConfig,
    isCorrelationSectoralAlgorithmConfig,
    isPrefSimpleConfig, isSectoralRetardScannerConfig,
    PrefSimpleAlgorithmConfig,
    Scanner,
    SectoralRetardAlgorithmConfig
} from "../../entities/Scanner";
import {fetchScanner} from "../../api/scannerRestClient";
import {Accordion, Row, Spinner, Stack} from "react-bootstrap";
import Table from "react-bootstrap/Table";

export type ScannerDetailsParams = {
    id: string
}

export default function ScannerDetails(params: ScannerDetailsParams) {
    const [scanner, setScanner] = useState<Scanner>();
    useEffect(() => {
        fetchScanner(params.id).then((data) => setScanner(data));
    }, [params.id]);

    if (!scanner) {
        return <Spinner animation="border" role="status">
            <span className="visually-hidden">Loading...</span>
        </Spinner>
    }

    const scannerConfigItem = () => {
        if (isAnomalyVolumeAlgorithmConfig(scanner.config)) {
            const config: AnomalyVolumeAlgorithmConfig = scanner.config;
            return <Stack>
                <Row>Величина scaleCoefficient: {config.scaleCoefficient}</Row>
                <Row>Период исторических данных: {config.historyPeriod}</Row>
                <Row>Тикер индекса: {config.indexTicker}</Row>
            </Stack>
        }
        if (isCorrelationSectoralAlgorithmConfig(scanner.config)) {
            const config: CorrelationSectoralAlgorithmConfig = scanner.config;
            return <Stack>
                <Row>Величина futuresOvernightScale: {config.futuresOvernightScale}</Row>
                <Row>Величина stockOvernightScale: {config.stockOvernightScale}</Row>
                <Row>Тикер фьючерса: {config.futuresTicker}</Row>
            </Stack>
        }
        if (isPrefSimpleConfig(scanner.config)) {
            const config: PrefSimpleAlgorithmConfig = scanner.config;
            return <Stack>
                <Row>Величина спреда: {config.spreadParam}</Row>
            </Stack>
        }
        if (isSectoralRetardScannerConfig(scanner.config)) {
            const config: SectoralRetardAlgorithmConfig = scanner.config;
            return <Stack>
                <Row>Величина historyScale: {config.historyScale}</Row>
                <Row>Величина intradayScale: {config.intradayScale}</Row>
            </Stack>
        }
        return <p>Нет конфигурации</p>
    }

    const scannerItem =
        <Accordion defaultActiveKey="1">
            <Accordion.Item eventKey="0">
                <Accordion.Header>{scanner.description}</Accordion.Header>
                <Accordion.Body>
                    {scannerConfigItem()}
                </Accordion.Body>
            </Accordion.Item>
        </Accordion>;

    const signals = scanner.signals.map((signal, index) =>
        <tr key={index}>
            <td>{signal.ticker}</td>
            <td>{signal.dateTime}</td>
            <td>{signal.isBuy ? "Сигнал к покупке" : "Сигнал к продаже"}</td>
        </tr>
    );

    const logs = scanner.logs.map((log, index) =>
        <tr key={index}>
            <td>{log.dateTime}</td>
            <td>{log.message}</td>
        </tr>
    )

    return <>
        {scannerItem}
        <h4>Сигналы</h4>
        <Table striped bordered hover>
            <thead>
            <tr>
                <th>Тикер</th>
                <th>Дата и время</th>
                <th>Тип сигнала</th>
            </tr>
            </thead>
            <tbody>
            {signals}
            </tbody>
        </Table>
        <h4>Лог работы</h4>
        <Table striped bordered hover>
            <thead>
            <tr>
                <th>Дата и время</th>
                <th>Сообщение</th>
            </tr>
            </thead>
            <tbody>
            {logs}
            </tbody>
        </Table>
    </>
}