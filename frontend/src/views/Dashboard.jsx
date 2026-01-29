import {useEffect, useState} from "react";
import {useAuth} from "../firebase/AuthProvider";
import Navbar from "../components/Navbar";
import "./DashboardStyle.css";
import {
    registerUserWithSensor,
    findDeviceFromUser,
    findNotificationHistoryFromSensorPageable,
    findNotificationHistoryFromSensorByDate
} from "../api/services/SensorService"
import {format} from 'date-fns';
import {ChevronLeft, ChevronRight} from 'lucide-react';
import Bargraph from "../components/Bargraph";
import {useErrorBoundary} from 'react-error-boundary';

export default function Dashboard() {
    const {user} = useAuth();
    const {showBoundary} = useErrorBoundary();
    const [isDropdownOpen, setIsDropdownOpen] = useState(false);

    const [devices, setDevices] = useState([]);
    const [selectedDevice, setSelectedDevice] = useState("No devices found");
    const [showAddDeviceModal, setShowAddDeviceModal] = useState(false);
    const [sensorID, setSensorID] = useState("");
    const [addDeviceError, setAddDeviceError] = useState("");
    const [deviceHistory, setDeviceHistory] = useState([]);
    const [deviceHistoryPage, setDeviceHistoryPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [deviceHistoryDaily, setDeviceHistoryDaily] = useState([]);
    const [date, setDate] = useState(new Date().toISOString().split("T")[0]);

    // select specific device from dropdown menu
    const handleDeviceSelect = (device) => {
        try {
            setSelectedDevice(device);
            setDeviceHistoryPage(0);
            setIsDropdownOpen(false);
        } catch (error) {
            showBoundary(error);
        }
    };

    // associate sensor with user
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

    // fetch all sensors from the user
    const fetchDevices = async () => {
        try {
            const response = await findDeviceFromUser();
            setDevices(response);
        } catch (error) {
            showBoundary(error);
        }
    }

    const fetchDeviceHistory = async (page, size) => {
        try {
            const response = await findNotificationHistoryFromSensorPageable(selectedDevice, page, size);
            setDeviceHistory(response.content);
            setTotalPages(response.totalPages);
        } catch (error) {
            showBoundary(error);
        }
    };

    const fetchDeviceHistoryByDate = async () => {
        try {
            const timezone = Intl.DateTimeFormat().resolvedOptions().timeZone;
            const response = await findNotificationHistoryFromSensorByDate(selectedDevice, date, timezone);
            setDeviceHistoryDaily(response);
        } catch (error) {
            showBoundary(error);
        }
    }

    useEffect(() => {
        if (user) {
            fetchDevices();
        }
    }, [user])

    useEffect(() => {
        if (devices.length > 0) {
            setSelectedDevice(devices[0].id);
        } else {
            setSelectedDevice("No devices found");
        }
    }, [devices])

    useEffect(() => {
        if (selectedDevice && selectedDevice !== "No devices found") {
            fetchDeviceHistory(deviceHistoryPage, 12);
        } else {
            setSelectedDevice("No devices found");
        }
    }, [selectedDevice, deviceHistoryPage]);

    useEffect(() => {
        if (selectedDevice && selectedDevice !== "No devices found") {
            fetchDeviceHistoryByDate();
        }
    }, [selectedDevice, date]);

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
                                src="/icons/polygon-triangle.png"
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
                        {deviceHistory.length > 0 && deviceHistory.map((device) => (
                            <div
                                key={device.id}
                                id="dashboard-left-sidebar-row"
                            >
                                <p id="dashboard-left-sidebar-time">
                                    {format(new Date(device.timestamp),
                                        'hh:mm a dd/MM/yyyy')}
                                </p>
                                <p id="dashboard-left-sidebar-motion">Motion detected</p>
                            </div>
                        ))}
                        <div id="dashboard-left-sidebar-page-buttons">
                            <ChevronLeft className="page-button-prev"
                                         size={40}
                                         strokeWidth={1.5}
                                         cursor="pointer"
                                         onClick={() =>
                                             setDeviceHistoryPage(p =>
                                                 Math.max(p - 1, 0))}
                            />
                            <ChevronRight className="page-button-next"
                                          size={40} strokeWidth={1.5}
                                          cursor="pointer"
                                          onClick={() => setDeviceHistoryPage(
                                              p => Math.min(p + 1, totalPages - 1))}
                            />
                        </div>
                    </div>
                    <div id="dashboard-right-sidebar">
                        <p id="dashboard-right-sidebar-header">Motion Graph</p>
                        <Bargraph data={deviceHistoryDaily}/>
                        <div id="dashboard-right-sidebar-grouped-chevron">
                            <ChevronLeft
                                className="page-button-prev"
                                id="date-button-prev"
                                size={40}
                                strokeWidth={1.5}
                                cursor="pointer"
                                onClick={() => {
                                    const prev = new Date(date);
                                    prev.setDate(prev.getDate() - 1);
                                    setDate(prev.toISOString().split("T")[0]);
                                }}
                            />
                            <p id="dashboard-right-sidebar-date">{date}</p>
                            <ChevronRight
                                className="page-button-prev"
                                id="date-button-next"
                                size={40} strokeWidth={1.5}
                                cursor="pointer"
                                onClick={() => {
                                    const next = new Date(date);
                                    next.setDate(next.getDate() + 1);
                                    setDate(next.toISOString().split("T")[0]);
                                }}
                            />
                        </div>
                    </div>
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