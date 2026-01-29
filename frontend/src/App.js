import './App.css';
import {BrowserRouter as Router, Routes, Route} from 'react-router-dom';
import Root from "./views/Root.jsx";
import SignUp from "./views/SignUp.jsx";
import Login from "./views/Login.jsx";
import {AuthProvider} from "./firebase/AuthProvider";
import Dashboard from "./views/Dashboard";
import {ErrorBoundary} from 'react-error-boundary';
import ErrorPage from "./components/ErrorPage.jsx"

function App() {
  return (
      <Router>
          <ErrorBoundary FallbackComponent={ErrorPage}>
          <AuthProvider>
          <Routes>
              <Route path="/" element={<Root/>}></Route>
              <Route path="/login" element={<Login/>}></Route>
              <Route path="/signup" element={<SignUp/>}></Route>
              <Route path="/dashboard" element={<Dashboard/>}></Route>
          </Routes>
          </AuthProvider>
          </ErrorBoundary>
      </Router>
  );
}

export default App;
