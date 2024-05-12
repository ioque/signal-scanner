import React, {useEffect, useState} from 'react';
import {
    Box,
    Paper,
    TableBody,
    TableCell,
    Table,
    TableContainer,
    TableHead,
    TableRow,
    Typography, CircularProgress
} from "@mui/material";
import {TelegramChat} from "../../entities/TelegramChat";
import {fetchSubscribers} from "../../api/telegramBotRestClient";

export default function TelegramChatList() {
    const [chats, setChats] = useState<Array<TelegramChat>>([]);

    useEffect(() => {
        fetchSubscribers().then((data) => setChats(data));
    }, []);

    const listItems = chats.map((chat, index) =>
        <TableRow
            key={index}
            sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
        >
            <TableCell component="th" scope="row">{chat.chatId}</TableCell>
            <TableCell align="right">{chat.name}</TableCell>
            <TableCell align="right">{new Date(chat.createdAt).toDateString() + " " + new Date(chat.createdAt).toTimeString()}
            </TableCell>
        </TableRow>
    );

    if (!chats || chats.length === 0) {
        return <CircularProgress />
    }

    return (
        <Box sx={{ width: '100%' }}>
            <Paper sx={{ width: '100%', mb: 2, padding: 2 }}>
                <Typography
                    sx={{ flex: '1 1 100%' }}
                    variant="h6"
                    id="tableTitle"
                    component="div"
                >
                    Список подписчиков
                </Typography>
                <TableContainer>
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell>ID чата</TableCell>
                                <TableCell align="right">Имя</TableCell>
                                <TableCell align="right">Дата подписки</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {listItems}
                        </TableBody>
                    </Table>
                </TableContainer>
            </Paper>
        </Box>
    );
}