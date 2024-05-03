import React, {useEffect, useState} from 'react';
import {useNavigate} from "react-router-dom";
import {InstrumentInList} from "../../entities/Datasource";
import {fetchInstrumentList} from "../../api/dataSourceRestClient";
import Table from 'react-bootstrap/Table';
import {Spinner} from "react-bootstrap";
import {
    Box,
    FormControlLabel,
    Paper, Switch,
    TableBody,
    TableCell,
    TableContainer,
    TableHead, TablePagination,
    TableRow,
    TableSortLabel
} from "@mui/material";
import { visuallyHidden } from '@mui/utils';

export type InstrumentListParams = {
    datasourceId: string,
}
type Order = 'asc' | 'desc';

interface HeadCell {
    disablePadding: boolean;
    id: keyof InstrumentInList;
    label: string;
    numeric: boolean;
}

const headCells: readonly HeadCell[] = [
    {
        id: 'ticker',
        numeric: false,
        disablePadding: true,
        label: 'Тикер',
    },
    {
        id: 'shortName',
        numeric: true,
        disablePadding: false,
        label: 'Наименование',
    },
    {
        id: 'todayValue',
        numeric: true,
        disablePadding: false,
        label: 'Текущий объем',
    },
    {
        id: 'todayLastPrice',
        numeric: true,
        disablePadding: false,
        label: 'Последняя цена',
    }
];

export default function InstrumentList(params: InstrumentListParams) {
    const navigate = useNavigate();
    const handleDoubleClick = (id: string) => navigate(`${id}`);
    const [instruments, setInstruments] = useState<Array<InstrumentInList>>([]);
    const [order, setOrder] = React.useState<Order>('asc');
    const [orderBy, setOrderBy] = React.useState<keyof InstrumentInList>('ticker');
    const [selected, setSelected] = React.useState<readonly number[]>([]);
    const [page, setPage] = React.useState(0);
    const [totalElements, setTotalElements] = React.useState(0);
    const [dense, setDense] = React.useState(false);
    const [rowsPerPage, setRowsPerPage] = React.useState(5);

    const handleChangePage = (event: unknown, newPage: number) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event: React.ChangeEvent<HTMLInputElement>) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    const handleChangeDense = (event: React.ChangeEvent<HTMLInputElement>) => {
        setDense(event.target.checked);
    };

    const createSortHandler =
        (property: keyof InstrumentInList) => (event: React.MouseEvent<unknown>) => {
            console.log(event, property);
        };

    useEffect(() => {
        fetchInstrumentList(params.datasourceId,[
            {name: "pageNumber", value: page},
            {name: "pageSize", value: rowsPerPage},
            {name: "orderValue", value: order.toUpperCase()},
            {name: "orderField", value: orderBy}
        ]).then((data) => {
            setPage(data.page)
            setInstruments(data.elements)
            setTotalElements(data.totalElements)
        });
    }, [page, params.datasourceId, rowsPerPage]);

    const listItems = instruments.map(instrument =>
        <TableRow
            onDoubleClick={() => handleDoubleClick(instrument.id)}
            style={{
                height: (dense ? 33 : 53),
            }}
            hover
            role="checkbox"
            tabIndex={-1}
            key={instrument.id}
            sx={{ cursor: 'pointer' }}
        >
            <TableCell
                component="th"
                scope="row"
                padding="none"
            >
                {instrument.ticker}
            </TableCell>
            <TableCell align="right">{instrument.shortName}</TableCell>
            <TableCell align="right">{instrument.todayValue}</TableCell>
            <TableCell align="right">{instrument.todayLastPrice}</TableCell>
        </TableRow>
    );

    if (!instruments || instruments.length === 0) {
        return <Spinner animation="border" role="status">
            <span className="visually-hidden">Loading...</span>
        </Spinner>
    }


    return (
        <Box sx={{ width: '100%' }}>
            <Paper sx={{ width: '100%', mb: 2 }}>
                <TableContainer>
                    <Table>
                        <TableHead>
                            <TableRow>
                                {headCells.map((headCell) => (
                                    <TableCell
                                        key={headCell.id}
                                        align={headCell.numeric ? 'right' : 'left'}
                                        padding={headCell.disablePadding ? 'none' : 'normal'}
                                    >
                                        <TableSortLabel
                                            active={orderBy === headCell.id}
                                            direction={orderBy === headCell.id ? order : 'asc'}
                                            onClick={createSortHandler(headCell.id)}
                                        >
                                            {headCell.label}
                                            {orderBy === headCell.id ? (
                                                <Box component="span" sx={visuallyHidden}>
                                                    {order === 'desc' ? 'sorted descending' : 'sorted ascending'}
                                                </Box>
                                            ) : null}
                                        </TableSortLabel>
                                    </TableCell>
                                ))}
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {listItems}
                        </TableBody>
                    </Table>
                </TableContainer>
                <TablePagination
                    rowsPerPageOptions={[5, 10, 25]}
                    component="div"
                    count={totalElements}
                    rowsPerPage={rowsPerPage}
                    page={page}
                    onPageChange={handleChangePage}
                    onRowsPerPageChange={handleChangeRowsPerPage}
                />
            </Paper>
            <FormControlLabel
                control={<Switch checked={dense} onChange={handleChangeDense} />}
                label="Уплотнить"
            />
        </Box>
    );
}