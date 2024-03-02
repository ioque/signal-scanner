import React, { useEffect } from 'react';
import { Outlet } from 'react-router-dom';
export default function Root() {
    useEffect(() => {
        document.title = "Система сигналов";
    }, []);

    return (
        <>
            <div id="sidebar">
                <h1>Система сигналов фондового рынка</h1>
                <nav>
                    <ul>
                        <li>
                            <a href={`/instruments`}>Финансовые инструменты</a>
                        </li>
                        <li>
                            <a href={`/scanners`}>Сканеры</a>
                        </li>
                    </ul>
                </nav>
            </div>
            <div id="detail">
                <Outlet />
            </div>
        </>
    );
}