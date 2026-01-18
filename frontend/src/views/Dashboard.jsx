import {useEffect, useState} from "react";
import {useAuth} from "../firebase/AuthProvider";
import Navbar from "../components/Navbar";
import "./DashboardStyle.css";
import {registerUserWithSensor, findDeviceFromUser, findNotificationHistoryFromSensor} from "../api/services/SensorService"

export default function Dashboard() {
    const {user} = useAuth();
    const [isDropdownOpen, setIsDropdownOpen] = useState(false);

    const [devices, setDevices] = useState([]);
    const [selectedDevice, setSelectedDevice] = useState("No devices found");
    const [showAddDeviceModal, setShowAddDeviceModal] = useState(false);
    const [sensorID, setSensorID] = useState("");
    const [addDeviceError, setAddDeviceError] = useState("");
    const [deviceHistory, setDeviceHistory] = useState([]);

    const handleDeviceSelect = (device) => {
        setSelectedDevice(device);
        setIsDropdownOpen(false);
    };

    const handleAddDevice = async () => {
        if (sensorID.length === 0) {
            setAddDeviceError("You must specify a sensor ID.");
            return;
        }

        try {
            setAddDeviceError("");
            await registerUserWithSensor(sensorID);
            await fetchDevices();
            setShowAddDeviceModal(false);
        } catch (error) {
            setAddDeviceError(error.response?.data?.message || "Failed to add device!");
        }
    }

    const fetchDevices = async () => {
        const response = await findDeviceFromUser();
        setDevices(response);
    }

    const fetchDeviceHistory = async () => {
        const response = await findNotificationHistoryFromSensor(selectedDevice);
        console.log(response);
    }

    useEffect(() => {
        if (user) {
            fetchDevices();
        }
    }, [user])

    useEffect(() => {
        if (devices.length > 0) {
            setSelectedDevice(devices[0].id);
            fetchDeviceHistory();
        } else {
            setSelectedDevice("No devices found");
        }
    }, [devices])

    return (
        <>
            <div id="dashboard-parent-container">
                <Navbar/>
                <div id="dashboard-container-1">
                    <p id="dashboard-my-devices">My Devices</p>
                    <div id="dashboard-device-dropdown" onClick={() => setIsDropdownOpen(!isDropdownOpen)}>
                        <div id="dashboard-dropdown-header">
                            <span id="dashboard-selected-device">{selectedDevice}</span>
                            <img
                                src="/images/polygon-triangle.png"
                                id="dashboard-polygon-triangle"
                                className={isDropdownOpen ? "rotated" : ""}
                                alt="arrow pointing down for the drop menu"
                            />
                        </div>
                        {isDropdownOpen && (
                            <div id="dashboard-dropdown-menu">
                                {devices.map((device) => (
                                    <div
                                        key={device.id}
                                        className={`dashboard-dropdown-item 
                                        ${selectedDevice === device.id ? "selected" : ""}`}
                                        onClick={() => {
                                            handleDeviceSelect(device.id);
                                        }}
                                    >
                                        {device.id}
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                    <button id="dashboard-add-device-button" className="glass"
                            onClick={() => setShowAddDeviceModal(true)}>Add Device
                    </button>
                </div>
                <div id="dashboard-container-2">
                    <div id="dashboard-left-sidebar">
                        <p id="dashboard-left-sidebar-p">Device History</p>
                        <div id="dashboard-left-sidebar-row">
                            <p id="dashboard-left-sidebar-time">10:32AM 17/01/2026</p>
                            <p id="dashboard-left-sidebar-motion">Motion detected</p>
                        </div>
                    </div>
                    <div id="dashboard-right-sidebar"></div>
                </div>

                {showAddDeviceModal &&
                    <div id="dashboard-add-device-modal-container"
                         onClick={() => setShowAddDeviceModal(false)}>
                        <div className="glass" id="dashboard-add-device-modal-container-2"
                             onClick={(e) => e.stopPropagation()}>
                            <button id="add-device-modal-x-button"
                                    onClick={() => {
                                        setShowAddDeviceModal(false)
                                    }}>X
                            </button>
                            <div id="add-device-modal-input-container">
                                <p id="add-device-modal-p">Enter the device ID on the back of the box</p>
                                <input id={"add-device-modal-input"} type="text"
                                       onChange={(e) => setSensorID(e.target.value)}/>
                            </div>
                            {addDeviceError && <p id="add-device-modal-error">{addDeviceError}</p>}
                            <input id="add-device-modal-submit" type="submit" value="Add Device"
                                   onClick={handleAddDevice}/>
                        </div>
                    </div>}

            </div>
        </>
    );
};