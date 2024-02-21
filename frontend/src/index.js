import React from 'react';
import ReactDOM from 'react-dom/client';
import {
    createBrowserRouter,
    RouterProvider,
} from "react-router-dom";
import reportWebVitals from './reportWebVitals';
import Root from './pages/layout';
import InstrumentsPage from './pages/InstrumentsPage';
import SchedulePage from './pages/SchedulePage';
import SignalScannersPage from './pages/SignalScannersPage';
import ErrorPage from './pages/ErrorPage';

const router = createBrowserRouter([
    {
        path: "/",
        element: <Root />,
        errorElement: <ErrorPage />,
        children: [
            {
                path: "instruments",
                element: <InstrumentsPage />,
            },
            {
                path: "signal-scanners",
                element: <SignalScannersPage />,
            },
            {
                path: "schedule",
                element: <SchedulePage />,
            },
        ],
    },
]);

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <React.StrictMode>
        <RouterProvider router={router}/>
    </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
