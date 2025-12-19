import {useState} from "react";
import {useAuth} from "../firebase/AuthProvider";
import Navbar from "../components/Navbar";
import "./DashboardStyle.css";

export default function Dashboard() {
    const {user} = useAuth();
    const [isDropdownOpen, setIsDropdownOpen] = useState(false);
    const [selectedDevice, setSelectedDevice] = useState("Device 1");

    const devices = ["Device 1", "Device 2", "Device 3", "Device 4"];

    const handleDeviceSelect = (device) => {
        setSelectedDevice(device);
        setIsDropdownOpen(false);
    };

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
                                        key={device}
                                        className={`dashboard-dropdown-item ${selectedDevice === device ? "selected" : ""}`}
                                        onClick={() => {
                                            handleDeviceSelect(device);
                                        }}
                                    >
                                        {device}
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                </div>
                <div id="dashboard-container-2">
                    <div id="dashboard-left-sidebar"></div>
                    <div id="dashboard-right-sidebar"></div>
                </div>
            </div>
        </>
    );
}