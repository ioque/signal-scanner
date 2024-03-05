import React, {useEffect, useState} from 'react';
import './style.scss'
import {Statistic} from "../../entities/Statistic";
import ErrorPage from "../../../../pages/ErrorPage";
import {fetchInstrumentStatistic} from "../../api/statisticRestClient";

export type StatisticViewParams = {
    id: string
}

export default function StatisticView(params: StatisticViewParams) {
    const [statistic, setStatistic] = useState<Statistic>();
    useEffect(() => {
        fetchInstrumentStatistic(params.id).then((data) => setStatistic(data));
    }, []);

    if (!statistic) {
        return <ErrorPage />
    }
    return (
        <div className="instrument-item-list">
            <p>{statistic.ticker}</p>
            <p>{statistic.todayValue}</p>
            <p>{statistic.medianHistoryValue}</p>
            <p>{statistic.todayLastPrice}</p>
            <p>{statistic.todayOpenPrice}</p>
        </div>
    );
}