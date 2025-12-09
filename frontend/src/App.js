import './App.css';
import {BrowserRouter as Router, Routes, Route} from 'react-router-dom';
import Root from "./views/Root.jsx";
import SignUp from "./views/SignUp.jsx";
import Login from "./views/Login.jsx";

function App() {
  return (
      <Router>
          <Routes>
              <Route path="/" element={<Root/>}></Route>
              <Route path="/login" element={<Login/>}></Route>
              <Route path="/signup" element={<SignUp/>}></Route>
          </Routes>
      </Router>
  );
}

export default App;
