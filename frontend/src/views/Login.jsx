import Navbar from "../components/Navbar";
import "./loginStyle.css"
import {Link} from 'react-router-dom';
import {useState} from "react";
import {signInWithEmailAndPassword} from 'firebase/auth';
import {auth} from "../firebase/Firebase.jsx";
import { useNavigate } from "react-router-dom";


export default function Login() {
    const [token, setToken] = useState(null);
    const [email, setEmail] = useState(null);
    const [password, setPassword] = useState(null);
    const navigate = useNavigate();
    const [error, setError] = useState(null);

    const onLogin = async (e) => {
        e.preventDefault();
        signInWithEmailAndPassword(auth, email, password)
            .then((response) => {
                navigate("/dashboard");
            })
            .catch((error) => {
                setError("Incorrect email or password. Please try again.");
                console.log(error);
            })
    }

    return (
        <>
            <Navbar/>
            <div className="login-parent-container">
                <form method="post" className="login-form" onSubmit={onLogin}>
                    <p className="login-form-heading">User Login</p>
                    <label htmlFor="email">Email</label>
                    <input type="text" id="username-input" name="email" placeholder="Email"
                           onChange={(e) => setEmail(e.target.value)}/>
                    <label htmlFor="password">Password</label>
                    <input type="password" id="password-input" placeholder="Password" name="password-input"
                           onChange={(e) => setPassword(e.target.value)}/>
                    <p className="login-forgot">Forgot your password?</p>
                    <p id="login-error-message">{error}</p>
                    <input type="submit" value="Log in" className="login-form-submit"/>
                    <p id="login-or-text">or</p>
                    <Link to="/signup" id="login-create-account">Create your account</Link>
                </form>
            </div>
        </>
    )
}