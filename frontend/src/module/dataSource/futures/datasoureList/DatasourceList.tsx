import React, {useEffect, useState} from 'react';
import {useNavigate} from "react-router-dom";
import {Datasource} from "../../entities/Datasource";
import {fetchDatasourceList} from "../../api/dataSourceRestClient";
import Table from "react-bootstrap/Table";

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
        <tr key={datasource.id} onClick={() => navigateToInstrumentList(datasource.id)}>
            <td>{datasource.id}</td>
            <td>{datasource.name}</td>
            <td>{datasource.url}</td>
        </tr>
    );

    return (
        <Table striped bordered hover>
            <thead>
            <tr>
                <th>ID</th>
                <th>Название</th>
                <th>URL</th>
            </tr>
            </thead>
            <tbody>
            {listItems}
            </tbody>
        </Table>
    );
}