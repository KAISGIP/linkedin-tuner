import React, { useState } from 'react';
import './Login.css';

interface LoginProps {
    onLogin: (username: string, password: string) => void;
}

const Login: React.FC<LoginProps> = ({ onLogin }) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState<string | null>(null);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);

        try {
            const formData = new URLSearchParams();
            formData.append('username', username);
            formData.append('password', password);

            const response = await fetch('http://localhost:8080/api/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: formData,
                credentials: 'include',
            });

            if (response.ok) {
                onLogin(username, password);
            } else {
                setError('Identifiants incorrects. Veuillez utiliser :\n- Nom d\'utilisateur : user\n- Mot de passe : password');
            }
        } catch (error) {
            console.error('Erreur lors de la connexion:', error);
            setError('Erreur de connexion au serveur. Veuillez vérifier que le serveur est en cours d\'exécution.');
        }
    };

    return (
        <div className="login-container">
            <div className="login-box">
                <h2>Connexion</h2>
                {error && <div className="error-message">{error}</div>}
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="username">Nom d'utilisateur</label>
                        <input
                            type="text"
                            id="username"
                            name="username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            placeholder="user"
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="password">Mot de passe</label>
                        <input
                            type="password"
                            id="password"
                            name="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="password"
                            required
                        />
                    </div>
                    <button type="submit">Se connecter</button>
                </form>
            </div>
        </div>
    );
};

export default Login; 