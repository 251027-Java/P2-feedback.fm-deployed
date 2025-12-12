import { useState, type FormEvent } from 'react';
import { authAPI } from '../services/api';

function Login() {
  const [username, setUsername] = useState('');

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    // Handle login - API call will be implemented in step 8.6
    // const response = await authAPI.getAuthUrl();
    console.log('Login submitted for:', username);
  };

  return (
    <div>
      <h1>Login</h1>
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          placeholder="Username"
        />
        <button type="submit">Login</button>
      </form>
    </div>
  );
}

export default Login;
