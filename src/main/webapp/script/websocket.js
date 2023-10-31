/*
 * Copyright (C) 2023 Niklaus Aeschbacher (com.nras)
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
 * Java EE 7: Building Web Applications with WebSocket, JavaScript and HTML5 
 * https://www.oracle.com/webfolder/technetwork/tutorials/obe/java/HomeWebsocket/WebsocketHome.html
 */

/*
 * 
 * TODO: Handle socket interrupts
 *   if (socket.readyState === 3) {
 *      socket.close();
 *      socket = new WebSocket("ws://localhost:8080/401_NRAS_WS/actions");
 *
 *      // wait until new connection is open
 *      while (socket.readyState !== 1) {
 *           setTimeout(function () {
 *               // function code goes here
 *           }, 40000);
 *      }
 * }
 * 
 */

window.onload = init;

var socket = new WebSocket("ws://localhost:8080/402_NRAS_WS/actions");
socket.onmessage = onMessage;

function init() {
    hideForm();
}

function onMessage(event) {

    var device = JSON.parse(event.data);
    if (device.action === "ADD") {
        printDeviceElement(device);
    }
    if (device.action === "REMOVE") {
        document.getElementById(device.id).remove();
    }
    if (device.action === "UPDATE") {
        var node = document.getElementById(device.id);
        var statusText = node.children[2];
        if (device.scope === "PUBLIC") {
            statusText.innerHTML = "Scope: "
                    + device.scope + " (<a href=\"#\" OnClick=toggleDevice("
                    + device.id + "," + '"PRIVATE"'
                    + ")>Make private</a>)";
        } else if (device.scope === "PRIVATE") {
            statusText.innerHTML = "Scope: "
                    + device.scope + " (<a style=\"color: red\" href=\"#\" OnClick=toggleDevice("
                    + device.id + "," + '"PUBLIC"'
                    + ")>Make public</a>)";
        }
    }
}

function addDevice(name, type, description) {
    var DeviceAction = {
        action: "ADD",
        scope: "PUBLIC",
        name: name,
        type: type,
        description: description
    };

    socket.send(JSON.stringify(DeviceAction));
}

function removeDevice(element) {
    var id = element;
    var DeviceAction = {
        action: "REMOVE",
        id: id
    };
    socket.send(JSON.stringify(DeviceAction));
}

function toggleDevice(element, scope) {
    var id = element;
    var DeviceAction = {
        action: "UPDATE",
        id: id,
        scope: scope
    };
    socket.send(JSON.stringify(DeviceAction));
}

function printDeviceElement(device) {

    var content = document.getElementById("content");

    var deviceDiv = document.createElement("div");
    deviceDiv.setAttribute("id", device.id);
    deviceDiv.setAttribute("class", "device " + device.type);
    content.appendChild(deviceDiv);

    var deviceName = document.createElement("span");
    deviceName.setAttribute("class", "deviceName");
    deviceName.innerHTML = device.name;
    deviceDiv.appendChild(deviceName);

    var deviceType = document.createElement("span");
    deviceType.innerHTML = "<b>Type:</b> " + device.type;
    deviceDiv.appendChild(deviceType);

    var deviceStatus = document.createElement("span");
    if (device.scope === "PUBLIC") {
        deviceStatus.innerHTML = "<b>Scope:</b> "
                + device.scope
                + " (<a href=\"#\" color=\"red\" OnClick=toggleDevice("
                + device.id + "," + '"PRIVATE"'
                + ")>Make private</a>)";
    } else if (device.scope === "PRIVATE") {
        deviceStatus.innerHTML = "<b>Scope:</b> "
                + device.scope
                + " (<a style=\"color: red\" href=\"#\" OnClick=toggleDevice("
                + device.id + "," + '"PUBLIC"'
                + ")>Make public</a>)";

    }

    deviceDiv.appendChild(deviceStatus);

    var deviceDescription = document.createElement("span");
    deviceDescription.innerHTML = "<b>Comments:</b> " + device.description;
    deviceDiv.appendChild(deviceDescription);

    var removeDevice = document.createElement("span");
    removeDevice.setAttribute("class", "removeDevice");
    removeDevice.innerHTML = "<a href=\"#\" OnClick=removeDevice("
            + device.id + ")>Remove device</a>";
    deviceDiv.appendChild(removeDevice);
}

function showForm() {
    document.getElementById("addDeviceForm").style.display = '';
}

function hideForm() {
    document.getElementById("addDeviceForm").style.display = "none";
}

function formSubmit() {
    var form = document.getElementById("addDeviceForm");
    var name = form.elements["device_name"].value;
    var type = form.elements["device_type"].value;
    var description = form.elements["device_description"].value;
    hideForm();
    document.getElementById("addDeviceForm").reset();
    addDevice(name, type, description);
}

