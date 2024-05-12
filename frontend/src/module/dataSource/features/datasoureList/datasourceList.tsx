import React, {useEffect, useState} from 'react';
import {useNavigate} from "react-router-dom";
import {Datasource} from "../../entities/Datasource";
import {fetchDatasourceList} from "../../api/dataSourceRestClient";
import {
    Typography,
    Box,
    IconButton,
    Paper,
    TableContainer, Table, TableHead, TableRow, TableCell, TableBody
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";

export default function DatasourceList() {
    const navigate = useNavigate();
    const navigateToInstrumentList = (datasourceId: string) => navigate(`${datasourceId}/instrument`);
    const [
        datasourceList,
        setDatasourceList
    ] = useState<Array<Datasource>>([]);

    useEffect(() => {
        fetchDatasourceList().then((data) => setDatasourceList(data));
    }, []);

    const listItems = datasourceList.map((datasource, index) =>
        <TableRow
            hover
            key={index}
            sx={{cursor: 'pointer'}}
            onDoubleClick={() => navigateToInstrumentList(datasource.id)}
        >
            <TableCell component="th"
                       scope="row"
                       padding="none">{datasource.id}</TableCell>
            <TableCell align="right">{datasource.url}</TableCell>
            <TableCell align="right">{datasource.description}</TableCell>
        </TableRow>
    );

    return (
        <Box sx={{width: '100%'}}>
            <Paper sx={{width: '100%', mb: 2, padding: 2}}>
                <Box display="flex" justifyContent="space-between">
                    <Typography
                        sx={{flex: '1 1 100%'}}
                        variant="h6"
                        id="tableTitle"
                        component="div"
                    >
                        Зарегистрированные источники данных
                    </Typography>
                    <IconButton onClick={() => navigate("/registration-datasource")}>
                        <AddIcon/>
                    </IconButton>
                </Box>
                <TableContainer>
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell>Идентификатор</TableCell>
                                <TableCell align="right">Адрес шлюза</TableCell>
                                <TableCell align="right">Описание</TableCell>
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