import React from 'react';
import ReactDOM from 'react-dom/client';
import reportWebVitals from './reportWebVitals';
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import Root from "./pages/layout";
import ErrorPage from "./pages/ErrorPage";
import InstrumentsPage from "./pages/InstrumentsPage";
import ScannersPage from "./pages/ScannersPage";
import InstrumentDetailsPage from "./pages/InstrumentDetailsPage";
import ScannerDetailsPage from "./pages/ScannerDetailsPage";
import 'bootstrap/dist/css/bootstrap.min.css';
import DatasourceListPage from "./pages/DatasourceListPage";
import EmulatedPositionListPage from "./pages/EmulatedPositionListPage";
import TelegramChatListPage from "./pages/TelegramChatListPage";
import CreateScanner from "./module/scanner/features/createScanner/createScanner";
import CreateDatasource from "./module/dataSource/features/createDatasource/createDatasource";
import CreateDatasourcePage from "./pages/CreateDatasourcePage";

const router = createBrowserRouter([
    {
        path: "/",
        element: <Root />,
        errorElement: <ErrorPage />,
        children: [
            {
                path: "datasource",
                element: <DatasourceListPage />,
            },
            {
                path: "registration-datasource",
                element: <CreateDatasourcePage />,
            },
            {
                path: "datasource/:datasourceId/instrument",
                element: <InstrumentsPage />,
            },
            {
                path: "datasource/:datasourceId/instrument/:instrumentId",
                element: <InstrumentDetailsPage />
            },
            {
                path: "scanner",
                element: <ScannersPage />,
            },
            {
                path: "scanner/:id",
                element: <ScannerDetailsPage />
            },
            {
                path: "create-scanner",
                element: <CreateScanner />,
            },
            {
                path: "emulated-position",
                element: <EmulatedPositionListPage />,
            },
            {
                path: "subscribers",
                element: <TelegramChatListPage />,
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
