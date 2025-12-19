import "./navbarStyle.css";
import {Link} from 'react-router-dom';
import {useAuth} from "../firebase/AuthProvider";
import {onLogout} from "../firebase/Firebase";

export default function Navbar() {
    const { user } = useAuth();

    return (
        <div className="navbar">
            <div className="navbar-anticipate-header-container">
                <Link id="navbar-anticipate-text" to="/">ANTICIPATE</Link>
            </div>
            {!user &&
                <div className="navbar-login-container">
                    <Link id="navbar-login-text" to="/login">Log in</Link>
                </div>
            }
            {user &&
                <>
                    <div className="navbar-login-container">
                        <Link id="navbar-login-text" to="/dashboard">Dashboard</Link>
                    </div>
                    <div className="navbar-login-container">
                        <Link id="navbar-login-text" to="/login" onClick={onLogout}>Log out</Link>
                    </div>
                </>
            }
        </div>
    )
}