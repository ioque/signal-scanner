import React, {useEffect, useState} from 'react';
import {Instrument} from "../../entities/Datasource";
import {fetchInstrumentDetails} from "../../api/dataSourceRestClient";
import {
    Box,
    Paper,
    TableContainer,
    Table,
    TableHead,
    TableRow,
    TableCell,
    TableBody,
    Typography,
    CardContent,
    Card,
    List,
    ListItem, CircularProgress
} from "@mui/material";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemText from "@mui/material/ListItemText";

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
        return <CircularProgress />
    }

    const historyValues = instrument.historyValues.map((value, index) =>
        <TableRow
            key={index}
            sx={{'&:last-child td, &:last-child th': {border: 0}}}
        >
            <TableCell component="th" scope="row">
                {new Date(value.tradeDate).toDateString()}
            </TableCell>
            <TableCell align="right">{Number(value.value).toFixed(2)}</TableCell>
            <TableCell align="right">{Number(value.openPrice).toFixed(2)}</TableCell>
            <TableCell align="right">{Number(value.closePrice).toFixed(2)}</TableCell>
            <TableCell align="right">{Number(value.highPrice).toFixed(2)}</TableCell>
            <TableCell align="right">{Number(value.lowPrice).toFixed(2)}</TableCell>
            <TableCell align="right">{value.waPrice === undefined ? "" : Number(value.waPrice).toFixed(2)}</TableCell>
        </TableRow>
    );

    return (
        <Box sx={{width: '100%'}}>
            <Card sx={{mb: 2}}>
                <CardContent>
                    <Typography variant="h5" component="div">
                        {instrument.shortName} ({instrument.ticker})
                    </Typography>
                    <Typography sx={{mb: 1.5}} color="text.secondary">
                        Полное наименование: {instrument.name}
                    </Typography>
                    <List>
                        <ListItem disablePadding>
                            <ListItemButton>
                                <ListItemText primary={`Последнее обновление: ${instrument.lastUpdate}`}/>
                            </ListItemButton>
                        </ListItem>
                        <ListItem disablePadding>
                            <ListItemButton>
                                <ListItemText primary={`Текущий объем: ${instrument.todayValue}`}/>
                            </ListItemButton>
                        </ListItem>
                        <ListItem disablePadding>
                            <ListItemButton>
                                <ListItemText primary={`Последняя цена: ${instrument.todayFirstPrice}`}/>
                            </ListItemButton>
                        </ListItem>
                        <ListItem disablePadding>
                            <ListItemButton>
                                <ListItemText primary={`Цена открытия: ${instrument.todayLastPrice}`}/>
                            </ListItemButton>
                        </ListItem>
                    </List>
                </CardContent>
            </Card>
            <Paper sx={{width: '100%', mb: 2, padding: 2}}>
                <Typography
                    sx={{flex: '1 1 100%'}}
                    variant="h6"
                    id="tableTitle"
                    component="div"
                >
                    Агрегированные итоги торгов
                </Typography>
                <TableContainer>
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell>Дата</TableCell>
                                <TableCell align="right">VALUE</TableCell>
                                <TableCell align="right">OPEN</TableCell>
                                <TableCell align="right">CLOSE</TableCell>
                                <TableCell align="right">HIGH</TableCell>
                                <TableCell align="right">LOW</TableCell>
                                <TableCell align="right">WA</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {historyValues}
                        </TableBody>
                    </Table>
                </TableContainer>
            </Paper>
        </Box>
    );
}