import "./navbarStyle.css";
import { Link } from 'react-router-dom';

export default function Navbar() {
    return (
        <div className="navbar">
            <div className="navbar-anticipate-header-container">
                <Link id="navbar-anticipate-text" to="/">ANTICIPATE</Link>
            </div>
            <div className="navbar-login-container">
                <Link id="navbar-login-text" to="/login">Log in</Link>
            </div>
        </div>
    )
}