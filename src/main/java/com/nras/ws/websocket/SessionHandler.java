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

 /* Adapted from 
 Java EE 7: Building Web Applications with WebSocket, JavaScript and HTML5 
 https://www.oracle.com/webfolder/technetwork/tutorials/obe/java/HomeWebsocket/WebsocketHome.html
 */
package com.nras.ws.websocket;

import static com.nras.ws.websocket.Message.ACTION.ADD;
import static com.nras.ws.websocket.Message.ACTION.REMOVE;
import static com.nras.ws.websocket.Message.ACTION.UPDATE;
import static com.nras.ws.websocket.Message.SCOPE.PRIVATE;
import static com.nras.ws.websocket.Message.SCOPE.PUBLIC;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * SessionHandler Singleton that manages sessions and messages. Singleton
 * pattern according to Bill Pugh
 * https://codereview.stackexchange.com/questions/151122/singleton-design-pattern-implementation-bill-pugh
 * In the original code from oracle for Glassfish the SessionHanlder is
 * instantiated through the @Inject annotation. TODO - Uper boundary of sets,
 * maps, ids
 *
 * @author Niklaus Aeschbacher
 */
@ApplicationScoped
public class SessionHandler {

    private final Set<Session> sessionSet;
    private final HashMap<Integer, Message> messageMap;

    private static class BPSingleton {

        private static final SessionHandler INSTANCE = new SessionHandler();
    }

    /**
     *
     * @return
     */
    public static SessionHandler getInstance() {
        return BPSingleton.INSTANCE;
    }

    private Integer messageId = Message.ID_UNDEF;

    private SessionHandler() {
        this.sessionSet = new HashSet<>();
        this.messageMap = new LinkedHashMap<>();
    }

    /**
     *
     * @param session
     * @param msgRaw
     * @throws JSONException
     */
    public void dispatchMessage(Session session, String msgRaw) throws JSONException {

        Message msg = new Message(msgRaw);

        switch (msg.getAction()) {
            case ADD:
                addMessage(session, msg);
                break;
            case UPDATE:
                updateMessage(session, msg);
                break;
            case REMOVE:
                removeMessage(session, msg.getId());
                break;

        }

    }

    /**
     *
     * @param session
     * @throws JSONException
     */
    public void addSession(Session session) throws JSONException {

        this.sessionSet.add(session);
        for (Message msg : messageMap.values()) {
            if (msg.getScope().equals(PUBLIC)) {
                sendToSession(session, msg);
            } else {
                sendToSession(session, msg);
            }
        }
    }

    /**
     *
     * @param session
     */
    public void removeSession(Session session) {
        sessionSet.remove(session);
    }

    /**
     *
     * @return
     */
    public List<Message> getMessageList() {
        return new ArrayList<>(messageMap.values());
    }

    /**
     *
     * @param session
     * @param msg
     * @throws JSONException
     */
    public void addMessage(Session session, Message msg) throws JSONException {

        messageId++;
        msg.setId(messageId);
        messageMap.put(msg.getId(), msg);
        if (msg.getScope().equals(PUBLIC)) {
            sendToAllConnectedSessions(msg);
        } else {
            sendToSession(session, msg);
        }

    }

    /**
     *
     * @param session
     * @param id
     * @throws JSONException
     */
    public void removeMessage(Session session, Integer id) throws JSONException {

        if (id > Message.ID_UNDEF) {
            Message msg = messageMap.get(id);
            if (msg != null) {
                msg.setAction(REMOVE);
                messageMap.remove(id);
                sendToAllConnectedSessions(msg);
            }
        }

    }

    /**
     *
     * @param session
     * @param updatedMessage
     * @throws JSONException
     */
    public void updateMessage(Session session, Message updatedMessage) throws JSONException {

        if (updatedMessage.getId() > Message.ID_UNDEF) {

            Message msg = messageMap.get(updatedMessage.getId());
            if (msg != null) {

                boolean nondisclosed
                        = (msg.getScope().equals(PUBLIC)
                        && updatedMessage.getScope().equals(PRIVATE));
                boolean disclosed
                        = (msg.getScope().equals(PRIVATE)
                        && updatedMessage.getScope().equals(PUBLIC));

                Iterator<String> it = updatedMessage.keys();
                while (it.hasNext()) {
                    String k = it.next();
                    msg.put(k, updatedMessage.get(k));
                }

                msg.setAction(UPDATE);
                if (nondisclosed) {
                    sendToSession(session, msg);
                    msg.setAction(REMOVE);
                    sendToOtherConnectedSessions(session, msg);
                } else if (disclosed) {
                    sendToSession(session, msg);
                    msg.setAction(ADD);
                    sendToOtherConnectedSessions(session, msg);
                } else {
                    sendToSession(session, msg);
                }

            }
        }
    }

    private Message getMessageById(Integer id) {
        return messageMap.get(id);
    }

    private void sendToAllConnectedSessions(JSONObject message) {
        for (Session session : sessionSet) {
            sendToSession(session, message);
        }
    }

    private void sendToOtherConnectedSessions(Session session, JSONObject message) {
        for (Session ohterSession : sessionSet) {
            if (!ohterSession.getId().equals(session.getId())) {
                sendToSession(ohterSession, message);
            }
        }
    }

    private void sendToSession(Session session, Message msg) throws JSONException {
        try {

            if (session.getUserProperties().containsKey(msg.getId().toString())) {

                switch (msg.getAction()) {
                    // ADD: no action, message already exists in session
                    case ADD:
                        break;
                    // UPDATE: send update
                    case UPDATE:
                        session.getBasicRemote().sendText(msg.toString());
                        break;
                    // REMOVE: send remove and remove from properties
                    case REMOVE:
                        session.getUserProperties().remove(msg.getId().toString());
                        session.getBasicRemote().sendText(msg.toString());
                        break;
                    default:
                        // throw not supported error
                        break;
                }

            } else {

                switch (msg.getAction()) {
                    // ADD: send add and put to properties
                    case ADD:
                        session.getUserProperties().put(msg.getId().toString(), msg);
                        session.getBasicRemote().sendText(msg.toString());
                        break;
                    // UPDATE: integrity violation: an update canot be sent to a message 
                    // that does not exist in properties - that has never send before
                    // do nothing or throw exception
                    // REMOVE: same as update 
                    case UPDATE:
                    case REMOVE:
                        break;
                    default:
                        // throw not supported error
                        break;
                }

            }

        } catch (IOException ex) {
            sessionSet.remove(session);
            Logger.getLogger(SessionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
