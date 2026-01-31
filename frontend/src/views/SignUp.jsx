import Navbar from "../components/Navbar";
import "./SignUp.css"
import {useNavigate} from 'react-router-dom'
import {useState} from "react";
import {validate as validateEmail} from 'email-validator'
import { registerUser } from "../api/services/UserRegistrationService"
import ReCAPTCHA from "react-google-recaptcha"

export default function SignUp() {
    const [errors, setErrors] = useState({});
    const navigate = useNavigate();

    const handleValidation = (form) => {
        const errors = {};

        if (!validateEmail(form.email)) {
            errors.email = "Invalid email format";
        }

        if (form.username.length < 8 || form.username.length > 20) {
            errors.username = "Username should be at least 8 characters long and 20 characters max";
        }

        if (form.password.length < 8 || form.password.length > 36) {
            errors.password = "Password should be atleast 8 characters long and 36 characters max";
        }

        if (form.confirmpassword !== form.password ) {
            errors.confirmpassword = "Password does not match";
        }

        setErrors(errors);
        return Object.keys(errors).length === 0
    }

    const handleSubmit = async (e) => {
        e.preventDefault();
        const formData = new FormData(e.target);
        const data = Object.fromEntries(formData);
        const isValid = handleValidation(data);

        if (isValid) {
            try {
                const response = registerUser(data);
                if (response.status === 200 || response.status === 201) {
                    navigate('/login');
                }
            } catch (error) {
                setErrors({ api: error.response?.data?.message });
            }
        }
    }

    return (
        <>
            <Navbar/>
            <div className="signup-parent-container">
                <form onSubmit={handleSubmit} className="signup-form">
                    <p className="signup-form-heading">Sign Up</p>
                    <label htmlFor="email-input">Email</label>
                    <input type="text" id="email-input" name="email" placeholder="Email"/>
                    <label htmlFor="username-input">Username</label>
                    <input type="text" id="username-input" name="username" placeholder="Username"/>
                    <label htmlFor="password-input">Password</label>
                    <input type="password" id="password-input" placeholder="Password" name="password"/>
                    <label htmlFor="username-input">Confirm Password</label>
                    <input type="password" name="confirmpassword" placeholder="Confirm password"/>
                    {Object.keys(errors).length > 0 && (
                        <div className="signup-error-box">
                            {Object.values(errors).map((msg, i) => (
                                <p key={i} className="signup-error">{msg}</p>
                            ))}
                        </div>
                    )}
                    <ReCAPTCHA
                    sitekey={process.env.REACT_APP_RECAPTCHA_SITE_KEY}
                    id="signup-recaptcha"
                    />
                    <input type="submit" value="Sign Up" id="signup-form-submit"/>
                </form>
            </div>
        </>
    )
}