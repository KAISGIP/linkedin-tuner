import React, { useState } from 'react';
import PostEditor from './components/PostEditor';
import Login from './components/Login';
import './App.css';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  const handleLogin = (username: string, password: string) => {
    setIsAuthenticated(true);
  };

  const handleLogout = () => {
    fetch('http://localhost:8080/logout', {
      method: 'POST',
      credentials: 'include',
    }).then(() => {
      setIsAuthenticated(false);
    });
  };

  return (
    <div className="App">
      <header className="App-header">
        <h1>LinkedIn Tuner</h1>
        <p>Créez et publiez vos posts LinkedIn avec l'aide de l'IA</p>
        {isAuthenticated && (
          <button onClick={handleLogout} className="logout-button">
            Se déconnecter
          </button>
        )}
      </header>
      <main>
        {isAuthenticated ? (
          <PostEditor />
        ) : (
          <Login onLogin={handleLogin} />
        )}
      </main>
    </div>
  );
}

export default App;
