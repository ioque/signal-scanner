import React, {useEffect, useState} from 'react';
import {
    Box,
    Paper,
    TableBody,
    TableCell,
    Table,
    TableContainer,
    TableHead,
    TableRow,
    Typography, CircularProgress
} from "@mui/material";
import {EmulatedPosition} from "../entities/EmulatedPosition";
import {fetchEmulatedPositions} from "../api/riskManagerRestClient";

export default function EmulatedPositionList() {
    const [positions, setPositions] = useState<Array<EmulatedPosition>>([]);

    useEffect(() => {
        fetchEmulatedPositions().then((data) => setPositions(data));
    }, []);

    const listItems = positions.map((position, index) =>
        <TableRow
            key={index}
            sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
        >
            <TableCell component="th" scope="row">{position.ticker}</TableCell>
            <TableCell align="right">{position.scannerId}</TableCell>
            <TableCell align="right">{Number(position.openPrice).toFixed(2)}</TableCell>
            <TableCell align="right">{Number(position.lastPrice).toFixed(2)}</TableCell>
            <TableCell align="right" style={{ color: position.profit >= 0 ? 'green' : 'red'}}>{Number(position.profit).toFixed(2)}</TableCell>
        </TableRow>
    );

    if (!positions || positions.length === 0) {
        return <CircularProgress />
    }

    return (
        <Box sx={{ width: '100%' }}>
            <Paper sx={{ width: '100%', mb: 2, padding: 2 }}>
                <Typography
                    sx={{ flex: '1 1 100%' }}
                    variant="h6"
                    id="tableTitle"
                    component="div"
                >
                    Список эмулируемых позиций
                </Typography>
                <TableContainer>
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell>Тикер</TableCell>
                                <TableCell align="right">Сканер</TableCell>
                                <TableCell align="right">Цена открытия</TableCell>
                                <TableCell align="right">Последняя цена</TableCell>
                                <TableCell align="right">Прибыль %</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {listItems}
                        </TableBody>
                    </Table>
                </TableContainer>
            </Paper>
        </Box>
    );
}