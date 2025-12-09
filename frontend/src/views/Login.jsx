import Navbar from "../components/Navbar";
import "./loginStyle.css"
import { Link } from 'react-router-dom';

export default function Login() {
    const onLogin = async (e) => {
        e.preventDefault();

        const formData = new FormData();
        const data = Object.fromEntries(formData);

        const response = await fetch("http://localhost:8000/login", {
            method: "POST",
            headers: { "Content-Type": "application/json"},
            body: JSON.stringify(data),
        })
    }

    return (
        <>
            <Navbar/>
            <div className="login-parent-container">
                <form method="post" className="login-form">
                    <p className="login-form-heading">User Login</p>
                    <label htmlFor="username-input">Username</label>
                    <input type="text" id="username-input" name="username-input" placeholder="Username"/>
                    <label htmlFor="password-input">Password</label>
                    <input type="password" id="password-input" placeholder="Password" name="password-input"/>
                    <p className="login-forgot">Forgot your username or password?</p>
                    <input type="submit" value="Log in" className="login-form-submit"/>
                    <p id="login-or-text">or</p>
                    <Link to="/signup" id="login-create-account">Create your account</Link>
                </form>
            </div>
        </>
    )
}