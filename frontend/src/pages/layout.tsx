import React, { useEffect } from 'react';
import { Outlet } from 'react-router-dom';
import {Container, Nav, Navbar} from "react-bootstrap";
export default function Root() {
    useEffect(() => {
        document.title = "Система сигналов";
    }, []);

    return (
        <>
            <Navbar expand="lg" className="bg-body-tertiary">
                <Container fluid="xxl">
                    <Navbar.Brand href="#home">Система сигналов</Navbar.Brand>
                    <Navbar.Toggle aria-controls="basic-navbar-nav" />
                    <Navbar.Collapse id="basic-navbar-nav">
                        <Nav className="me-auto">
                            <Nav.Link href={`/datasource`}>Источники данных</Nav.Link>
                            <Nav.Link href={`/scanner`}>Сканеры</Nav.Link>
                        </Nav>
                    </Navbar.Collapse>
                </Container>
            </Navbar>
            <div id="detail">
                <Container fluid="xxl">
                    <Outlet />
                </Container>
            </div>
        </>
    );
}