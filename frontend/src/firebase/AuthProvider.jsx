import {useContext, createContext, useState, useEffect} from "react";
import {useNavigate} from "react-router-dom";
import {auth} from "./Firebase";
import { onAuthStateChanged } from 'firebase/auth';

const AuthContext = createContext();

export function AuthProvider({children}) {
    const [user, setUser] = useState(null);

    useEffect(() => {
        const unsubscribe = onAuthStateChanged(auth, (user) => {
            setUser(user);
        })
    }, [])

    return (
        <AuthContext.Provider value={ {user} }>
            {children}
        </AuthContext.Provider>

    )
}

export default AuthProvider;
export const useAuth = () => useContext(AuthContext);