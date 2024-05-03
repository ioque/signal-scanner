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
import {
    Box,
    Card,
    CardContent, CircularProgress, List, ListItem,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Typography
} from "@mui/material";
import ListItemText from "@mui/material/ListItemText";

export type ScannerDetailsParams = {
    id: string
}

export default function ScannerDetails(params: ScannerDetailsParams) {
    const [scanner, setScanner] = useState<Scanner>();
    useEffect(() => {
        fetchScanner(params.id).then((data) => setScanner(data));
    }, [params.id]);

    if (!scanner) {
        return <CircularProgress />
    }

    const scannerConfigItem = () => {
        if (isAnomalyVolumeAlgorithmConfig(scanner.config)) {
            const config: AnomalyVolumeAlgorithmConfig = scanner.config;
            return <List sx={{padding: 1}}>
                <ListItem>
                    <ListItemText>
                        Величина scaleCoefficient: {config.scaleCoefficient}
                    </ListItemText>
                </ListItem>
                <ListItem>
                    <ListItemText>
                        Период исторических данных: {config.historyPeriod}
                    </ListItemText>
                </ListItem>
                <ListItem>
                    <ListItemText>
                        Тикер индекса: {config.indexTicker}
                    </ListItemText>
                </ListItem>
            </List>
        }
        if (isCorrelationSectoralAlgorithmConfig(scanner.config)) {
            const config: CorrelationSectoralAlgorithmConfig = scanner.config;
            return <List sx={{padding: 1}}>
                <ListItem>
                    <ListItemText>
                        Величина futuresOvernightScale: {config.futuresOvernightScale}
                    </ListItemText>
                </ListItem>
                <ListItem>
                    <ListItemText>
                        Величина stockOvernightScale: {config.stockOvernightScale}
                    </ListItemText>
                </ListItem>
                <ListItem>
                    <ListItemText>
                        Тикер фьючерса: {config.futuresTicker}
                    </ListItemText>
                </ListItem>
            </List>
        }
        if (isPrefSimpleConfig(scanner.config)) {
            const config: PrefSimpleAlgorithmConfig = scanner.config;
            return <List sx={{padding: 1}}>
                <ListItem>
                    <ListItemText>
                        Величина спреда: {config.spreadParam}
                    </ListItemText>
                </ListItem>
            </List>
        }
        if (isSectoralRetardScannerConfig(scanner.config)) {
            const config: SectoralRetardAlgorithmConfig = scanner.config;
            return <List sx={{padding: 1}}>
                <ListItem>
                    <ListItemText>
                        Величина historyScale: {config.historyScale}
                    </ListItemText>
                    <ListItemText>
                        Величина intradayScale: {config.intradayScale}
                    </ListItemText>
                </ListItem>
            </List>
        }
        return <p>Нет конфигурации</p>
    }

    const scannerItem =
        <Card sx={{mb: 2}}>
            <CardContent>
                <Typography variant="h5" component="div">
                    {scanner.description}
                </Typography>
                <Typography variant="h6" component="div">
                    Конфигурация
                </Typography>
                {scannerConfigItem()}
            </CardContent>
        </Card>;

    const signals = scanner.signals.map((signal, index) =>
        <tr key={index}>
            <td>{signal.ticker}</td>
            <td>{new Date(signal.dateTime).toDateString()} {new Date(signal.dateTime).toTimeString()}</td>
            <td>{signal.isBuy ? "Покупка" : "Продаже"}</td>
            <td>{signal.price}</td>
            <td>{signal.summary}</td>
        </tr>
    );

    return <Box sx={{width: '100%'}}>
        {scannerItem}
        <Paper sx={{width: '100%', mb: 2, padding: 2}}>
            <Typography
                sx={{flex: '1 1 100%'}}
                variant="h6"
                id="tableTitle"
                component="div"
            >
                Зафиксированные сигналы
            </Typography>
            <TableContainer>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Тикер</TableCell>
                            <TableCell align="right">Дата и время</TableCell>
                            <TableCell align="right" width={100}>Тип сигнала</TableCell>
                            <TableCell align="right">Цена</TableCell>
                            <TableCell align="right">Описание</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {signals}
                    </TableBody>
                </Table>
            </TableContainer>
        </Paper>
    </Box>
}