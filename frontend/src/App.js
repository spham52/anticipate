import './App.css';
import {BrowserRouter as Router, Routes, Route} from 'react-router-dom';
import Root from "./views/Root.jsx";
import SignUp from "./views/SignUp.jsx";
import Login from "./views/Login.jsx";
import {AuthProvider} from "./firebase/AuthProvider";
import Dashboard from "./views/Dashboard";

function App() {
  return (
      <Router>
          <AuthProvider>
          <Routes>
              <Route path="/" element={<Root/>}></Route>
              <Route path="/login" element={<Login/>}></Route>
              <Route path="/signup" element={<SignUp/>}></Route>
              <Route path="/dashboard" element={<Dashboard/>}></Route>
          </Routes>
          </AuthProvider>
      </Router>
  );
}

export default App;
