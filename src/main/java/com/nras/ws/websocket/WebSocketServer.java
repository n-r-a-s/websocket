/*
 * Copyright (C) 2023 Niklaus Aeschbacher (com.nras.*)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.nras.ws.websocket;

import jakarta.enterprise.context.ApplicationScoped;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;

/**
 *
 * @author Niklaus Aeschbacher
 */
@ApplicationScoped
@ServerEndpoint("/actions")
public class WebSocketServer {

    /**
     *
     * @param session
     * @throws JSONException
     */
    @OnOpen
    public void open(Session session) throws JSONException {
        SessionHandler.getInstance().addSession(session);
    }

    /**
     *
     * @param session
     */
    @OnClose
    public void close(Session session) {
        SessionHandler.getInstance().removeSession(session);
    }

    /**
     *
     * @param error
     */
    @OnError
    public void onError(Throwable error) {
        Logger.getLogger(WebSocketServer.class.getName()).log(Level.SEVERE, null, error);
    }

    /**
     *
     * @param message
     * @param session
     * @throws JSONException
     */
    @OnMessage
    public void handleMessage(String message, Session session) throws JSONException {
        SessionHandler.getInstance().dispatchMessage(session, message);

    }
}
