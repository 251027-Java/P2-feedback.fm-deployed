import { useState, type ChangeEvent } from 'react';
import { userAPI } from '../services/api';

function Dashboard() {
  const [refreshInterval, setRefreshInterval] = useState(30);

  const handleIntervalChange = (e: ChangeEvent<HTMLInputElement>) => {
    const value = parseInt(e.target.value, 10);
    if (!isNaN(value) && value > 0) {
      setRefreshInterval(value);
    }
  };

  return (
    <div>
      <h1>Dashboard</h1>
      <div>
        <label>
          Auto-refresh interval (seconds):
          <input
            type="number"
            value={refreshInterval}
            onChange={handleIntervalChange}
            min="10"
            max="300"
          />
        </label>
        <p>Dashboard will refresh every {refreshInterval} seconds</p>
      </div>
      <p>Dashboard component - to be implemented</p>
    </div>
  );
}

export default Dashboard;
