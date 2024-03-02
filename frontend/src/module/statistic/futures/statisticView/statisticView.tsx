import React from 'react';
import './style.scss'
import {Statistic} from "../../entities/Statistic";

export type StatisticViewParams = {
    ticker: string
}

const statistic: Statistic = {
    ticker: "AFKS",
    todayValue: 10,
    medianHistoryValue: 10,
    todayLastPrice: 10,
    todayOpenPrice: 10
}

export default function StatisticView(params: StatisticViewParams) {
    console.log(params)
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