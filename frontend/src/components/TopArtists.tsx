import { useState } from 'react';

function TopArtists() {
  const [searchTerm, setSearchTerm] = useState('');

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(e.target.value);
  };

  return (
    <div>
      <h1>Top Artists</h1>
      <div>
        <input
          type="text"
          value={searchTerm}
          onChange={handleSearchChange}
          placeholder="Search artists..."
        />
        {searchTerm && <p>Searching for: {searchTerm}</p>}
      </div>
      <p>Top Artists component - to be implemented</p>
    </div>
  );
}

export default TopArtists;
