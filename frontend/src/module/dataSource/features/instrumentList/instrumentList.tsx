import React, {useEffect, useState} from 'react';
import {useNavigate} from "react-router-dom";
import {InstrumentInList} from "../../entities/Datasource";
import {fetchInstrumentList} from "../../api/dataSourceRestClient";
import {
    Box,
    CircularProgress, IconButton,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead, TablePagination,
    TableRow,
    TableSortLabel, useTheme
} from "@mui/material";
import FirstPageIcon from '@mui/icons-material/FirstPage';
import KeyboardArrowLeft from '@mui/icons-material/KeyboardArrowLeft';
import KeyboardArrowRight from '@mui/icons-material/KeyboardArrowRight';
import LastPageIcon from '@mui/icons-material/LastPage';

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

interface TablePaginationActionsProps {
    count: number;
    page: number;
    rowsPerPage: number;
    onPageChange: (
        event: React.MouseEvent<HTMLButtonElement>,
        newPage: number,
    ) => void;
}

function TablePaginationActions(props: TablePaginationActionsProps) {
    const theme = useTheme();
    const { count, page, rowsPerPage, onPageChange } = props;

    const handleFirstPageButtonClick = (
        event: React.MouseEvent<HTMLButtonElement>,
    ) => {
        onPageChange(event, 0);
    };

    const handleBackButtonClick = (event: React.MouseEvent<HTMLButtonElement>) => {
        onPageChange(event, page - 1);
    };

    const handleNextButtonClick = (event: React.MouseEvent<HTMLButtonElement>) => {
        onPageChange(event, page + 1);
    };

    const handleLastPageButtonClick = (event: React.MouseEvent<HTMLButtonElement>) => {
        onPageChange(event, Math.max(0, Math.ceil(count / rowsPerPage) - 1));
    };

    return (
        <Box sx={{ flexShrink: 0, ml: 2.5 }}>
            <IconButton
                onClick={handleFirstPageButtonClick}
                disabled={page === 0}
                aria-label="first page"
            >
                {theme.direction === 'rtl' ? <LastPageIcon /> : <FirstPageIcon />}
            </IconButton>
            <IconButton
                onClick={handleBackButtonClick}
                disabled={page === 0}
                aria-label="previous page"
            >
                {theme.direction === 'rtl' ? <KeyboardArrowRight /> : <KeyboardArrowLeft />}
            </IconButton>
            <IconButton
                onClick={handleNextButtonClick}
                disabled={page >= Math.ceil(count / rowsPerPage) - 1}
                aria-label="next page"
            >
                {theme.direction === 'rtl' ? <KeyboardArrowLeft /> : <KeyboardArrowRight />}
            </IconButton>
            <IconButton
                onClick={handleLastPageButtonClick}
                disabled={page >= Math.ceil(count / rowsPerPage) - 1}
                aria-label="last page"
            >
                {theme.direction === 'rtl' ? <FirstPageIcon /> : <LastPageIcon />}
            </IconButton>
        </Box>
    );
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
    const handleDoubleClick = (ticker: string) => navigate(`${ticker}`);
    const [instruments, setInstruments] = useState<Array<InstrumentInList>>([]);
    const [order, setOrder] = React.useState<Order>('asc');
    const [orderBy, setOrderBy] = React.useState<keyof InstrumentInList>('ticker');
    const [page, setPage] = React.useState(0);
    const [totalElements, setTotalElements] = React.useState(0);
    const [rowsPerPage, setRowsPerPage] = React.useState(25);

    const handleChangePage = (event: unknown, newPage: number) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event: React.ChangeEvent<HTMLInputElement>) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    const createSortHandler =
        (property: keyof InstrumentInList) => (event: React.MouseEvent<unknown>) => {
            handleRequestSort(event, property);
        };

    const handleRequestSort = (
        event: React.MouseEvent<unknown>,
        property: keyof InstrumentInList,
    ) => {
        const isAsc = orderBy === property && order === 'asc';
        setOrder(isAsc ? 'desc' : 'asc');
        setOrderBy(property);
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
    }, [order, orderBy, page, params.datasourceId, rowsPerPage]);

    const listItems = instruments.map(instrument =>
        <TableRow
            onDoubleClick={() => handleDoubleClick(instrument.ticker)}
            hover
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
        return <CircularProgress />
    }

    return (
        <Box sx={{ width: '100%' }}>
            <Paper sx={{ width: '100%', mb: 2, p: 1 }}>
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
                    ActionsComponent={TablePaginationActions}
                />
            </Paper>
        </Box>
    );
}