import React, {useEffect, useState} from 'react';
import {useNavigate} from "react-router-dom";
import {Datasource} from "../../entities/Datasource";
import {fetchDatasourceList} from "../../api/dataSourceRestClient";
import {Button, CardActions, CardContent, Card, Typography} from "@mui/material";
import Grid from "@mui/material/Grid";

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

    const listItems = datasourceList.map(datasource =>
        <Grid item xs={8} key={datasource.id}>
            <Card>
                <CardContent>
                    <Typography variant="h5" component="div">
                        {datasource.name}
                    </Typography>
                    <Typography sx={{ mb: 1.5 }} color="text.secondary">
                        Адрес шлюза: {datasource.url}
                    </Typography>
                    <Typography variant="body2">
                        {datasource.description}
                    </Typography>
                </CardContent>
                <CardActions>
                    <Button size="small" onClick={() => navigateToInstrumentList(datasource.id)}>Список инструментов</Button>
                </CardActions>
            </Card>
        </Grid>
    );

    return (
        <Grid container spacing={2}>
            {listItems}
        </Grid>
    );
}