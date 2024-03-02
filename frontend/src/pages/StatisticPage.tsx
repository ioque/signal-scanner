import React from 'react';
import StatisticView, {StatisticViewParams} from "../module/statistic/futures/statisticView/statisticView";
import {useParams} from "react-router-dom";
import ErrorPage from "./ErrorPage";

const StatisticPage: React.FC = () => {
    const params = useParams<StatisticViewParams>()
    if (!params.ticker) return <ErrorPage />;
    return <StatisticView ticker={params.ticker} />
}

export default StatisticPage