import "./ErrorPageStyle.css";

export default function ErrorPage({error, resetErrorBoundary}) {

    return (
        <div id="error-page-container">
            <p id="error-page-content">{error.message}</p>
            <button id="error-page-try-again" className="glass" onClick={resetErrorBoundary}>Try Again</button>
        </div>
    )
};