import React, {useEffect, useState} from 'react';
import {ScannerInList} from "../../entities/Scanner";
import {useNavigate} from "react-router-dom";
import {fetchScanners} from "../../api/scannerRestClient";
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

export default function ScannerList() {
    const navigate = useNavigate();
    const handleClick = (id: string) => navigate(`${id}`);
    const [scanners, setScanners] = useState<Array<ScannerInList>>([]);

    useEffect(() => {
        fetchScanners().then((data) => setScanners(data));
    }, []);

    const listItems = scanners.map((scanner, index) =>
        <TableRow
            key={index}
            sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
            onDoubleClick={() => handleClick(scanner.id)}
        >
            <TableCell component="th" scope="row">{scanner.id}</TableCell>
            <TableCell align="right">{scanner.status}</TableCell>
            <TableCell align="right">{scanner.description}</TableCell>
            <TableCell align="right">{
                scanner.lastExecutionDateTime == null ?
                "" :
                (new Date(scanner.lastExecutionDateTime).toDateString() + " " + new Date(scanner.lastExecutionDateTime).toTimeString())
            }
            </TableCell>
        </TableRow>
    );

    if (!scanners || scanners.length === 0) {
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
                    Список сканеров
                </Typography>
                <TableContainer>
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell>Идентификатор</TableCell>
                                <TableCell align="right">Статус</TableCell>
                                <TableCell align="right">Описание</TableCell>
                                <TableCell align="right">Последний запуск</TableCell>
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