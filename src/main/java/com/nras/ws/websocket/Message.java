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

import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Message
 * @author Niklaus Aeschbacher
 */
public class Message extends JSONObject {

    private static final String ID_KEY = "id";
    private static final String ACTION_KEY = "action";
    private static final String SCOPE_KEY = "scope";

    /**
     *
     */
    public static final Integer ID_UNDEF = -1;

    /**
     *
     */
    public enum SCOPE {

        /**
         *
         */
        PUBLIC,
        /**
         *
         */
        PRIVATE,
        /**
         *
         */
        UNDEF_SCOPE
    }

    /**
     *
     */
    public enum ACTION {

        /**
         *
         */
        ADD,
        /**
         *
         */
        REMOVE,
        /**
         *
         */
        UPDATE,
        /**
         *
         */
        UNDEF_ACTION
    }

    /**
     *
     */
    public Message() {

    }

    /**
     *
     * @param jo
     * @param names
     * @throws JSONException
     */
    public Message(JSONObject jo, String[] names) throws JSONException {
        super(jo, names);
    }

    /**
     *
     * @param x
     * @throws JSONException
     */
    public Message(JSONTokener x) throws JSONException {
        super(x);
    }

    /**
     *
     * @param map
     */
    public Message(Map map) {
        super(map);
    }

    /**
     *
     * @param map
     * @param includeSuperClass
     */
    public Message(Map map, boolean includeSuperClass) {
        super(map, includeSuperClass);
    }

    /**
     *
     * @param bean
     */
    public Message(Object bean) {
        super(bean);
    }

    /**
     *
     * @param bean
     * @param includeSuperClass
     */
    public Message(Object bean, boolean includeSuperClass) {
        super(bean, includeSuperClass);
    }

    /**
     *
     * @param object
     * @param names
     */
    public Message(Object object, String[] names) {
        super(object, names);
    }

    /**
     *
     * @param source
     * @throws JSONException
     */
    public Message(String source) throws JSONException {
        super(source);
    }

    /**
     *
     * @return @throws JSONException
     */
    public Integer getId() throws JSONException {
        if (this.has(ID_KEY)) {
            if (this.get(ID_KEY) instanceof Integer) {
                return (Integer) this.get(ID_KEY);
            }
        }
        return ID_UNDEF;
    }

    /**
     *
     * @param id
     * @throws JSONException
     */
    public void setId(Integer id) throws JSONException {
        this.put(ID_KEY, id);
    }

    /**
     *
     * @return @throws JSONException
     */
    public ACTION getAction() throws JSONException {
        if (this.has(ACTION_KEY)) {
            if (this.get(ACTION_KEY) instanceof String) {
                return ACTION.valueOf((String) this.get(ACTION_KEY));
            }
        }
        return ACTION.UNDEF_ACTION;
    }

    /**
     *
     * @param action
     * @throws JSONException
     */
    public void setAction(ACTION action) throws JSONException {
        this.put(ACTION_KEY, action.toString());
    }

    /**
     *
     * @return @throws JSONException
     */
    public SCOPE getScope() throws JSONException {

        if (this.has(SCOPE_KEY)) {
            if (this.get(SCOPE_KEY) instanceof String) {
                return SCOPE.valueOf((String) this.get(SCOPE_KEY));
            }
        }
        return SCOPE.UNDEF_SCOPE;
    }

    /**
     *
     * @param scope
     * @throws JSONException
     */
    public void setScope(SCOPE scope) throws JSONException {
        this.put(SCOPE_KEY, scope.toString());
    }

}
