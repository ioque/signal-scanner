import React from 'react';
import InstrumentList, {InstrumentListParams} from "../module/dataSource/futures/instrumentList/instrumentList";
import {useParams} from "react-router-dom";
import ErrorPage from "./ErrorPage";

export default function InstrumentsPage() {
    const params = useParams<InstrumentListParams>()
    if (!params.datasourceId) return <ErrorPage />;
    return <InstrumentList datasourceId={params.datasourceId} />
}