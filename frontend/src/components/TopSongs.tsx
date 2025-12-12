import { useState, type ChangeEvent } from 'react';
import { songsAPI } from '../services/api';

function TopSongs() {
  const [timeRange, setTimeRange] = useState('medium_term');
  const [filterText, setFilterText] = useState('');

  const handleTimeRangeChange = (e: ChangeEvent<HTMLSelectElement>) => {
    setTimeRange(e.target.value);
  };

  const handleFilterChange = (e: ChangeEvent<HTMLInputElement>) => {
    setFilterText(e.target.value);
  };

  return (
    <div>
      <h1>Top Songs</h1>
      <div>
        <label>
          Time Range:
          <select value={timeRange} onChange={handleTimeRangeChange}>
            <option value="short_term">Last 4 weeks</option>
            <option value="medium_term">Last 6 months</option>
            <option value="long_term">All time</option>
          </select>
        </label>
        <input
          type="text"
          value={filterText}
          onChange={handleFilterChange}
          placeholder="Filter songs..."
        />
        {filterText && <p>Filtering by: {filterText}</p>}
      </div>
      <p>Top Songs component - to be implemented</p>
    </div>
  );
}

export default TopSongs;
