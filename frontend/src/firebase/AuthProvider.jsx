import {useContext, createContext, useState, useEffect} from "react";
import {useNavigate} from "react-router-dom";
import {auth} from "./Firebase";
import {onAuthStateChanged} from 'firebase/auth';
import {setAuthToken} from '../api/axios.jsx';

const AuthContext = createContext();

export function AuthProvider({children}) {
    const [user, setUser] = useState(null);

    useEffect(() => {
        const unsubscribe = onAuthStateChanged(auth, async (user) => {
            setUser(user);

            if (user) {
                const token = await user.getIdToken();
                setAuthToken(token);
            } else {
                setAuthToken(null);
            }
        })

        return () => unsubscribe();
    }, [])

    return (
        <AuthContext.Provider value={{user}}>
            {children}
        </AuthContext.Provider>

    )
}

export default AuthProvider;
export const useAuth = () => useContext(AuthContext);