import React from "react";
import InstrumentDetails, {
    InstrumentDetailsParams
} from "../module/dataSource/features/instrumentDetails/instrumentDetails";
import {useParams} from "react-router-dom";
import ErrorPage from "./ErrorPage";

export default function InstrumentDetailsPage() {
    const params = useParams<InstrumentDetailsParams>()
    if (!params.instrumentId || !params.datasourceId) return <ErrorPage />;
    return <InstrumentDetails instrumentId={params.instrumentId} datasourceId={params.datasourceId} />
}