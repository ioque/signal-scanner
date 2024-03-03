import React from "react";
import {useParams} from "react-router-dom";
import ErrorPage from "./ErrorPage";
import ScannerDetails, {ScannerDetailsParams} from "../module/scanner/futures/scannerDetails/scannerDetails";

export default function ScannerDetailsPage() {
    const params = useParams<ScannerDetailsParams>()
    if (!params.id) return <ErrorPage />;
    return <ScannerDetails id={params.id} />
}