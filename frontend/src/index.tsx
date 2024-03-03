import React from 'react';
import ReactDOM from 'react-dom/client';
import reportWebVitals from './reportWebVitals';
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import Root from "./pages/layout";
import ErrorPage from "./pages/ErrorPage";
import InstrumentsPage from "./pages/InstrumentsPage";
import ScannersPage from "./pages/ScannersPage";
import StatisticPage from "./pages/StatisticPage";
import InstrumentDetailsPage from "./pages/InstrumentDetailsPage";
import ScannerDetailsPage from "./pages/ScannerDetailsPage";

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
                path: "instruments/:id",
                element: <InstrumentDetailsPage />
            },
            {
                path: "instruments/:id/statistic",
                element: <StatisticPage />
            },
            {
                path: "scanners",
                element: <ScannersPage />,
            },
            {
                path: "scanners/:id",
                element: <ScannerDetailsPage />
            },
        ],
    },
]);


const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);
root.render(
  <React.StrictMode>
      <RouterProvider router={router}/>
  </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
