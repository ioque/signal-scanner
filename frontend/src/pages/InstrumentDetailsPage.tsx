import React from "react";
import InstrumentDetails, {
    InstrumentDetailsParams
} from "../module/dataSource/futures/instrumentDetails/instrumentDetails";
import {useParams} from "react-router-dom";
import ErrorPage from "./ErrorPage";

export default function InstrumentDetailsPage() {
    const params = useParams<InstrumentDetailsParams>()
    if (!params.id) return <ErrorPage />;
    return <InstrumentDetails id={params.id} />
}